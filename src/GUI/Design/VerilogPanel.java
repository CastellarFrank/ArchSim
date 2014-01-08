/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Design;

import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
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
        } return "";
    }
    
    public void setProgram(String program) {
        if (textArea != null)
            textArea.setText(program);
    }
    
}
