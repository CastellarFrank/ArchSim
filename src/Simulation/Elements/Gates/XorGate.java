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
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class XorGate extends OrGate {

    public XorGate(int x, int y) {
        super(x, y);
    }

    public XorGate(int x, int y, int x2, int y2, String[] extraParams) throws ArchException {
        super(x, y, x2, y2, extraParams);
    }

    @Override
    public boolean calcFunction() {
        int i;
        boolean f = false;
        for (i = 0; i != inputCount; i++) {
            f ^= getInput(i);
        }
        return f;
    }
    
    @Override
    public Element getXmlElement(Document document) {
        Element element = super.getXmlElement(document);
        element.setAttribute("type", XorGate.class.getName());
        
        return element;
    }
    
}
