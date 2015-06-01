/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Edit;

import Utils.TextUtils;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Franklin
 */
public class InputTypeHandler {

    public enum EntryType{
        BINARY(2),
        DECIMAL(10),
        HEXADECIMAL(16);
        
        private final int baseValue;
        
        EntryType(int base){
            this.baseValue = base;
        }

        public int getBaseValue() {
            return baseValue;
        }
        
        public String getBaseName(){
            return this.toString();
        }
    }
    
    JTextField txtBinary;
    JTextField txtDecimal;
    JTextField txtHexadecimal;
    boolean modifying;
    String currentValue;
    EntryType currentValueType;
    ArrayList<JComponent> components;
    boolean isCurrentValueValid = true;
    String errorMessage = "";
    
    
    public InputTypeHandler(String currentValue){
        this. currentValue = currentValue;
        long value = 0;
        try {
            value = Long.parseLong(currentValue, 2);
        } catch (Exception e) {}
        
        this.txtBinary = new JTextField(Long.toBinaryString(value), 13);
        this.txtDecimal = new JTextField(String.valueOf(value), 13);
        this.txtHexadecimal = new JTextField(Long.toHexString(value), 13);
        
        this.setDocumentHandlers();
        this.initializeComponentsStructure();
    }
    
    public String getCurrentAsBinary(){
        if(!this.isCurrentValueValid)
            return "";
        
        return Long.toBinaryString(Long.parseLong(this.currentValue, 
                this.currentValueType == null ? 2 : this.currentValueType.getBaseValue()));
    }
    
    public String getCurrentValue() {
        return this.currentValue;
    }
        
    public String getErrorMessage() {
        return this.errorMessage;
    }
    
    public ArrayList<JComponent> getComponents() {
        return components;
    }

    public JTextField getTxtBinary() {
        return txtBinary;
    }

    public JTextField getTxtDecimal() {
        return txtDecimal;
    }

    public JTextField getTxtHexadecimal() {
        return txtHexadecimal;
    }

    private void setDocumentHandlers() {
        this.setDocumentListeners(this.txtBinary, EntryType.BINARY);
        this.setDocumentListeners(this.txtDecimal, EntryType.DECIMAL);        
        this.setDocumentListeners(this.txtHexadecimal, EntryType.HEXADECIMAL);
    } 
    
    private void initializeComponentsStructure() {
        this.components = new ArrayList<JComponent>();
        Font f = new Font("Consolas", Font.PLAIN, 14);
        JLabel binaryLabel = new JLabel(TextUtils.LeftEmptyPadding("Binary:", 12)),
                decimalLabel = new JLabel(TextUtils.LeftEmptyPadding("Decimal:", 12)),
                hexadecimalLabel = new JLabel(TextUtils.LeftEmptyPadding("Hexadecimal:", 12));
        binaryLabel.setFont(f);
        decimalLabel.setFont(f);
        hexadecimalLabel.setFont(f);
        components.add(binaryLabel);
        components.add(this.txtBinary);
        components.add(decimalLabel);
        components.add(this.txtDecimal);
        components.add(hexadecimalLabel);
        components.add(this.txtHexadecimal);
    }

    private void setDocumentListeners(final JTextField textElement, final EntryType entryType) {
        textElement.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if(!modifying)
                    analyze(textElement.getText(), entryType);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if(!modifying)
                    analyze(textElement.getText(), entryType);
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                if(!modifying)
                    analyze(textElement.getText(), entryType);
            }
        });
    }
    
    private void analyze(String value, EntryType entryType){
        this.currentValue = value;
        this.currentValueType = entryType;
        this.modifying = true;
        if(entryType == null){
            this.modifying = false;
            this.isCurrentValueValid = false;
            this.errorMessage = "Base not recognized.";
            return;
        }
        
        long longValue;
        try {
             longValue = Long.parseLong(value, entryType.getBaseValue());
        } catch (NumberFormatException e) {
            this.modifying = false;
            this.isCurrentValueValid = false;
            this.errorMessage = "The: [" + entryType.getBaseName()+ "] value is invalid.";
            return;
        }
        
        switch(entryType){
            case BINARY :
                this.txtDecimal.setText(String.valueOf(longValue));
                this.txtHexadecimal.setText(Long.toHexString(longValue));
                break;
            case DECIMAL:
                this.txtBinary.setText(Long.toBinaryString(longValue));
                this.txtHexadecimal.setText(Long.toHexString(longValue));
                break;
            case HEXADECIMAL:
                this.txtBinary.setText(Long.toBinaryString(longValue));
                this.txtDecimal.setText(String.valueOf(longValue));
                break;
        }
        modifying = false;
        isCurrentValueValid = true;
    }
}
