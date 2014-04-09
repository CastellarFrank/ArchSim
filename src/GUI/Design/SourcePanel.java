/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Design;

import VerilogCompiler.VerilogInfo;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.CodeTemplateManager;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.templates.CodeTemplate;
import org.fife.ui.rsyntaxtextarea.templates.StaticCodeTemplate;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 *
 * @author Nestor Bermudez
 */
public class SourcePanel extends JPanel {
    
    RSyntaxTextArea textArea;
    GridBagLayout layout;
    JTextArea errorArea;
    boolean hasBeenModified = false;
    public String title;
    
    JPanel yellowPanel;
    RTextScrollPane main;
    
    public SourcePanel(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.PAGE_START;
        
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridheight = 3;
        c.gridx = 0;
        c.gridy = 0;
        
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

        errorArea = new JTextArea();
        
        errorArea.setSize(width, height/4);
        errorArea.setPreferredSize(new Dimension(width, height/4));
        errorArea.setVisible(false);

        addChangeListener();

        main = new RTextScrollPane(textArea);
        main.setFoldIndicatorEnabled(true);
        main.setPreferredSize(new Dimension(width, (int) (height * 0.75)));
        this.add(main, c);
        
        showBottomPanel(width, height/4);
    }
    
    @Override
    public void validate() {
        if (main != null)
            main.setPreferredSize(new Dimension(50, 3*getHeight()/4));
        
        if (yellowPanel != null)
            yellowPanel.setPreferredSize(new Dimension(50, getHeight()/4));
        super.validate();
        
    }
    
    public final void showBottomPanel(int width, int height) {
        GridBagConstraints c = new GridBagConstraints();
        
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        
        c.fill = GridBagConstraints.HORIZONTAL;
        
        c.gridheight = 1;
        c.gridy = 1;
        c.anchor = GridBagConstraints.PAGE_END;
        
        yellowPanel = new JPanel();
        yellowPanel.setMaximumSize(new Dimension(width, height));
        yellowPanel.setBackground(Color.yellow);
        yellowPanel.setPreferredSize(new Dimension(width, height));
        
        yellowPanel.add(errorArea);
        
        this.add(yellowPanel, c);
    }

    public void setSelected() {
        textArea.requestFocus();
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
}
