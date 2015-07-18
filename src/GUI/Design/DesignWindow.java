/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Design;

import DataStructures.CircuitGenerator;
import DataStructures.ModuleRepository;
import Exceptions.ModuleDesignNotFoundException;
import GUI.ErrorPanel;
import GUI.MainWindow;
import Simulation.Configuration;
import Simulation.Elements.BaseElement;
import Utils.FileUtils;
import Utils.TextUtils;
import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.SemanticCheck;
import VerilogCompiler.SyntacticTree.Declarations.ModuleDecl;
import VerilogCompiler.SyntacticTree.Others.Port;
import VerilogCompiler.VerilogLexer;
import VerilogCompiler.parser;
import java.awt.Graphics;
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
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class DesignWindow extends javax.swing.JInternalFrame {

    MainWindow parent;
    PreviewPanel preview;
    SourcePanel newSource;
    ErrorPanel errors;
    String fileName = "";
    String filePath = "";
    boolean modified = false, compiled = false;

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
        
        preview = new PreviewPanel(parent);
        errors = new ErrorPanel();
        newSource = new SourcePanel();
        this.tabs.setTabPlacement(JTabbedPane.LEFT);
        this.tabs.addTab("Design Preview", preview);

        //this.tabs.addTab("Source", source);

        this.tabs.addTab("Source", newSource);
        
        //this.tabs.addTab("Error Log", errors);        

        addTabChangeListener();
        this.tabs.setSelectedIndex(1);
        pack();

        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        setCustomTitle();  
        
        this.setLocation((parent.getWidth() - getWidth()) / 2, 
                (parent.getHeight() - getHeight()) / 2);
    }

    @Override
    protected void paintComponent(Graphics g) {        
        if (!modified && newSource != null && newSource.hasBeenModified) {
            addFilenameToTitle(fileName + "*");
            modified = true;
            compiled = false;
            update(g);
        }
        super.paintComponent(g);
    }
    
    public void refreshTheme() {
        this.newSource.refreshTheme();
    }
    
    public void addFilenameToTitle(String fileName) {
        this.fileName = fileName;
        String sTitle = "Component Design Window";
        setTitle(sTitle + " - " + fileName);
    }
    
    public void setFilePath(String absolutePath) {
        this.filePath = absolutePath;
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
                if(tabs.getSelectedIndex() == 0){
                    if(newSource.hasBeenModified || !compiled){
                        ModuleDecl mod = compileWithoutSemantics();
                        compileLogic(mod);
                        newSource.hasBeenModified = false;
                    }
                }else if (tabs.getSelectedIndex() == 1) {
                    newSource.setSelected();
                }
            }
        });
    }

    private void saveComponent() {
        JFileChooser fileChooser = new JFileChooser(Configuration.MODULES_DIRECTORY_PATH);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int choose = fileChooser.showSaveDialog(this);
        
        if (choose != JFileChooser.APPROVE_OPTION)
            return;
        
        File target = fileChooser.getSelectedFile();
        
        String pureFileName = TextUtils.GetFileNameWithoutExtension(target.getName());
        File newTarget = new File(target.getParent() + File.separator + TextUtils.AddDesignTypeFileExtension(pureFileName));
        
        boolean saveMetadataFile = false;
        ModuleDecl module = compileWithoutSemantics();
        File moduleDirectoryFile = new File(Configuration.MODULES_DIRECTORY_PATH);
        boolean isChildPath = FileUtils.CheckIfPathIsChild(moduleDirectoryFile, newTarget);
        if(module == null){
            int response = JOptionPane.showConfirmDialog(
                    this, 
                    "The module has compilation errors.\nDo you want to save the file anyway?.", 
                    "Compilation Error", 
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.WARNING_MESSAGE);
            if(response != JOptionPane.YES_OPTION)
                return;
        }else{
            String moduleName = module.getModuleName();
            if(isChildPath){
                File currentFile = new File(this.filePath);
                boolean isSameFile = currentFile.compareTo(target) == 0 || currentFile.compareTo(newTarget) == 0;
                if(isSameFile){
                    if(ModuleRepository.getInstance().isModuleBeingUsed(moduleName)){
                        int response = JOptionPane.showConfirmDialog(
                                this,
                                "The module: [" + moduleName + "] is being used.\nDo you want to save the module?, module instances will be automatically updated.", 
                                "Module is being used", 
                                JOptionPane.YES_NO_OPTION, 
                                JOptionPane.WARNING_MESSAGE);
                        if(response != JOptionPane.YES_OPTION)
                            return;
                    }
                }else{
                    if(ModuleRepository.getInstance().getModuleNames().contains(moduleName)){
                        JOptionPane.showMessageDialog(this, "Can't save the File, already exists a module named: [" + moduleName + "].", "Duplicated Module", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                saveMetadataFile = true;
            }
        }
        
        FileUtils.DeleteFile(target);
        FileUtils.DeleteFile(newTarget);
        
        try {
            if(newSource.hasBeenModified || !compiled){
                compileLogic(module);
                newSource.hasBeenModified = false;
            }
            
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document document = docBuilder.newDocument();

            Element root = document.createElement("module");
            document.appendChild(root);
            Element elements = this.preview.toXmlRootElement(document);
            root.appendChild(elements);

            Element program = document.createElement("behaviour");
            program.setTextContent(newSource.getProgram());
            root.appendChild(program);

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
        addFilenameToTitle(newTarget.getName());
        modified = false;
        
        if(saveMetadataFile && Configuration.COMPILE_ON_SAVE)
        {
            saveModuleMetadata(module);
        }
        
        if(isChildPath)
            this.parent.refreshModules();
    }
    
    public static boolean saveModuleMetadata(ModuleDecl module) {
        if (module == null)
            return false;
        
        File newTarget = new File(Configuration.MODULE_METADATA_DIRECTORY_PATH +
                "/" + TextUtils.AddMetadataTypeFileExtension(module.getModuleName()));
        
        FileUtils.DeleteFile(newTarget);
        
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
                portElement.setAttribute("index", Integer.toString(port.getSideIndex()));
                ports.appendChild(portElement);
            }
            root.appendChild(ports);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult result = new StreamResult(newTarget);

            transformer.transform(domSource, result);
            return true;
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(DesignWindow.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
        
        return false;
    }

    public void destroyFrame() {
    }
    
    public void loadFromDocument(Document document) {
        NodeList behaviour = document.getElementsByTagName("behaviour");
        newSource.setProgram(behaviour.item(0).getTextContent());
        newSource.hasBeenModified = false;
        compiled = true;
        
        NodeList elements = document.getElementsByTagName("element");
        if(elements == null || elements.getLength() == 0)
            return;
        
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
            
            try {
                BaseElement baseElement = CircuitGenerator.getInstance().constructElement(type, x, y, x2, y2, stringExtraParams);
                ModuleDecl module = compileWithoutSemantics();
                if (!(baseElement instanceof ChipRectangule)) {
                    System.out.println("Error! It shouldn't be anything but a ChipRectangule");
                    continue;
                }
                ((ChipRectangule) baseElement).init(module.getPortList(), element);
                preview.addElement(baseElement);
            } catch (ModuleDesignNotFoundException ex) {
                //No pudo compilar el modulo.
            }catch(Exception ex){
                //No pudo compilar el modulo.
            }
        }
    }

    public void showErrorPanel(String errorLog) {
        this.errors.addLog(errorLog);
        //this.tabs.setSelectedIndex(2);
    }
    
    public void showClickableErrors(ArrayList<String> errors) {
        this.newSource.setErrors(errors);
    }
    
    public ModuleDecl compileWithoutSemantics() {
        try {
            ErrorHandler.getInstance().reset();
            SemanticCheck.getInstance().resetAll();
            parser parser = new parser(new VerilogLexer(new StringReader(newSource.getProgram())));
            Symbol result = parser.parse();
            if (result == null || result.value == null) {
                if (ErrorHandler.getInstance().hasErrors()) {
                    showErrorPanel(ErrorHandler.getInstance().getErrors());
                    showClickableErrors(ErrorHandler.getInstance().getErrorList());
                }
                return null;
            }

            ModuleDecl module = (ModuleDecl) result.value;
            return module;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            showClickableErrors(ErrorHandler.getInstance().getErrorList());
            return null;
        }
    }
    
    public void generateCircuitFromModule(ModuleDecl module) {
        ChipRectangule chip = new ChipRectangule(50, 50, 100, 50, new String[] { module.getModuleName() });
        chip.init(module.getPortList());        
        preview.addElement(chip);
    }
    
    public void closeLogic() {
        if (newSource.hasBeenModified) {
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
    
    public void compileLogic(ModuleDecl module) {        
        if (module != null) {
            module.validateSemantics();
            if (ErrorHandler.getInstance().hasErrors()) {
                //showErrorPanel(ErrorHandler.getInstance().getErrors());
                showClickableErrors(ErrorHandler.getInstance().getErrorList());
            }
            else {
                preview.removeAll();
                generateCircuitFromModule(module);
                tabs.setSelectedIndex(0);
                compiled = true;
                if(this.newSource != null)
                    this.newSource.clearLogs();
            }
        } else {
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
                formInternalFrameOpened(evt);
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

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabs, javax.swing.GroupLayout.DEFAULT_SIZE, 658, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabs, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)
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
        compileLogic(module);
    }//GEN-LAST:event_compileMenuActionPerformed

    private void clearLogsMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearLogsMenuActionPerformed
        //this.errors.clearLogs();
        this.newSource.clearLogs();
    }//GEN-LAST:event_clearLogsMenuActionPerformed

    private void closeMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeMenuActionPerformed
        closeLogic();
    }//GEN-LAST:event_closeMenuActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        newSource.setSelected();
    }//GEN-LAST:event_formInternalFrameOpened

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem clearLogsMenu;
    private javax.swing.JMenuItem closeMenu;
    private javax.swing.JMenuItem compileMenu;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JMenuItem saveOption;
    private javax.swing.JTabbedPane tabs;
    // End of variables declaration//GEN-END:variables

}
