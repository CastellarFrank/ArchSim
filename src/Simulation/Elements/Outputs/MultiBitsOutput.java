/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation.Elements.Outputs;

import Exceptions.ArchException;
import Simulation.Configuration;
import Simulation.Elements.BaseElement;
import Simulation.Elements.NamedWire;
import java.awt.Font;
import java.awt.Graphics;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class MultiBitsOutput extends BaseElement {

    public MultiBitsOutput(int x, int y) {
        super(x, y);
        binaryValues = new String[1];
        binaryValues[0] = "z";
    }

    public MultiBitsOutput(int x, int y, int x2, int y2, String[] extraParams) throws ArchException {
        super(x, y, x2, y2, extraParams);
        binaryValues = new String[1];
        binaryValues[0] = extraParams[0];
    }

    @Override
    public void setPoints() {
        super.setPoints();
        lead1 = interpolatePoint(point1, point2, 1 - 12 / dn);
    }

    @Override
    public int getPostCount() {
        return 1;
    }

    @Override
    public void draw(Graphics g) {
        Font f = new Font("SansSerif", Font.BOLD, 16);
        g.setFont(f);
        g.setColor(needsHighlight() ? selectedColor : defaultColor);
        setBbox(point1, lead1, 0);
        drawText(g, binaryValues[0]);
        setVoltageColor(g, voltages[0]);
        drawThickLine(g, point1, lead1);
        drawPosts(g);
    }

    @Override
    public void doStep() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public Element getXmlElement(Document document) {
        Element element = super.getXmlElement(document);
        element.setAttribute("type", NamedWire.class.getName());
        
        Element extraParam0 = document.createElement("param");
        extraParam0.setTextContent(binaryValues[0]);
        
        element.appendChild(extraParam0);
        
        return element;
    }
}
