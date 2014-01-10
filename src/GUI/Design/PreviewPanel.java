/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Design;

import Exceptions.ArchException;
import GUI.ContainerPanel;
import GUI.MainWindow;
import GUI.MouseMode;
import GUI.Simulation.DrilldownWindow;
import Simulation.Configuration;
import Simulation.Elements.BaseElement;
import Simulation.Elements.Gates.AndGate;
import Simulation.Elements.ModuleChip;
import Simulation.Elements.Wire;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class PreviewPanel extends ContainerPanel implements MouseListener, MouseMotionListener {

    MainWindow parent;

    @SuppressWarnings("LeakingThisInConstructor")
    public PreviewPanel(MainWindow parent) {
        super();
        init();

        this.parent = parent;

        addMouseListener(this);
        addMouseMotionListener(this);
        
        Wire wire;
        try {
            wire = new Wire(20, 20, 100, 100, null);
            addElement(wire);
            
            AndGate gate = new AndGate(30, 30, 100, 100, null);
            addElement(gate);
            
            ModuleChip chip = new ModuleChip(48, 192, 48, 300, new String[] { "Full_Adder" });
            addElement(chip);
        } catch (ArchException ex) {
            Logger.getLogger(PreviewPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            if (mouseComponent instanceof ModuleChip) {
                ModuleChip chip = (ModuleChip) mouseComponent;
                DrilldownWindow window = new DrilldownWindow(chip.getModuleInfo());
                parent.addDrilldownWindow(window);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int exModifiers = e.getModifiersEx();
        if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
            currentMouseMode = defaultMouseMode;
            if ((exModifiers & (MouseEvent.CTRL_DOWN_MASK
                    | MouseEvent.META_DOWN_MASK)) != 0) {
                currentMouseMode = MouseMode.POST_DRAG;
            } else if ((exModifiers & MouseEvent.ALT_DOWN_MASK) != 0) {
                currentMouseMode = MouseMode.MOVE_ALL;
            }
        }
        dragging = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        dragging = false;
        currentMouseMode = defaultMouseMode;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        boolean success = true;
        switch (currentMouseMode) {
            case SELECT:
                if (mouseComponent == null) {
                } else {
                    currentMouseMode = MouseMode.SELECT_DRAG;
                    success = dragSelected(e.getX(), e.getY());
                }
                break;
            case SELECT_DRAG:
                success = dragSelected(e.getX(), e.getY());
                break;
            case POST_DRAG:
                if (mouseComponent != null) {
                    dragPost(snapGrid(e.getX()), snapGrid(e.getY()));
                }
                break;
            case MOVE_ALL:
                moveAll(snapGrid(e.getX()), snapGrid(e.getY()));
                break;
        }

        dragging = true;
        if (success) {
            if (currentMouseMode == MouseMode.SELECT_DRAG && false) {
                dragX = e.getX();
                dragY = e.getY();
            } else {
                dragX = snapGrid(e.getX());
                dragY = snapGrid(e.getY());
            }
        }

        repaint(Configuration.REPAINT_PAUSE);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        BaseElement previousMouse = mouseComponent;
        mouseComponent = null;
        int bestDist = 100000;
        int bestArea = 100000;
        int x = e.getX();
        int y = e.getY();
        dragX = snapGrid(x);
        dragY = snapGrid(y);
        draggingPost = -1;
        for (int i = 0; i < elements.size(); i++) {
            BaseElement ce = getElement(i);
            if (ce.boundingBox.contains(x, y)) {
                int j;
                int area = ce.boundingBox.width * ce.boundingBox.height;
                int jn = ce.getPostCount();
                if (jn > 2) {
                    jn = 2;
                }
                for (j = 0; j != jn; j++) {
                    Point pt = ce.getPost(j);
                    int dist = distance(x, y, pt.x, pt.y);
                    if (dist <= bestDist && area <= bestArea) {
                        bestDist = dist;
                        bestArea = area;
                        mouseComponent = ce;
                    }
                }
                if (ce.getPostCount() == 0) {
                    mouseComponent = ce;
                }
            }
        }
        if (mouseComponent != previousMouse) {
            repaint();
        }
    }
}
