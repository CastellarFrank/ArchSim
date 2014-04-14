/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Design;

import Simulation.Configuration;
import VerilogCompiler.VerilogInfo;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.CodeTemplateManager;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rsyntaxtextarea.templates.CodeTemplate;
import org.fife.ui.rsyntaxtextarea.templates.StaticCodeTemplate;
import org.fife.ui.rtextarea.RTextScrollPane;
import rsyntaxtextarea.TextEditorDemo;

/**
 *
 * @author Nestor Bermudez
 */
public class SourcePanel extends javax.swing.JPanel {

    RSyntaxTextArea textArea;
    boolean hasBeenModified = false;
    final DefaultListModel model = new DefaultListModel();
    /**
     * Creates new form SourceTest
     */
    public SourcePanel() {
        initComponents();
        
        errorList.setModel(model);
        errorList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent ev) {
                JList list = (JList) ev.getSource();
                if (ev.getClickCount() == 2) {
                    int index = list.locationToIndex(ev.getPoint());
                    System.out.println(list.getModel().getElementAt(index));
                    String line = list.getModel().getElementAt(index).toString();
                    if (line.matches("[0-9]+:.*")) {
                        int lineNumber = Integer.parseInt(line.substring(0, line.indexOf(":")));
                        try {
                            int offset = textArea.getLineStartOffset(lineNumber - 1);
                            textArea.setCaretPosition(offset);
                        } catch (BadLocationException ex) {
                            Logger.getLogger(SourcePanel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        });
        
        textArea = new RSyntaxTextArea(20, 60);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_VERILOG);
        textArea.setCodeFoldingEnabled(true);
        textArea.setAntiAliasingEnabled(true);
        
        CompletionProvider provider = createCompletionProvider();
        AutoCompletion ac = new AutoCompletion(provider);
        ac.install(textArea);

        RSyntaxTextArea.setTemplatesEnabled(true);

        CodeTemplateManager ctm = RSyntaxTextArea.getCodeTemplateManager();

        CodeTemplate ct = new StaticCodeTemplate("iblock", "initial\nbegin\n\t",
                "\nend");
        ctm.addTemplate(ct);
        
        ct = new StaticCodeTemplate("ablock", "always@(",
                ")\nbegin\n\t\nend");
        ctm.addTemplate(ct);

        ct = new StaticCodeTemplate("nmod", "module name(", "); \n\n\t\nendmodule");
        ctm.addTemplate(ct);
        
        RTextScrollPane main = new RTextScrollPane(textArea);
        main.setFoldIndicatorEnabled(true);
        main.setVisible(true);
        
        mainRegion.setLayout(new BorderLayout());
        mainRegion.add(main);
        
    }
    
    public String getProgram() {
        if (textArea != null) {
            return textArea.getText();
        }
        return "";
    }

    public void setProgram(String program) {
        if (textArea != null) {
            textArea.setText(program);
        }
    }
    
    public void refreshTheme() {
        try {
            Theme theme = Theme.load(new FileInputStream(new File(Configuration.THEME)));
            theme.apply(textArea);
        } catch (IOException ex) {
            if (Configuration.DEBUG_MODE) {
                Logger.getLogger(TextEditorDemo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void setReadOnly(boolean readOnly) {
        if (textArea != null) {
            textArea.setEditable(!readOnly);
        }
    }
    
    public void clearLogs() {
        model.clear();
    }
    
    private CompletionProvider createCompletionProvider() {

        DefaultCompletionProvider provider = new DefaultCompletionProvider();

        for (String keyword : VerilogInfo.getInstance().getKeywords()) {
            provider.addCompletion(new BasicCompletion(provider, keyword));
        }
        return provider;

    }
    
    private void addChangeListener() {
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                hasBeenModified = true;
            }
        });
    }
    
    public void setSelected() {
        textArea.requestFocus();
    }
    
    public void setErrors(ArrayList<String> errors) {
        model.clear();
        for (String error : errors) {
            model.addElement(error);
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

        mainRegion = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        errorList = new javax.swing.JList();

        mainRegion.setMinimumSize(new java.awt.Dimension(0, 0));
        mainRegion.setPreferredSize(new java.awt.Dimension(637, 400));

        javax.swing.GroupLayout mainRegionLayout = new javax.swing.GroupLayout(mainRegion);
        mainRegion.setLayout(mainRegionLayout);
        mainRegionLayout.setHorizontalGroup(
            mainRegionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        mainRegionLayout.setVerticalGroup(
            mainRegionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 456, Short.MAX_VALUE)
        );

        jScrollPane1.setMaximumSize(new java.awt.Dimension(637, 161));

        errorList.setForeground(java.awt.Color.red);
        jScrollPane1.setViewportView(errorList);

        jTabbedPane1.addTab("Compilation Logs", jScrollPane1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 637, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainRegion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(mainRegion, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList errorList;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel mainRegion;
    // End of variables declaration//GEN-END:variables
}
