/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation.Elements;

import Exceptions.OutOfRangeException;
import java.awt.Font;
import java.awt.Graphics;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class NamedWire extends BaseElement {

    String text;

    public NamedWire(int x, int y) {
        super(x, y);
    }

    public NamedWire(int x, int y, int x2, int y2, String[] extraParams) {
        super(x, y, x2, y2, extraParams);
        if (extraParams == null || extraParams.length == 0)
            throw new OutOfRangeException("One extra param expected.");
        text = extraParams[0];
    }
    
    @Override
    public void setPoints() {
        super.setPoints();
        lead1 = interpolatePoint(point1, point2, 1 - 12 / dn);
    }
    
    @Override
    public int getPostCount() { return 1; }
    
    @Override
    public void draw(Graphics g) {
        Font f = new Font("SansSerif", Font.BOLD, 12);
        g.setFont(f);
        g.setColor(needsHighlight() ? BaseElement.selectedColor : BaseElement.defaultColor);
        setBbox(point1, lead1, 0);
        drawText(g, text);
        setVoltageColor(g, voltages[0]);
        drawThickLine(g, point1, lead1);
        drawPosts(g);
    }

    @Override
    public void doStep() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public Element getXmlElement(Document document) {
        Element element = super.getXmlElement(document);
        element.setAttribute("type", NamedWire.class.getName());
        
        Element extraParam0 = document.createElement("param");
        extraParam0.setTextContent(text);
        
        element.appendChild(extraParam0);
        
        return element;
    }
    
}
