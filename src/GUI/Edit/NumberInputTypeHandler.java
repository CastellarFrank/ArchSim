/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Edit;

import Simulation.Elements.BaseElement;
import Utils.TextUtils;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigInteger;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author Franklin
 */
public class NumberInputTypeHandler extends InputTypeHandler{
    JSpinner spinnerValue;
    boolean modifying;
    long minumunValue = 100;
    long maximunValue = Long.MAX_VALUE;
    StringBuilder currentValueObject;
    
    public NumberInputTypeHandler(String currentValue) {
        super(currentValue);
        currentValueObject = new StringBuilder(currentValue);
        long value = 0;
        try {
            value = Long.parseLong(currentValue);
        } catch (Exception e) {}
        this.spinnerValue = new JSpinner();
        SpinnerNumberModel model = new CustomSpinnerNumberModel(value, 100L, Long.MAX_VALUE, 100L, this.currentValueObject, this.spinnerValue);
        this.spinnerValue.setModel(model);
        this.setBehaviorProperties();
        this.initializeComponentsStructure();
    }
    
    private void initializeComponentsStructure(){
        JLabel binaryLabel = new JLabel(TextUtils.LeftEmptyPadding("Milliseconds:", 13));
        binaryLabel.setFont(BaseElement.fontSimulationEditionDialogType);
        components.add(binaryLabel);
        components.add(this.spinnerValue);
    }

    private void setBehaviorProperties() {
        //Changing spinner width
        NumberEditor fieldEditor = ((JSpinner.NumberEditor)this.spinnerValue.getEditor());
        Dimension prefSize = fieldEditor.getPreferredSize();
        prefSize = new Dimension(120, prefSize.height);
        fieldEditor.setPreferredSize(prefSize);
        
        //Disabling spinner security to be able of handling values
        JFormattedTextField txt = fieldEditor.getTextField();
        NumberFormatter formatter = (NumberFormatter) txt.getFormatter();
        formatter.setCommitsOnValidEdit(true);
        formatter.setAllowsInvalid(true);
        
        //Avoiding invalid characters at typing spinner.
        txt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                parentDialog.setErrorMessage("");
                if (c == KeyEvent.VK_BACK_SPACE) {
                   return;
                }
                String charAsString = String.valueOf(c);
                if(!charAsString.matches("[0-9]")){
                    errorMessage = "The character: [" + c + "] is not valid.";
                    if(parentDialog != null)
                        parentDialog.setErrorMessage(errorMessage);
                    e.consume();
                }
            }
        });
    }

    @Override
    public String getCurrentAsBinary() {
        this.currentValue = this.currentValueObject.toString();
        return Long.toBinaryString(new BigInteger(this.currentValue).longValue());
    }

    @Override
    public long getCurrentAsDecimal() {
        this.currentValue = this.currentValueObject.toString();
        return Long.parseLong(this.currentValue);
    }
    
    private class CustomSpinnerNumberModel extends SpinnerNumberModel {
        StringBuilder currentValue;
        JSpinner spinnerElement;
        long currentLongValue = 0;
        long minimunValue;
        long maximunValue;
        CustomSpinnerNumberModel(long value, long min, long max, long step, StringBuilder currentValue, JSpinner spinnerElement) {
            super(value, null, null, step);
            this.currentValue = currentValue;
            this.currentLongValue = Long.parseLong(currentValue.toString());
            this.spinnerElement = spinnerElement;
            this.minimunValue = min;
            this.maximunValue = max;
        }
        
        @Override
        public void setValue(Object value) {
            if(currentLongValue == (Long)value){
                super.setValue(value);
            }else{
                super.setValue(value);
                this.setValidValue(value);
            }
        }

        private void setValidValue(Object value) {
            long valueAsDouble = (Long)value;
            if(valueAsDouble > this.maximunValue)
                valueAsDouble = this.maximunValue;
            else if(valueAsDouble < this.minimunValue)
                valueAsDouble = this.minimunValue;
            
            long difference = valueAsDouble % 100;
            valueAsDouble -= difference;
            this.currentValue.setLength(0);
            this.currentValue.append(String.valueOf(valueAsDouble));
            this.currentLongValue = valueAsDouble;
            this.spinnerElement.setValue(valueAsDouble);
        }
    }
}
