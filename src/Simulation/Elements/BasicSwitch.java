/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation.Elements;

import Exceptions.ArchException;
import java.awt.Graphics;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class BasicSwitch extends BaseElement {

    public boolean isOpen;
    public boolean toggleAtClicking = true;

    public BasicSwitch(int x, int y) {
        super(x, y);
        this.isOpen = false;
    }

    public BasicSwitch(int x, int y, int x2, int y2, String[] extraParams) throws ArchException {
        super(x, y, x2, y2, extraParams);
        this.isOpen = Boolean.getBoolean(extraParams[0]);
    }

    public BasicSwitch(int x, int y, int x2, int y2, int flags) {
        super(x, y, x2, y2, flags);
        this.isOpen = false;
    }
    
    public void toggle() {
        this.isOpen = !isOpen;
    }

    @Override
    public void draw(Graphics g) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void doStep() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Element getXmlElement(Document document) {
        Element element = super.getXmlElement(document);
        element.setAttribute("type", BasicSwitch.class.getName());
        
        Element extraParam0 = document.createElement("param");
        extraParam0.setTextContent(Boolean.toString(isOpen));
        
        element.appendChild(extraParam0);
        
        return element;
    }

}
