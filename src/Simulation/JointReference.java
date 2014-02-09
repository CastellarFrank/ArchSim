/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation;

import Simulation.Elements.BaseElement;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class JointReference {
    public BaseElement element;
    public int postNumber;

    public JointReference(BaseElement element, int postNumber) {
        this.element = element;
        this.postNumber = postNumber;
    }
}
