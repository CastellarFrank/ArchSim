/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation;

import java.util.Vector;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class Joint {
    public int coordX, coordY;
    public Vector<JointReference> references;
    private boolean isSelected;
    private boolean isError;

    public Joint() {
        references = new Vector<JointReference>();
    }   
    
    public Joint(int coordX, int coordY) {
        references = new Vector<JointReference>();
        this.coordX = coordX;
        this.coordY = coordY;
    }   

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean value) {
        this.isSelected = value;
    }
    
    public boolean isError(){
        return  this.isError;
    }
    
    public void setAsErrorState(){
        this.isError = true;
    }
}
