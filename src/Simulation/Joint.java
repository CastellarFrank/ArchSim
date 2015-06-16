/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation;

import Simulation.Elements.Wire;
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
    private boolean hasInput;
    private int index;

    public Joint() {
        references = new Vector<JointReference>();
    }   
    
    public Joint(int coordX, int coordY, int jointIndex) {
        references = new Vector<JointReference>();
        this.coordX = coordX;
        this.coordY = coordY;
        this.index = jointIndex;
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
    
    public void setAsHasInput(){
        if(!this.hasInput){
            this.hasInput = true;
            this.updateInputState();
        }
    }
    
    public void addReference(JointReference jointRef) {
        this.references.add(jointRef);
        if(jointRef.element.isWire()){
            if(this.hasInput)
                ((Wire)jointRef.element).setJointInput(this.index);
        }
    }
    
    public void updateInputState(){
        for(JointReference reference : this.references){
            if(reference.element.isWire()){
                if(this.hasInput)
                    ((Wire)reference.element).setJointInput(this.index);
            }
        }
    }
}
