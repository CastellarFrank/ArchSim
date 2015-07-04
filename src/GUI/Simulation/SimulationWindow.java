/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Simulation;

import GUI.Design.DesignWindow;
import GUI.MainWindow;
import GUI.MenuInfo;
import Simulation.Elements.Gates.AndGate;
import Simulation.Elements.Gates.NandGate;
import Simulation.Elements.Gates.NorGate;
import Simulation.Elements.Gates.NotGate;
import Simulation.Elements.Gates.OrGate;
import Simulation.Elements.Gates.XnorGate;
import Simulation.Elements.Gates.XorGate;
import Simulation.Elements.Inputs.ClockInput;
import Simulation.Elements.Inputs.LogicInput;
import Simulation.Elements.Inputs.MultiBitsInput;
import Simulation.Elements.ModuleChip;
import Simulation.Elements.Outputs.LogicOutput;
import Simulation.Elements.Outputs.MultiBitsOutput;
import Simulation.Elements.Wire;
import Utils.FileUtils;
import Utils.TextUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class SimulationWindow extends javax.swing.JInternalFrame implements ActionListener {
    SimulationCanvas canvas;
    String[] extraParams2 = new String[] { "2" };    
    public final String startExecutionText = "Start Execution";
    public final String continueExecutionText = "Continue";
    public final String pauseExecutionText = "Pause";
    MainWindow parentWindow;
    
    /**
     * Creates new form SimulationWindow
     */
    public SimulationWindow(MainWindow parent) {
        super("Component Simulation Window", true, true, true, true);
        this.parentWindow = parent;
        initComponents();
        
        //<editor-fold defaultstate="collapsed" desc="Set look and feel">
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        if (parent.needsRefresh) {
            parent.refreshModules();
        }
                
        canvas = new SimulationCanvas(parent);
        this.setContentPane(canvas);
        
        this.setLocation((parent.getWidth() - getWidth()) / 2, 
                (parent.getHeight() - getHeight()) / 2);
        
        if (canvas.isPaused) {
            pauseContinueMenu.setText(this.startExecutionText);
        }
        
        this.refreshSimulationMenuStructure();
    }
    
    public void refreshSimulationMenuStructure() {
        ArrayList<MenuInfo> menus = this.parentWindow.moduleMenus;
        modulesMenu.removeAll();
        for (MenuInfo menu : menus) {
            if (menu.isMenuItem) {
                modulesMenu.add(makeMenuItem(menu.label));
            } else {
                modulesMenu.add(makeMenu(menu));
            }
        }
    }
    
    public void addFilenameToTitle(String fileName) {
        setTitle(getTitle() + " - " + fileName);
    }
    
    public final JMenuItem makeMenuItem(String moduleName) {
        JMenuItem menu = new JMenuItem(moduleName + " module.");
        menu.setActionCommand(moduleName);
        menu.addActionListener(this);
        
        return menu;
    }
    
    public final JMenu makeMenu(MenuInfo info) {
        JMenu menu = new JMenu(info.label);
        
        for (MenuInfo child : info.children) {
            menu.add(makeMenuItem(child.label));
        }
        
        return menu;
    }
    
    public void loadFromDocument(Document document) {
        NodeList elements = document.getElementsByTagName("element");
        
        for (int i = 0; i < elements.getLength(); i++) {
            Element element = (Element) elements.item(i);
            NodeList extraParams = element.getElementsByTagName("param");
            String[] stringExtraParams = new String[extraParams.getLength()];
            
            for (int paramId = 0; paramId < extraParams.getLength(); paramId ++) {
                Node param = extraParams.item(paramId);
                stringExtraParams[paramId] = param.getTextContent();
            }
            
            String type = element.getAttribute("type");
            int x = Integer.parseInt(element.getAttribute("x"));
            int y = Integer.parseInt(element.getAttribute("y"));
            int x2 = Integer.parseInt(element.getAttribute("x2"));
            int y2 = Integer.parseInt(element.getAttribute("y2"));
            
            canvas.constructAndAddElement(type, x, y, x2, y2, stringExtraParams);
        }
    }
    
    private void setAddingElementInfo(String className, String[] params) {
        canvas.draggingClass = className;
        canvas.draggingExtraParams = params;
        canvas.setDrawingCursor();
    }
    
    private void saveComponent() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int choose = fileChooser.showSaveDialog(this);
        
        if (choose != JFileChooser.APPROVE_OPTION)
            return;
        File target = fileChooser.getSelectedFile();
        File newTarget = new File(target.getParent() + File.separator + TextUtils.AddSimulationTypeFileExtension(target.getName()));
        FileUtils.DeleteFile(target);
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document document = docBuilder.newDocument();

            Element root = document.createElement("module");
            document.appendChild(root);
            Element elements = this.canvas.toXmlRootElement(document);
            root.appendChild(elements);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult result = new StreamResult(newTarget);

            transformer.transform(domSource, result);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(DesignWindow.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }
    
    public boolean closeLogic() {    
        if (canvas.containsElements()) {
            int result = JOptionPane.showConfirmDialog(this, "Would you like to save before exit?",
                    "Exiting", JOptionPane.YES_OPTION);
            if (result == -1)
                return false;
            this.parentWindow.removeSimulationWindow(this);
            if (result == JOptionPane.YES_OPTION) {
                saveComponent();
                canvas.destroyAll();
                dispose();
            } else if (result == JOptionPane.NO_OPTION) {
                canvas.destroyAll();
                dispose();
            }
        } else {
            this.parentWindow.removeSimulationWindow(this);
            canvas.destroyAll();
            dispose();
        }
        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        saveMenu = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        closeMenu = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        addAndMenu = new javax.swing.JMenuItem();
        addNandMenu = new javax.swing.JMenuItem();
        addNorMenu = new javax.swing.JMenuItem();
        addOrMenu = new javax.swing.JMenuItem();
        addXnorMenu = new javax.swing.JMenuItem();
        addXorMenu = new javax.swing.JMenuItem();
        inverterMenu = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        lInputMenu = new javax.swing.JMenuItem();
        multiBitsMenu = new javax.swing.JMenuItem();
        clockInputMenu = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        lOuputMenu = new javax.swing.JMenuItem();
        multiBitsOutputMenu = new javax.swing.JMenuItem();
        modulesMenu = new javax.swing.JMenu();
        addWireMenu = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        pauseContinueMenu = new javax.swing.JMenuItem();
        windowMenu = new javax.swing.JMenu();
        debuggerMenu = new javax.swing.JMenuItem();

        setBackground(new java.awt.Color(255, 255, 255));
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        jMenu1.setText("File");

        saveMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveMenu.setText("Save");
        saveMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuActionPerformed(evt);
            }
        });
        jMenu1.add(saveMenu);
        jMenu1.add(jSeparator1);

        closeMenu.setText("Close");
        closeMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeMenuActionPerformed(evt);
            }
        });
        jMenu1.add(closeMenu);

        jMenuBar1.add(jMenu1);

        jMenu3.setText("Design");

        jMenu4.setText("Add Gate");

        addAndMenu.setText("And");
        addAndMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAndMenuActionPerformed(evt);
            }
        });
        jMenu4.add(addAndMenu);

        addNandMenu.setText("Nand");
        addNandMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNandMenuActionPerformed(evt);
            }
        });
        jMenu4.add(addNandMenu);

        addNorMenu.setText("Nor");
        addNorMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNorMenuActionPerformed(evt);
            }
        });
        jMenu4.add(addNorMenu);

        addOrMenu.setText("Or");
        addOrMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addOrMenuActionPerformed(evt);
            }
        });
        jMenu4.add(addOrMenu);

        addXnorMenu.setText("Xnor");
        addXnorMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addXnorMenuActionPerformed(evt);
            }
        });
        jMenu4.add(addXnorMenu);

        addXorMenu.setText("Xor");
        addXorMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addXorMenuActionPerformed(evt);
            }
        });
        jMenu4.add(addXorMenu);

        jMenu3.add(jMenu4);

        inverterMenu.setText("Add Inverter");
        inverterMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inverterMenuActionPerformed(evt);
            }
        });
        jMenu3.add(inverterMenu);

        jMenu5.setText("Add Input");

        lInputMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1, java.awt.event.InputEvent.CTRL_MASK));
        lInputMenu.setText("Logic Input");
        lInputMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lInputMenuActionPerformed(evt);
            }
        });
        jMenu5.add(lInputMenu);

        multiBitsMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2, java.awt.event.InputEvent.CTRL_MASK));
        multiBitsMenu.setText("MultiBits Input");
        multiBitsMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                multiBitsMenuActionPerformed(evt);
            }
        });
        jMenu5.add(multiBitsMenu);

        clockInputMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_3, java.awt.event.InputEvent.CTRL_MASK));
        clockInputMenu.setText("Clock Input");
        clockInputMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clockInputMenuActionPerformed(evt);
            }
        });
        jMenu5.add(clockInputMenu);

        jMenu3.add(jMenu5);

        jMenu6.setText("Add Output");

        lOuputMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        lOuputMenu.setText("Logic Output");
        lOuputMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lOuputMenuActionPerformed(evt);
            }
        });
        jMenu6.add(lOuputMenu);

        multiBitsOutputMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        multiBitsOutputMenu.setText("MultiBits Output");
        multiBitsOutputMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                multiBitsOutputMenuActionPerformed(evt);
            }
        });
        jMenu6.add(multiBitsOutputMenu);

        jMenu3.add(jMenu6);

        modulesMenu.setText("Add Module");
        jMenu3.add(modulesMenu);

        addWireMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        addWireMenu.setText("Add Wire");
        addWireMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addWireMenuActionPerformed(evt);
            }
        });
        jMenu3.add(addWireMenu);

        jMenuBar1.add(jMenu3);

        jMenu2.setText("Execution");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        jMenuItem1.setText("Restart");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        pauseContinueMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        pauseContinueMenu.setText("Pause");
        pauseContinueMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseContinueMenuActionPerformed(evt);
            }
        });
        jMenu2.add(pauseContinueMenu);

        jMenuBar1.add(jMenu2);

        windowMenu.setText("Window");

        debuggerMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        debuggerMenu.setText("Show Debugger");
        debuggerMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                debuggerMenuActionPerformed(evt);
            }
        });
        windowMenu.add(debuggerMenu);

        jMenuBar1.add(windowMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 595, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 476, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addAndMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAndMenuActionPerformed
        setAddingElementInfo(AndGate.class.getName(), extraParams2);
    }//GEN-LAST:event_addAndMenuActionPerformed

    private void addNandMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNandMenuActionPerformed
        setAddingElementInfo(NandGate.class.getName(), extraParams2);
    }//GEN-LAST:event_addNandMenuActionPerformed

    private void addNorMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNorMenuActionPerformed
        setAddingElementInfo(NorGate.class.getName(), extraParams2);
    }//GEN-LAST:event_addNorMenuActionPerformed

    private void addOrMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addOrMenuActionPerformed
        setAddingElementInfo(OrGate.class.getName(), extraParams2);
    }//GEN-LAST:event_addOrMenuActionPerformed

    private void addXnorMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addXnorMenuActionPerformed
        setAddingElementInfo(XnorGate.class.getName(), extraParams2);
    }//GEN-LAST:event_addXnorMenuActionPerformed

    private void addXorMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addXorMenuActionPerformed
        setAddingElementInfo(XorGate.class.getName(), extraParams2);
    }//GEN-LAST:event_addXorMenuActionPerformed

    private void addWireMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addWireMenuActionPerformed
        setAddingElementInfo(Wire.class.getName(), null);
    }//GEN-LAST:event_addWireMenuActionPerformed

    private void saveMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuActionPerformed
        saveComponent();
    }//GEN-LAST:event_saveMenuActionPerformed

    private void lOuputMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lOuputMenuActionPerformed
        setAddingElementInfo(LogicOutput.class.getName(), null);
    }//GEN-LAST:event_lOuputMenuActionPerformed

    private void closeMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeMenuActionPerformed
        closeLogic();
    }//GEN-LAST:event_closeMenuActionPerformed

    private void lInputMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lInputMenuActionPerformed
        setAddingElementInfo(LogicInput.class.getName(), null);
    }//GEN-LAST:event_lInputMenuActionPerformed

    private void pauseContinueMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseContinueMenuActionPerformed
        if (canvas.isPaused) {
            canvas.resume();
            pauseContinueMenu.setText(this.pauseExecutionText);
        } else {
            canvas.pause();
            pauseContinueMenu.setText(this.continueExecutionText);
        }
        canvas.prepareRunWithoutAnalysis();
    }//GEN-LAST:event_pauseContinueMenuActionPerformed

    private void inverterMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inverterMenuActionPerformed
        setAddingElementInfo(NotGate.class.getName(), null);
    }//GEN-LAST:event_inverterMenuActionPerformed

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        if (closeLogic())
            System.out.println("close it");
    }//GEN-LAST:event_formInternalFrameClosing

    private void multiBitsMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_multiBitsMenuActionPerformed
        setAddingElementInfo(MultiBitsInput.class.getName(), new String[] { "z" });
    }//GEN-LAST:event_multiBitsMenuActionPerformed

    private void multiBitsOutputMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_multiBitsOutputMenuActionPerformed
        setAddingElementInfo(MultiBitsOutput.class.getName(), new String[] { "z" });
    }//GEN-LAST:event_multiBitsOutputMenuActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        canvas.reset();
        pauseContinueMenu.setText(this.startExecutionText);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void clockInputMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clockInputMenuActionPerformed
        setAddingElementInfo(ClockInput.class.getName(), null);
    }//GEN-LAST:event_clockInputMenuActionPerformed

    private void debuggerMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_debuggerMenuActionPerformed
        try {
            this.canvas.debugger.setVisible(true);
            this.canvas.debugger.setSelected(true);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(SimulationWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_debuggerMenuActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem addAndMenu;
    private javax.swing.JMenuItem addNandMenu;
    private javax.swing.JMenuItem addNorMenu;
    private javax.swing.JMenuItem addOrMenu;
    private javax.swing.JMenuItem addWireMenu;
    private javax.swing.JMenuItem addXnorMenu;
    private javax.swing.JMenuItem addXorMenu;
    private javax.swing.JMenuItem clockInputMenu;
    private javax.swing.JMenuItem closeMenu;
    private javax.swing.JMenuItem debuggerMenu;
    private javax.swing.JMenuItem inverterMenu;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JMenuItem lInputMenu;
    private javax.swing.JMenuItem lOuputMenu;
    private javax.swing.JMenu modulesMenu;
    private javax.swing.JMenuItem multiBitsMenu;
    private javax.swing.JMenuItem multiBitsOutputMenu;
    private javax.swing.JMenuItem pauseContinueMenu;
    private javax.swing.JMenuItem saveMenu;
    private javax.swing.JMenu windowMenu;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e) {
        setAddingElementInfo(ModuleChip.class.getName(), new String[] {e.getActionCommand()});      
    }

    public SimulationCanvas getCanvasElement() {
        return this.canvas;
    }
}
