/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Design;

import Exceptions.ArchException;
import GUI.ErrorPanel;
import GUI.MainWindow;
import Simulation.Configuration;
import Simulation.Elements.Inputs.LogicInput;
import Simulation.Elements.Wire;
import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.SemanticCheck;
import VerilogCompiler.SyntacticTree.Declarations.ModuleDecl;
import VerilogCompiler.SyntacticTree.Others.Port;
import VerilogCompiler.SyntacticTree.PortDirection;
import VerilogCompiler.VerilogLexer;
import VerilogCompiler.parser;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java_cup.runtime.Symbol;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class DesignWindow extends javax.swing.JInternalFrame {

    MainWindow parent;
    VerilogPanel source;
    PreviewPanel preview;
    ErrorPanel errors;
    String fileName = "";

    /**
     * Creates new form DesignWindow
     */
    public DesignWindow(MainWindow parent) {
        super("Component Design Window", true, true, true, true);
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
        
        this.parent = parent;

        source = new VerilogPanel();
        preview = new PreviewPanel(parent);
        errors = new ErrorPanel();
        this.tabs.setTabPlacement(JTabbedPane.LEFT);
        this.tabs.addTab("Design Preview", preview);


        this.tabs.addTab("Source", source);

        this.tabs.addTab("Error Log", errors);

        addTabChangeListener();
        pack();

        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        setCustomTitle();
    }

    private void setCustomTitle() {
        String sTitle = "Component Design Window";
        sTitle += !fileName.isEmpty() ? " - " + fileName : "";
        this.setTitle(sTitle);
    }
    
    private void addTabChangeListener() {
        tabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                /*if (jTabbedPane1.getSelectedIndex() == 0) {
                    try {
                        parser parser = new parser(new VerilogLexer(new StringReader(source.getProgram())));

                        Symbol result = parser.parse();
                        if (result == null) {
                            showErrorPanel("TODO!:");
                            return;
                        }
                        ModuleDecl module = (ModuleDecl) result.value;

                        if (module != null) {
                            System.out.println(module.toString());
                            module.validateSemantics();
                            System.err.println(ErrorHandler.getInstance().getErrors());
                        } else {
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(SyntaxTests.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }*/
                if (tabs.getSelectedIndex() == 1) {
                    source.setSelected();
                }
            }
        });
    }

    private void saveComponent() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int choose = fileChooser.showSaveDialog(this);
        
        if (choose != JFileChooser.APPROVE_OPTION)
            return;
        File target = fileChooser.getSelectedFile();
        
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document document = docBuilder.newDocument();

            Element root = document.createElement("module");
            document.appendChild(root);
            Element elements = this.preview.toXmlRootElement(document);
            root.appendChild(elements);

            Element program = document.createElement("behaviour");
            program.setTextContent(source.getProgram());
            root.appendChild(program);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult result = new StreamResult(target);

            transformer.transform(domSource, result);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(DesignWindow.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
        source.hasBeenModified = false;
        if (Configuration.COMPILE_ON_SAVE) 
            saveModuleMetadata(target.getName());
    }
    
    public void saveModuleMetadata(String fileName) {
        ModuleDecl module = compileWithoutSemantics();
        if (module == null)
            return;
        
        File target = new File(Configuration.MODULE_METADATA_DIRECTORY_PATH +
                "/" + fileName);
        
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document document = docBuilder.newDocument();

            Element root = document.createElement("moduleInfo");
            document.appendChild(root);
            
            Element name = document.createElement("name");
            name.setTextContent(module.getModuleName());
            root.appendChild(name);
            
            Element ports = document.createElement("ports");
            for (Port port : module.getPortList()) {
                Element portElement = document.createElement("port");
                portElement.setAttribute("type", port.getDirection().name());
                portElement.setAttribute("name", port.getIdentifier());
                ports.appendChild(portElement);
            }
            root.appendChild(ports);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult result = new StreamResult(target);

            transformer.transform(domSource, result);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(DesignWindow.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }

    public void destroyFrame() {
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
            
            preview.constructAndAddElement(type, x, y, x2, y2, stringExtraParams);
        }
        
        NodeList behaviour = document.getElementsByTagName("behaviour");
        source.setProgram(behaviour.item(0).getTextContent());
    }

    public void showErrorPanel(String errorLog) {
        this.errors.addLog(errorLog);
        this.tabs.setSelectedIndex(2);
    }
    
    public ModuleDecl compileWithoutSemantics() {
        try {
            ErrorHandler.getInstance().reset();
            SemanticCheck.getInstance().resetAll();
            parser parser = new parser(new VerilogLexer(new StringReader(source.getProgram())));
            Symbol result = parser.parse();
            if (result == null) {
                if (ErrorHandler.getInstance().hasErrors()) {
                    showErrorPanel(ErrorHandler.getInstance().getErrors());
                }
                return null;
            }

            ModuleDecl module = (ModuleDecl) result.value;
            return module;
        } catch (Exception ex) {
            showErrorPanel(ErrorHandler.getInstance().getErrors());
            return null;
        }
    }
    
    public void generateCircuitFromModule(ModuleDecl module) {
        ArrayList<Port> ports = module.getPortList();        
        int wireDefaultLenght = 20, inputsDefaultLenght = 20, verticalSpacing = 20;
        int currentX = 0, currentY = 20, currentX2 = 50, currentY2 = 20;
        for (Port port : ports) {
            try {
                if (port.getDirection() == PortDirection.INPUT) {
                    LogicInput portInput = new LogicInput(currentX2, currentY2, 
                            currentX, currentY, new String[] { "false" });
                    preview.addElement(portInput);
                    currentX += currentX2;
                    currentX2 *= 2;                    
                } else {
                    
                }
                Wire portWire = new Wire(currentX, currentY, currentX2, currentY2, null);
                preview.addElement(portWire);
                
                currentX2 /= 2;
                currentX -= currentX2;
                currentY += verticalSpacing;
                currentY2 += verticalSpacing;
            } catch (ArchException ex) {
                Logger.getLogger(DesignWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        preview.prepareForAnalysis();
    }
    
    public void closeLogic() {
        if (source.hasBeenModified) {
            int result = JOptionPane.showConfirmDialog(this, "Would you like to save before exit?",
                    "Exiting", JOptionPane.YES_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                saveComponent();
                dispose();
            } else if (result == JOptionPane.NO_OPTION) {
                dispose();
            }
        } else {
            dispose();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabs = new javax.swing.JTabbedPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        saveOption = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        closeMenu = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        clearLogsMenu = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        compileMenu = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Design Window");
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

        saveOption.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveOption.setText("Save");
        saveOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveOptionActionPerformed(evt);
            }
        });
        jMenu1.add(saveOption);
        jMenu1.add(jSeparator1);

        closeMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        closeMenu.setText("Close");
        closeMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeMenuActionPerformed(evt);
            }
        });
        jMenu1.add(closeMenu);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");

        clearLogsMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        clearLogsMenu.setText("Clear logs");
        clearLogsMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearLogsMenuActionPerformed(evt);
            }
        });
        jMenu2.add(clearLogsMenu);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Build");

        compileMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        compileMenu.setText("Compile");
        compileMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                compileMenuActionPerformed(evt);
            }
        });
        jMenu3.add(compileMenu);

        jMenuItem2.setText("Generate Preview");
        jMenu3.add(jMenuItem2);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabs, javax.swing.GroupLayout.DEFAULT_SIZE, 575, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabs, javax.swing.GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        closeLogic();
    }//GEN-LAST:event_formInternalFrameClosing

    private void saveOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveOptionActionPerformed
        saveComponent();
    }//GEN-LAST:event_saveOptionActionPerformed

    private void compileMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_compileMenuActionPerformed
        ModuleDecl module = compileWithoutSemantics();
        if (module != null) {
            module.validateSemantics();
            if (ErrorHandler.getInstance().hasErrors())
                showErrorPanel(ErrorHandler.getInstance().getErrors());
            else {
                preview.removeAll();
                generateCircuitFromModule(module);
                tabs.setSelectedIndex(0);
            }
        } else {
        }
    }//GEN-LAST:event_compileMenuActionPerformed

    private void clearLogsMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearLogsMenuActionPerformed
        this.errors.clearLogs();
    }//GEN-LAST:event_clearLogsMenuActionPerformed

    private void closeMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeMenuActionPerformed
        closeLogic();
    }//GEN-LAST:event_closeMenuActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem clearLogsMenu;
    private javax.swing.JMenuItem closeMenu;
    private javax.swing.JMenuItem compileMenu;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JMenuItem saveOption;
    private javax.swing.JTabbedPane tabs;
    // End of variables declaration//GEN-END:variables
}
