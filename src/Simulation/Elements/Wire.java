/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation.Elements;

import Exceptions.ArchException;
import java.awt.Graphics;
import java.awt.Point;
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
        Point extra1 = new Point(point1.x, point2.y);
        drawThickLine(g, point1, extra1);
        drawThickLine(g, extra1, point2);
        setBbox(point1, point2, 3);
        drawPosts(g);
    }

    @Override
    public void stampVoltages() {
        containerPanel.stampVoltageSource(joints[0], joints[1], voltageSourceReference, 0);
    }
    
    @Override
    public int getVoltageSourceCount() { 
        return 1;
    }

    @Override
    public void doStep() {
        
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
