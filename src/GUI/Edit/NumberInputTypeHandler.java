/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Edit;

import Utils.TextUtils;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigInteger;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Franklin
 */
public class NumberInputTypeHandler extends InputTypeHandler{
    JTextField txtValue;
    boolean modifying;
    long minumunValue = 100;
    long maximunValue = Long.MAX_VALUE;
    
    public NumberInputTypeHandler(String currentValue) {
        super(currentValue);
        this.txtValue = new JTextField(currentValue, 13);
        this.setDocumentHandlers();
        this.initializeComponentsStructure();
    }
    
    private void initializeComponentsStructure(){
        Font f = new Font("Consolas", Font.PLAIN, 14);
        JLabel binaryLabel = new JLabel(TextUtils.LeftEmptyPadding("Milliseconds:", 12));
        binaryLabel.setFont(f);
        components.add(binaryLabel);
        components.add(this.txtValue);
    }

    private void setDocumentHandlers() {
        this.setDocumentListeners(txtValue);
    }
    
    private void setDocumentListeners(final JTextField textElement) {
        textElement.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if(!modifying)
                    analyze(textElement.getText());
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                if(!modifying)
                    analyze(textElement.getText());
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                if(!modifying)
                    analyze(textElement.getText());
            }
        });
        textElement.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                parentDialog.setErrorMessage("");
                if (c == KeyEvent.VK_BACK_SPACE) {
                    return;
                }
                String charAsString = String.valueOf(c);
                if(!charAsString.matches("[0-9]")){
                    errorMessage = "The character: [" + c + "] is not Decimal.";
                    if(parentDialog != null)
                        parentDialog.setErrorMessage(errorMessage);
                    e.consume();
                }
            }
        });
    }
    
    private void analyze(String text) {
        long numberToAnalyze;
        try {
            numberToAnalyze = Long.parseLong(text);
        } catch (Exception e) {
            this.errorMessage = "The entry may be too big.";
            this.isCurrentValueValid = false;
            return;
        }
        
        if(numberToAnalyze < this.minumunValue){
            this.errorMessage = "The min value supported: [" + this.minumunValue + "].";
            this.isCurrentValueValid = false;
            return;
        }
        if(numberToAnalyze > this.maximunValue){
            this.errorMessage = "The max value supported: [" + this.maximunValue + "].";
            this.isCurrentValueValid = false;
            return;
        }
        
        this.currentValue = Long.toString(numberToAnalyze);
        this.isCurrentValueValid = true;
    }

    @Override
    public String getCurrentAsBinary() {
        return Long.toBinaryString(new BigInteger(this.currentValue).longValue());
    }

    @Override
    public long getCurrentAsDecimal() {
        return Long.parseLong(this.currentValue);
    }
}
