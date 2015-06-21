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
import java.math.BigInteger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class LogicOutput extends BaseElement {
    int separationBetweenText = 10;
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
        lead1 = point2;
        lead1.x -= (separationBetweenText * sign(dx));
    }

    @Override
    public int getPostCount() {
        return 1;
    }

    @Override
    public void draw(Graphics g) {
        Font f = fontSimulationNumberType;
        g.setFont(f);
        g.setColor(needsHighlight() ? selectedColor : defaultColor);
        String s = "z";
        if (binaryValues != null && binaryValues[0] != null) {
            if (!binaryValues[0].contains("z") && !binaryValues[0].contains("x")) {
                try  {
                    long x = new BigInteger(binaryValues[0], 2).longValue();
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
        
        setVoltageColor(g, voltages[0]);
        
        drawThickLine(g, point1, lead1);
        drawPosts(g);
        drawDescriptionElementText(g, "(L-Output)", separationBetweenText, outputElementDescriptionColor);
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
