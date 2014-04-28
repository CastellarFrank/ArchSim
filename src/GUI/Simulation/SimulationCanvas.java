/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Simulation;

import Exceptions.ArchException;
import GUI.ContainerPanel;
import GUI.Edit.EditionDialog;
import GUI.MainWindow;
import GUI.MouseMode;
import GUI.NestedWatcher.CustomTreeModel;
import GUI.NestedWatcher.Debugger;
import GUI.Watcher.Selector.WatchesSelector;
import GUI.Watcher.WatchesTableModel;
import Simulation.Configuration;
import Simulation.Elements.BaseElement;
import Simulation.Elements.BasicSwitch;
import Simulation.Elements.ModuleChip;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class SimulationCanvas extends ContainerPanel implements
        MouseListener, MouseMotionListener, KeyListener {

    MainWindow parent;
    public String draggingClass;
    public String[] draggingExtraParams;
    public boolean deleting = false;
    public JPopupMenu pop;
    
    Debugger debugger;
    
    ArrayList<DrilldownWindow> drilldowns;
    
    public WatchesTableModel getWatchingVariables() {
        return watchesTableModel;
    }

    @SuppressWarnings("LeakingThisInConstructor")
    public SimulationCanvas(MainWindow parentP) {
        super();
        try {
            init();

            this.parent = parentP;
            
            pop = new JPopupMenu("Options");
            JMenuItem menu = new JMenuItem(new AbstractAction() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    String modId = ((ModuleChip) mouseComponent).getModuleInstanceId();
                    WatchesSelector selector = new WatchesSelector(parent, debugger, watchesTableModel, 
                            simulationScope, modId, parent, true, (ModuleChip) mouseComponent);
                    selector.setVisible(true);
                }
            });
            menu.setText("Add Watch");
            
            JMenuItem viewMenu = new JMenuItem(new AbstractAction() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (mouseComponent != null && mouseComponent instanceof ModuleChip) {
                        ModuleChip chip = (ModuleChip) mouseComponent;
                        DrilldownWindow window = new DrilldownWindow(chip.getModuleName());
                        drilldowns.add(window);
                        parent.addDrilldownWindow(window);
                    }                    
                }
            });
            
            viewMenu.setText("View code");                        
            
            pop.add(menu);
            pop.add(viewMenu);
            
            this.add(pop);

            addMouseListener(this);
            addMouseMotionListener(this);
            addKeyListener(this);
            
            drilldowns = new ArrayList<DrilldownWindow>();
        } catch (ArchException ex) {
            Logger.getLogger(SimulationCanvas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void prepareForReanalysis() {
        needsAnalysis = true;
        repaint();
    }

    public void checkForSwitchClicked() {
        if (mouseComponent instanceof BasicSwitch) {
            BasicSwitch component = (BasicSwitch) mouseComponent;
            component.toggle();

            prepareForReanalysis();
        }
    }

    public void setDrawingCursor() {
        this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            if (mouseComponent == null) {
                return;
            }
            //if (mouseComponent instanceof ModuleChip) {
                /*ModuleChip chip = (ModuleChip) mouseComponent;
                DrilldownWindow window = new DrilldownWindow(chip.getModuleName());
                drilldowns.add(window);
                parent.addDrilldownWindow(window);*/
            //} else {
                if (mouseComponent.getEditInfo(0) != null) {
                    EditionDialog dialog = new EditionDialog(mouseComponent, parent, true);
                    dialog.setVisible(true);
                }
            //}
        } else {
            if (draggingClass != null) {
                int x = e.getX(), y = e.getY();
                constructAndAddElement(draggingClass, x, y, x + 50, y, draggingExtraParams);
                draggingClass = null;
                draggingExtraParams = null;
                prepareForReanalysis();
            }
        }

        if (e.getClickCount() == 1 && deleting && mouseComponent != null) {
            if (mouseComponent instanceof ModuleChip) {
                simulationScope.unregister(((ModuleChip)mouseComponent).getModuleInstanceId());
            }
            this.elements.remove(mouseComponent);
            mouseComponent = null;
            prepareForReanalysis();
        }

        if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
            if (currentMouseMode == MouseMode.SELECT
                    || currentMouseMode == MouseMode.SELECT_DRAG) {
                clearSelection();
                repaint();
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
        if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
            if (mouseComponent != null && mouseComponent instanceof ModuleChip) {                
                pop.show(e.getComponent(), e.getX(), e.getY());  
                if (watchesTableModel == null)
                    watchesTableModel = new WatchesTableModel(simulationScope);
                if (debugger == null) {
                    debuggerModel = new CustomTreeModel(simulationScope, watchesTableModel.getModelData());
                    debugger = new Debugger(this);
                }
                return;
            }
        }

        //<editor-fold defaultstate="collapsed" desc="Selecting area">
        initDragX = e.getX();
        initDragY = e.getY();
        //</editor-fold>

        int x = e.getX(), y = e.getY();
        if (draggingClass != null) {
            if (draggingClass.equals(ModuleChip.class.getName())) {
                newElementBeenDrawn = constructElement(draggingClass, x, y, x, y, draggingExtraParams);
            } else {
                newElementBeenDrawn = constructElement(draggingClass, x, y);
            }
        }

        checkForSwitchClicked();
        dragging = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        dragging = false;
        currentMouseMode = defaultMouseMode;
        draggingClass = null;
        selectedArea = null;
        if (newElementBeenDrawn != null) {
            addElement(newElementBeenDrawn);
            prepareForReanalysis();
            newElementBeenDrawn = null;
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }

        repaint();
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
                    selectElementsOnArea(e.getX(), e.getY());
                } else if (newElementBeenDrawn == null){
                    currentMouseMode = MouseMode.SELECT_DRAG;
                    success = dragSelected(e.getX(), e.getY());
                }
                break;
            case SELECT_DRAG:
                success = dragSelected(e.getX(), e.getY());
                break;
            case POST_DRAG:
                if (mouseComponent != null && !(mouseComponent instanceof ModuleChip)) {
                    dragPost(snapGrid(e.getX()), snapGrid(e.getY()));
                }
                break;
            case MOVE_ALL:
                moveAll(snapGrid(e.getX()), snapGrid(e.getY()));
                break;
        }

        if (newElementBeenDrawn != null ) {
            deleting = false;
            if (!(newElementBeenDrawn instanceof ModuleChip))
                newElementBeenDrawn.movePoint2(snapGrid(e.getX()), snapGrid(e.getY()));
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
        int bestDist = Integer.MAX_VALUE;
        int bestArea = Integer.MAX_VALUE;
        int x = e.getX();
        int y = e.getY();
        dragX = snapGrid(x);
        dragY = snapGrid(y);
        draggingPost = -1;
        for (int i = 0; i < elements.size(); i++) {
            BaseElement baseElement = getElement(i);

            if (baseElement.boundingBox.contains(x, y)) {
                if (Configuration.DEBUG_MODE) {
                    //System.out.println("Mouse collides with box: " + baseElement.boundingBox);
                }

                int j;
                int area = baseElement.boundingBox.width * baseElement.boundingBox.height;
                int jn = baseElement.getPostCount();
                if (jn > 2) {
                    jn = 2;
                }
                for (j = 0; j != jn; j++) {
                    Point pt = baseElement.getPost(j);
                    int dist = distance(x, y, pt.x, pt.y);
                    if (dist <= bestDist && area <= bestArea) {
                        bestDist = dist;
                        bestArea = area;
                        mouseComponent = baseElement;
                    }
                }
                if (baseElement.getPostCount() == 0) {
                    mouseComponent = baseElement;
                }
            }
        }
        if (mouseComponent != previousMouse) {
            repaint();
        }
        if(mouseComponent != null && mouseComponent instanceof ModuleChip) {
            ModuleChip chip = (ModuleChip) mouseComponent;
            setToolTipText(chip.getPortsInformation());
            
        } else {
            setToolTipText(null);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (deleting && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            deleting = !deleting;
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        } else if (!deleting && e.getKeyCode() == KeyEvent.VK_DELETE) {
            boolean anyRemoved = false;
            for (int i = 0; i < elements.size(); i++) {
                if (elements.get(i).isSelected()) {
                    BaseElement element = elements.get(i);
                    if (element instanceof ModuleChip) {
                        simulationScope.unregister(((ModuleChip)element).getModuleInstanceId());
                    }
                    elements.remove(i);
                    anyRemoved = true;
                    i = i -1;
                }
            }
            if (anyRemoved) { 
                prepareForAnalysis();
                return; 
            }
            deleting = !deleting;
            this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void updatePreview(Graphics g) {
        super.updatePreview(g);
        //if (watcherWindow != null && !watcherWindow.isSelected()) 
        //    watcherWindow.refreshModel();
        if (debugger != null && !debugger.isSelected())
            debugger.refresh();
        
    }
    
    public void destroyAll() {
        //if (watcherWindow != null)
        //    watcherWindow.dispose();
        if (debugger != null)
            debugger.dispose();
        for (DrilldownWindow window : drilldowns) {
            window.dispose();
        }
    }
}
