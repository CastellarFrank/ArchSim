/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Edit;

import java.math.BigInteger;
import java.util.ArrayList;
import javax.swing.JComponent;
/**
 *
 * @author Franklin
 */
public abstract class InputTypeHandler {    
    EditionDialog parentDialog;
    protected String currentValue;
    boolean isCurrentValueValid;
    ArrayList<JComponent> components;
    String errorMessage = "";
    
    public InputTypeHandler(String currentValue){
        this.isCurrentValueValid = true;
        this.currentValue = currentValue;
        this.components = new ArrayList<JComponent>();
    }
    
    public void setEditionDialog(EditionDialog parentEditionDialog) {
        this.parentDialog = parentEditionDialog;
    }  
    
    public abstract String getCurrentAsBinary();
    
    public abstract long getCurrentAsDecimal();
        
    public String getErrorMessage() {
        return this.errorMessage;
    }
    
    public ArrayList<JComponent> getComponents() {
        return components;
    }
}
