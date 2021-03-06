/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation.Elements.Gates;

import Exceptions.ArchException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class NorGate extends OrGate {

    public NorGate(int x, int y) {
        super(x, y);
    }

    public NorGate(int x, int y, int x2, int y2, String[] extraParams) throws ArchException {
        super(x, y, x2, y2, extraParams);
    }

    @Override
    public boolean isInverting() {
        return true;
    }

    @Override
    public boolean calcFunction() {
        return !super.calcFunction();
    }
    
    @Override
    public Element getXmlElement(Document document) {
        Element element = super.getXmlElement(document);
        element.setAttribute("type", NorGate.class.getName());
        
        return element;
    }
}
