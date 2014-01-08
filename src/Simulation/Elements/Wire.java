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
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class Wire extends BaseElement {

    public Wire(int x, int y) {
        super(x, y);
    }

    public Wire(int x, int y, int x2, int y2, String[] extraParams) throws ArchException {
        super(x, y, x2, y2, extraParams);
    }

    public Wire(int x, int y, int x2, int y2, int flags) {
        super(x, y, x2, y2, flags);
    }

    @Override
    public void draw(Graphics g) {
        setVoltageColor(g, voltages[0]);
        drawThickLine(g, point1, point2);
        setBbox(point1, point2, 3);
        drawPosts(g);
    }

    @Override
    public void doStep() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Element getXmlElement(Document document) {
        Element element = super.getXmlElement(document);
        element.setAttribute("type", Wire.class.getName());
        
        return element;
    }

    @Override
    boolean isWire() {
        return true;
    }
}
