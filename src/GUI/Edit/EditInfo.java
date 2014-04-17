/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Edit;

import java.math.BigInteger;
import java.util.ArrayList;
import javax.swing.JComponent;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */

public class EditInfo {
    //<editor-fold defaultstate="collapsed" desc="Attributes">
    public String name, text;
    public String value;
    public double minval, maxval;
    public boolean validate;
    
    ArrayList<JComponent> components;
    //</editor-fold>
    
    public EditInfo(String name, double currentValue, double minValue, double maxValue, boolean validate) {
	this.name = name;
	this.value = currentValue + "";
	this.minval = minValue;
        this.maxval = maxValue;
        components = new ArrayList<JComponent>();
        this.validate = validate;
    }
    
    public EditInfo(String name, double currentValue, double minValue, double maxValue) {
	this.name = name;
	this.value = currentValue + "";
	this.minval = minValue;
        this.maxval = maxValue;
        components = new ArrayList<JComponent>();
        this.validate = true;
    }
    
    public EditInfo(String name, String currentValue, double minValue, double maxValue) {
	this.name = name;
	this.value = currentValue;
	this.minval = minValue;
        this.maxval = maxValue;
        components = new ArrayList<JComponent>();
    }
    
    public EditInfo addComponent(JComponent component) {
        components.add(component);
        return this;
    }
    
    public EditInfo setMinValue(int minValue) {
        this.minval = minValue;
        return this;
    }
    
    public EditInfo setMaxValue(int maxValue) {
        this.maxval = maxValue;
        return this;
    }
    
    public boolean accepts(String newValue) {
        if (!validate) return true;
        try {
            Integer result = new BigInteger(newValue).intValue();
            if (result <= maxval && result >= minval)
                return true;
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}