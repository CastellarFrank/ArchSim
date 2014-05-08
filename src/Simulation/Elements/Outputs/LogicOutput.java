/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation.Elements.Outputs;

import Exceptions.ArchException;
import Simulation.Configuration;
import Simulation.Elements.BaseElement;
import java.awt.Font;
import java.awt.Graphics;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class LogicOutput extends BaseElement {

    public boolean isOpen;
    
    public LogicOutput(int x, int y) {
        super(x, y);
        this.isOpen = false;
    }

    public LogicOutput(int x, int y, int x2, int y2, String[] extraParams) throws ArchException {
        super(x, y, x2, y2, extraParams);
        this.isOpen = Boolean.parseBoolean(extraParams[0]);
        if (isOpen)
            voltages[0] = Configuration.LOGIC_0_VOLTAGE;
        else
            voltages[0] = Configuration.LOGIC_1_VOLTAGE;
    }

    public LogicOutput(int x, int y, int x2, int y2, int flags) {
        super(x, y, x2, y2, flags);
        this.isOpen = false;
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
        Font f = new Font("SansSerif", Font.BOLD, 20);
        g.setFont(f);
        g.setColor(needsHighlight() ? selectedColor : defaultColor);
        String s = "z";
        if (binaryValues != null && binaryValues[0] != null) {
            if (!binaryValues[0].contains("z") && !binaryValues[0].contains("x")) {
                try  {
                    int x = Integer.parseInt(binaryValues[0]);
                    if (x != 0)
                        s = "H";
                    else
                        s = "L";
                } catch (Exception x){}
            }
        }
        if (Configuration.LOGIC_VALUES_AS_NUMBER) {
            s = s.equals("H") ? "1" : "0";
        }
        
        setBbox(point1, lead1, 0);
        drawCenteredText(g, s, x2, y2, true);
        
        if (s.equals("0") || s.equals("L"))
            g.setColor(BaseElement.lowSignalColor);
        else if (s.equals("1") || s.equals("H"))
            g.setColor(BaseElement.highSignalColor);
        
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
        element.setAttribute("type", LogicOutput.class.getName());
        
        Element extraParam0 = document.createElement("param");
        extraParam0.setTextContent(Boolean.toString(isOpen));
        
        element.appendChild(extraParam0);
        
        return element;
    }
}
