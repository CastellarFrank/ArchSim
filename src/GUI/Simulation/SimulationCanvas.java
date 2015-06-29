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
import Simulation.Elements.Inputs.ClockInput;
import Simulation.Elements.ModuleChip;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import static java.awt.datatransfer.DataFlavor.stringFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.TransferHandler;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class SimulationCanvas extends ContainerPanel implements
        MouseListener, MouseMotionListener, KeyListener {

    public MainWindow parent;
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
                    ModuleChip chip = (ModuleChip) mouseComponent;
                    if (chip != null) {
                        String modId = chip.getModuleInstanceId();
                        WatchesSelector selector = new WatchesSelector(parent, debugger, watchesTableModel, 
                                simulationScope, modId, parent, true, (ModuleChip) mouseComponent);
                        selector.setVisible(true);
                    }
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
            this.initializeDebuggerIfNull();
        } catch (ArchException ex) {
            Logger.getLogger(SimulationCanvas.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.setTransferHandler(new TransferHandler(){
            @Override
            public boolean canImport(TransferHandler.TransferSupport support) {
                if(!support.isDataFlavorSupported(stringFlavor))
                    return false;
                
                Transferable t = support.getTransferable();
                String data = null;
                try {
                    data = t.getTransferData(stringFlavor).toString();
                } catch (UnsupportedFlavorException ex) {
                    Logger.getLogger(SimulationCanvas.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(SimulationCanvas.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                return data != null && data.startsWith(":<dP0t4t0:3");
            }

            @Override
            public boolean importData(TransferHandler.TransferSupport support) {
                if(!canImport(support))
                    return false;
                
                Transferable t = support.getTransferable();
                String data = null;
                try {
                    data = t.getTransferData(stringFlavor).toString();
                } catch (UnsupportedFlavorException ex) {
                    Logger.getLogger(SimulationCanvas.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(SimulationCanvas.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                if(data == null || data.isEmpty())
                    return false;
                
                data = data.replaceFirst(":<dP0t4t0:3", "");
                
                String[] elements = data.split(",");
                String [] extraParam = null;
                if(elements.length > 1)
                    extraParam = new String[]{ elements[1] };
                
                draggingClass = elements[0];
                draggingExtraParams = extraParam;
                setDrawingCursor();
                return true;
            }
        });
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
                    dialog.setSimulationCanvasElement(this);
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
            }else if(mouseComponent instanceof ClockInput){
                this.clockEventManagement.removeClock((ClockInput)mouseComponent);
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
                initializeDebuggerIfNull();
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
                if(newElementBeenDrawn != null && newElementBeenDrawn instanceof ClockInput)
                    this.clockEventManagement.removeClock((ClockInput)newElementBeenDrawn);
                newElementBeenDrawn = constructElement(draggingClass, x, y);
            }
        }

        checkForSwitchClicked();
        dragging = true;
    }

    private void initializeDebuggerIfNull() {
        if (watchesTableModel == null)
            watchesTableModel = new WatchesTableModel(simulationScope);
        if (debugger == null) {
            debuggerModel = new CustomTreeModel(simulationScope, watchesTableModel.getModelData());
            debugger = new Debugger(this);
            debugger.setVisible(false);
            this.parent.addDebuggerWindow(debugger);
        }
        
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
                if (mouseComponent == null && newElementBeenDrawn == null) {
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
                this.needsJointsAnalyze = true;
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

            if (baseElement.collidesWith(x, y)) {
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
                    }else if(element instanceof ClockInput){
                        this.clockEventManagement.removeClock((ClockInput)element);
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
        boolean needsRefresh = this.needsAnalysis || parent.debuggerRefresh;
        super.updatePreview(g);
        
        if (debugger != null && needsRefresh)
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
