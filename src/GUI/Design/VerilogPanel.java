/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Design;

import Simulation.Configuration;
import VerilogCompiler.VerilogInfo;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rsyntaxtextarea.templates.CodeTemplate;
import org.fife.ui.rsyntaxtextarea.templates.StaticCodeTemplate;
import org.fife.ui.rtextarea.RTextScrollPane;
import rsyntaxtextarea.TextEditorDemo;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class VerilogPanel extends JPanel {

    RSyntaxTextArea textArea;
    GridLayout layout;
    JTextArea errorArea;
    boolean hasBeenModified = false;
    public String title;

    public VerilogPanel() {
        super(new GridLayout(1, 1));
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
        errorArea.setEditable(false);
        errorArea.setSize(getWidth(), 100);
        errorArea.setVisible(false);

        addChangeListener();

        RTextScrollPane sp = new RTextScrollPane(textArea);
        sp.setFoldIndicatorEnabled(true);
        add(sp);

        //add(errorArea);

        layout = (GridLayout) getLayout();
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

    /**
     * Create a simple provider that adds some Java-related completions.
     */
    private CompletionProvider createCompletionProvider() {

        DefaultCompletionProvider provider = new DefaultCompletionProvider();

        // Add completions for all Verilog keywords. A BasicCompletion is just
        // a straightforward word completion.
        for (String keyword : VerilogInfo.getInstance().getKeywords()) {
            provider.addCompletion(new BasicCompletion(provider, keyword));
        }
        return provider;

    }

    public void showBottomPanel() {
        //layout.setRows(2);
        //errorArea.setVisible(true);
        //add(errorArea);
    }

    public void setSelected() {
        textArea.requestFocus();
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

    public void setReadOnly(boolean readOnly) {
        if (textArea != null) {
            textArea.setEditable(!readOnly);
        }
    }
}
