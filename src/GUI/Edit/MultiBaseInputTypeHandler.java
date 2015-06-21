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
public class MultiBaseInputTypeHandler extends InputTypeHandler{
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
    EntryType currentValueType;
    boolean isUndefinedValue = false;
    
    public MultiBaseInputTypeHandler(String currentValue){
        super(currentValue);
        
        if(currentValue.matches("[zZxX]")){
            this.txtBinary = new JTextField(currentValue, 13);
            this.txtDecimal = new JTextField(currentValue, 13);
            this.txtHexadecimal = new JTextField(currentValue, 13);
        }else{
            long value = 0;
            try {
                value = new BigInteger(currentValue, 2).longValue();
            } catch (Exception e) {}
            this.txtBinary = new JTextField(Long.toBinaryString(value), 13);
            this.txtDecimal = new JTextField(String.valueOf(value), 13);
            this.txtHexadecimal = new JTextField(Long.toHexString(value), 13);
        }
        
        this.setDocumentHandlers();
        this.initializeComponentsStructure();
    }
    
    @Override
    public String getCurrentAsBinary(){
        if(!this.isCurrentValueValid)
            return "";
        if(this.isUndefinedValue)
            return this.currentValue;
        
        return Long.toBinaryString(new BigInteger(this.currentValue, 
                this.currentValueType == null ? 2 : this.currentValueType.getBaseValue()).longValue());
    }
    
    @Override
    public long getCurrentAsDecimal() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public String getCurrentValue() {
        return this.currentValue;
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
        
        textElement.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
               char c = e.getKeyChar();
               parentDialog.setErrorMessage("");
               if (c == KeyEvent.VK_BACK_SPACE) {
                  return;
               }
               String charAsString = String.valueOf(c);
               if(charAsString.matches("[zZxX]")){
                   return;
               }
               
               switch(entryType){
                   case BINARY:
                        if(!charAsString.matches("[0-1]")){
                            errorMessage = "The character: [" + c + "] is not Binary.";
                            if(parentDialog != null)
                                parentDialog.setErrorMessage(errorMessage);
                            e.consume();
                        }
                       break;
                   case DECIMAL:
                        if(!charAsString.matches("[0-9]")){
                            errorMessage = "The character: [" + c + "] is not Decimal.";
                            if(parentDialog != null)
                                parentDialog.setErrorMessage(errorMessage);
                            e.consume();
                        }
                       break;
                   case HEXADECIMAL:
                       if(!charAsString.matches("[0-9a-fA-F]")){
                            errorMessage = "The character: [" + c + "] is not Hexadecimal.";
                            if(parentDialog != null)
                                parentDialog.setErrorMessage(errorMessage);
                            e.consume();
                        }
                       break;
               }
            }
        });
    }
    
    private void analyze(String value, EntryType entryType){
        this.modifying = true;
        if(entryType == null){
            this.modifying = false;
            this.isCurrentValueValid = false;
            this.errorMessage = "Base not recognized.";
            return;
        }
        boolean isZValue = false;
        if(value.isEmpty() || value == null || value.matches("[zZ]")){
            this.currentValue = "z";
            this.isUndefinedValue = true;
            isZValue = true;
        }else if(value.matches("[xX]")){
            isZValue = true;
            this.currentValue = "x";
            this.isUndefinedValue = true;
        }else{
            this.currentValue = value;
            this.isUndefinedValue = false;
        }
        
        this.currentValueType = entryType;
        
        long longValue = 0;
        if(!isZValue){
            try {
                longValue = Long.parseLong(value, entryType.getBaseValue());
           } catch (NumberFormatException e) {
               this.modifying = false;
               this.isCurrentValueValid = false;
               this.errorMessage = "The: [" + entryType.getBaseName()+ "] value is invalid.";
               return;
           }
        }
        
        switch(entryType){
            case BINARY :
                if(isZValue){
                    this.txtDecimal.setText(this.currentValue);
                    this.txtHexadecimal.setText(this.currentValue);
                }else{
                    this.txtDecimal.setText(String.valueOf(longValue));
                    this.txtHexadecimal.setText(Long.toHexString(longValue));
                }
                break;
            case DECIMAL:
                if(isZValue){
                    this.txtBinary.setText(this.currentValue);
                    this.txtHexadecimal.setText(this.currentValue);
                }else{
                    this.txtBinary.setText(Long.toBinaryString(longValue));
                    this.txtHexadecimal.setText(Long.toHexString(longValue));
                }
                break;
            case HEXADECIMAL:
                if(isZValue){
                    this.txtBinary.setText(this.currentValue);
                    this.txtDecimal.setText(this.currentValue);
                }else{
                    this.txtBinary.setText(Long.toBinaryString(longValue));
                    this.txtDecimal.setText(String.valueOf(longValue));
                }
                break;
        }
        isCurrentValueValid = true;
        modifying = false;
    }
}
