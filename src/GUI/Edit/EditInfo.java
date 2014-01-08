/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Edit;

import java.util.ArrayList;
import javax.swing.JComponent;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */

public class EditInfo {
    //<editor-fold defaultstate="collapsed" desc="Attributes">
    public String name, text;
    public double value, minval, maxval;
    
    ArrayList<JComponent> components;
    //</editor-fold>
    
    public EditInfo(String name, double currentValue, double minValue, double maxValue) {
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
}