/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Simulation.Configuration;
import Simulation.Elements.BaseElement;
import Simulation.Joint;
import Simulation.JointReference;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Constructor;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class ContainerPanel extends JCanvas {
    //<editor-fold defaultstate="collapsed" desc="Attributes">

    protected Vector<BaseElement> elements;
    protected Vector<Joint> joints;
    public BaseElement newElementBeenDrawn, menuElm, mouseComponent, stopElm;
    public boolean needsAnalysis = false, isPaused = false;
    protected boolean dragging = false;
    protected int dragX, dragY, initDragX, initDragY;
    protected int gridSize, gridMask, gridRound;
    protected int draggingPost = -1;
    protected Rectangle selectedArea = null;
    protected MouseMode defaultMouseMode = MouseMode.SELECT, currentMouseMode = MouseMode.SELECT;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Methods">
    public BaseElement getElement(int i) {
        return this.elements.get(i);
    }

    public Joint getJoint(int index) {
        if (index >= joints.size()) {
            return null;
        }
        return joints.elementAt(index);
    }

    public int distance(int x1, int y1, int x2, int y2) {
        x2 -= x1;
        y2 -= y1;
        return x2 * x2 + y2 * y2;
    }

    public void init() {
        setGrid();
    }
    
    public void selectionOnArea(int x, int y) {
        int x1 = Math.min(x, initDragX);
	int x2 = Math.max(x, initDragX);
	int y1 = Math.min(y, initDragY);
	int y2 = Math.max(y, initDragY);
	selectedArea = new Rectangle(x1, y1, x2-x1, y2-y1);
	int i;
	for (i = 0; i != elements.size(); i++) {
	    BaseElement element = elements.elementAt(i);
	    element.selectRect(selectedArea);
	}
    }
    
    public void clearSelection() {
        for (BaseElement baseElement : elements) {
            baseElement.setSelected(false);
        }
        selectedArea = null;
    }

    public Element toXmlRootElement(Document document) {
        Element root = document.createElement("elements");
        for (BaseElement element : elements) {
            root.appendChild(element.getXmlElement(document));
        }
        return root;
    }

    public BaseElement constructElement(String type, int x, int y) {
        Class[] proto = new Class[2];
        proto[0] = proto[1] = int.class;

        Class baseElementClass;
        try {
            baseElementClass = Class.forName(type);
            Constructor constructor = baseElementClass.getConstructor(proto);

            Object[] params = new Object[2];
            params[0] = snapGrid(x);
            params[1] = snapGrid(y);

            BaseElement element = (BaseElement) constructor.newInstance(params);
            element.containerPanel = this;
            return element;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ContainerPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(ContainerPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ContainerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public BaseElement constructElement(String type,
            int x, int y, int x2, int y2, String[] extraParams) {
        Class[] proto = new Class[5];
        proto[0] = proto[1] = proto[2] = proto[3] = int.class;
        proto[4] = String[].class;

        Class baseElementClass;
        try {
            baseElementClass = Class.forName(type);
            Constructor constructor = baseElementClass.getConstructor(proto);

            Object[] params = new Object[5];
            params[0] = x;
            params[1] = y;
            params[2] = x2;
            params[3] = y2;
            params[4] = extraParams;

            BaseElement element = (BaseElement) constructor.newInstance(params);
            element.containerPanel = this;
            return element;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ContainerPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(ContainerPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ContainerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void constructAndAddElement(String type,
            int x, int y, int x2, int y2, String[] extraParams) {
        addElement(constructElement(type, x, y, x2, y2, extraParams));
    }
    //</editor-fold>

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        updatePreview(g);
    }

    protected  void analyze() {
        if (elements.isEmpty()) {
            return;
        }

        joints.clear();

        int elementIndex, postIndex, jointIndex;
        for (elementIndex = 0; elementIndex < elements.size(); elementIndex++) {
            BaseElement baseElement = getElement(elementIndex);
            int postCount = baseElement.getPostCount();
            for (postIndex = 0; postIndex < postCount; postIndex++) {
                Point post = baseElement.getPost(postIndex);

                for (jointIndex = 0; jointIndex < joints.size(); jointIndex++) {
                    Joint joint = getJoint(jointIndex);
                    if (post.x == joint.coordX && post.y == joint.coordY) {
                        break;
                    }
                }
                int outOfRange = joints.size();
                JointReference jointRef = new JointReference(baseElement, postIndex);
                if (jointIndex == outOfRange) {
                    /*Add new joint*/
                    Joint newJoint = new Joint(post.x, post.y);
                    newJoint.references.add(jointRef);

                    baseElement.setJointIndex(postIndex, jointIndex);
                    joints.add(newJoint);
                } else {
                    /*Add joint reference to existing joint*/
                    Joint joint = getJoint(jointIndex);
                    joint.references.add(jointRef);

                    baseElement.setJointIndex(postIndex, jointIndex);
                }
            }
        }
    }

    public void runStep() {
        if (elements.isEmpty()) return;
        for (BaseElement baseElement : elements) {
            //baseElement.doStep();
        }
    }
    
    public void resume() {
        isPaused = false;
    }
    
    public void pause() {
        isPaused = true;
    }
    
    public void updatePreview(Graphics g) {
        if (needsAnalysis) {
            analyze();
            needsAnalysis = false;
        }
        
        if (!isPaused)
            runStep();

        for (BaseElement baseElement : elements) {
            baseElement.draw(g);
        }

        //<editor-fold defaultstate="collapsed" desc="Color bad connections">
        for (int jointIndex = 0; jointIndex < joints.size(); jointIndex++) {
            Joint joint = joints.elementAt(jointIndex);
            if (joint.references.size() == 1) {
                boolean collidesWith = false;
                JointReference reference = joint.references.elementAt(0);
                for (BaseElement baseElement : elements) {
                    if (baseElement != reference.element
                            && baseElement.collidesWith(joint.coordX, joint.coordY)) {
                        collidesWith = true;
                    }
                }

                if (collidesWith) {
                    Color old = g.getColor();
                    g.setColor(Color.RED);
                    g.fillOval(joint.coordX - 3, joint.coordY - 3, 7, 7);
                    g.setColor(old);
                }
            }
        }
        //</editor-fold>   

        if (newElementBeenDrawn != null
                && (newElementBeenDrawn.x != newElementBeenDrawn.x2 || newElementBeenDrawn.y != newElementBeenDrawn.y2)) {
            newElementBeenDrawn.draw(g);
        } else if (selectedArea != null) {
            Color old = g.getColor();
            g.setColor(Color.YELLOW.darker());
            g.drawRect(selectedArea.x, selectedArea.y, 
                    selectedArea.width, selectedArea.height);
            
            g.setColor(old);
        }
    }

    public void addElement(BaseElement element) {
        if (element == null) {
            return;
        }
        if (element.containerPanel == null) {
            element.containerPanel = this;
        }
        element.x = snapGrid(element.x);
        element.y = snapGrid(element.y);
        element.x2 = snapGrid(element.x2);
        element.y2 = snapGrid(element.y2);
        element.setPoints();

        elements.add(element);
    }
    
    public void removeAll() {
        elements.clear();
    }

    public ContainerPanel() {
        elements = new Vector<BaseElement>(50);
        joints = new Vector<Joint>(100);

        BaseElement.defaultColor = Color.BLACK;
        BaseElement.whiteColor = Color.WHITE;
        BaseElement.selectedColor = new Color(255, 128, 0);
        BaseElement.lowSignalColor = Color.DARK_GRAY;
        BaseElement.highSignalColor = Color.GREEN.darker();
        BaseElement.highImpedanceSignalColor = Color.RED;
        BaseElement.unknownSignalColor = Color.BLUE;
        BaseElement.textColor = Color.DARK_GRAY;

        needsAnalysis = true;
    }

    protected void setGrid() {
        gridSize = Configuration.SMALL_GRID ? 8 : 16;
        gridMask = ~(gridSize - 1);
        gridRound = gridSize / 2 - 1;
    }

    public int snapGrid(int x) {
        return (x + gridRound) & gridMask;
    }

    public void dragPost(int x, int y) {
        if (draggingPost == -1) {
            int distance1 = distance(mouseComponent.x, mouseComponent.y, x, y);
            int distance2 = distance(mouseComponent.x2, mouseComponent.y2, x, y);
            draggingPost =
                    (distance1 > distance2) ? 1 : 0;
        }
        int dx = x - dragX;
        int dy = y - dragY;
        if (dx == 0 && dy == 0) {
            return;
        }
        mouseComponent.movePoint(draggingPost, dx, dy);
        prepareForAnalysis();
        repaint();
    }

    public boolean dragSelected(int x, int y) {
        boolean me = false;
        if (mouseComponent != null && !mouseComponent.isSelected()) {
            mouseComponent.setSelected(me = true);
        }

        // snap grid, unless we're only dragging text elements
        int i;
        for (i = 0; i != elements.size(); i++) {
            BaseElement ce = getElement(i);
            if (ce.isSelected()) {
                break;
            }
        }
        if (i != elements.size()) {
            x = snapGrid(x);
            y = snapGrid(y);
        }

        int dx = x - dragX;
        int dy = y - dragY;
        if (dx == 0 && dy == 0) {
            // don't leave mouseComponent selected if we selected it above
            if (me) {
                mouseComponent.setSelected(false);
            }
            return false;
        }
        boolean allowed = true;

        // check if moves are allowed
        for (i = 0; allowed && i != elements.size(); i++) {
            BaseElement ce = getElement(i);
            if (ce.isSelected() && !ce.allowMove(dx, dy)) {
                allowed = false;
            }
        }

        if (allowed) {
            for (i = 0; i != elements.size(); i++) {
                BaseElement ce = getElement(i);
                if (ce.isSelected()) {
                    ce.move(dx, dy);
                }
            }
            prepareForAnalysis();
        }

        // don't leave mouseComponent selected if we selected it above
        if (me) {
            mouseComponent.setSelected(false);
        }

        return allowed;
    }

    public void moveAll(int x, int y) {
        int dx = x - dragX;
        int dy = y - dragY;

        if (dx == 0 && dy == 0) {
            return;
        }

        for (BaseElement baseElement : elements) {
            baseElement.move(dx, dy);
        }
    }

    public void prepareForAnalysis() {
        needsAnalysis = true;
        repaint();
    }
    
    public boolean containsElements() {
        return !elements.isEmpty();
    }
}
