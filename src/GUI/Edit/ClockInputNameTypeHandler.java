/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Edit;

import Simulation.ClockEventManagement;
import static Simulation.Elements.BaseElement.fontSimulationEditionDialogType;
import Utils.TextUtils;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Franklin
 */
public class ClockInputNameTypeHandler extends InputTypeHandler {

    JTextField txtName;
    JLabel lblName;
    ClockEventManagement clockManagement;
    int uniqueClockId;
            
    public ClockInputNameTypeHandler(String currentValue, ClockEventManagement management, int uniqueClockId) {
        super(currentValue);
        this.clockManagement = management;
        this.uniqueClockId = uniqueClockId;
        this.setBehaviorProperties();
        this.setDocumentListener();
        this.components.add(lblName);
        this.components.add(txtName);
    }
    
    @Override
    public String getCurrentAsBinary() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long getCurrentAsDecimal() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }   

    private void setBehaviorProperties() {
        this.lblName = new JLabel(TextUtils.LeftEmptyPadding("Name:", 13));
        this.lblName.setFont(fontSimulationEditionDialogType);
        
        this.txtName = new JTextField(this.currentValue);
        Dimension prefSize = this.txtName.getPreferredSize();
        prefSize = new Dimension(140, prefSize.height);
        this.txtName.setPreferredSize(prefSize);
    }

    private void setDocumentListener() {
        this.txtName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                analyze(txtName.getText());
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                analyze(txtName.getText());
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                analyze(txtName.getText());
            }
        });
    }
    
     private void analyze(String text) {
        this.parentDialog.setErrorMessage("");
        if(this.currentValue.equals(text))
            return;

        this.currentValue = text;
        if(this.clockManagement.seriesNameExist(this.currentValue, this.uniqueClockId)){
            String errorMsg = "The name: [" + this.currentValue + "] is being used.";
            this.parentDialog.setErrorMessage(errorMsg);
            this.isCurrentValueValid  = false;
            this.errorMessage = errorMsg;
        }else{
            this.isCurrentValueValid  = true;
        }
    }
}
