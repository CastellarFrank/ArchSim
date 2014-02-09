/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation.Elements.Inputs;

import Exceptions.ArchException;
import Simulation.Configuration;
import Simulation.Elements.BaseElement;
import Simulation.Elements.BasicSwitch;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class LogicInput extends BasicSwitch {

    public LogicInput(int x, int y) {
        super(x, y);
    }

    public LogicInput(int x, int y, int x2, int y2, String[] extraParams) throws ArchException {
        super(x, y, x2, y2, extraParams);
    }

    public LogicInput(int x, int y, int x2, int y2, int flags) {
        super(x, y, x2, y2, flags);
    }

    @Override
    public void toggle() {
        if (isOpen)
            voltages[0] = Configuration.LOGIC_1_VOLTAGE + 1;
        else
            voltages[0] = 0.0;
        isOpen = !isOpen;
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
        Font f = new Font("SansSerif", Font.BOLD, 20);
        g.setFont(f);
        g.setColor(needsHighlight() ? BaseElement.selectedColor : BaseElement.defaultColor);
        String text = isOpen ? "L" : "H";
        if (Configuration.LOGIC_VALUES_AS_NUMBER) {
            text = isOpen ? "0" : "1";
        }
        setBbox(point1, lead1, 0);
        drawCenteredText(g, text, x2, y2, true);
        setVoltageColor(g, voltages[0]);
        Point extra1 = new Point(point1.x, lead1.y);
        drawThickLine(g, point1, extra1);
        drawThickLine(g, extra1, lead1);
        drawPosts(g);
    }
    
    @Override
    public int getVoltageSourceCount() { 
        return 1;
    }

    @Override
    public void stampVoltages() {
        double newVoltage;
        if (isOpen)
            newVoltage = Configuration.LOGIC_0_VOLTAGE;
        else
            newVoltage = Configuration.LOGIC_1_VOLTAGE * 2;
        containerPanel.stampVoltageSource(0, joints[0], voltageSourceReference, newVoltage);
    }

    @Override
    public void doStep() {
    }
    
    @Override
    public Element getXmlElement(Document document) {
        Element element = super.getXmlElement(document);
        element.setAttribute("type", LogicInput.class.getName());
        
        return element;
    }
}
