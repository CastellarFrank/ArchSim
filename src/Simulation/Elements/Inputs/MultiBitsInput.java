/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation.Elements.Inputs;

import Exceptions.ArchException;
import GUI.Edit.EditInfo;
import Simulation.Configuration;
import Simulation.Elements.BaseElement;
import Simulation.Elements.NamedWire;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class MultiBitsInput extends BaseElement {

    public MultiBitsInput(int x, int y) {
        super(x, y);
        binaryValues = new String[1];
        binaryValues[0] = "0";
    }

    public MultiBitsInput(int x, int y, int x2, int y2, String[] extraParams) throws ArchException {
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
    public int getPostCount() { return 1; }
    
    @Override
    public int getVoltageSourceCount() { 
        return 1;
    }

    @Override
    public void draw(Graphics g) {
        Font f = new Font("SansSerif", Font.BOLD, 16);
        g.setFont(f);
        g.setColor(needsHighlight() ? BaseElement.selectedColor : BaseElement.defaultColor);
        setBbox(point1, lead1, 0);
        drawText(g, binaryValues[0]);
        setVoltageColor(g, voltages[0]);
        Point extra1 = new Point(point1.x, lead1.y);
        drawThickLine(g, point1, extra1);
        drawThickLine(g, extra1, lead1);
        drawPosts(g);
    }

    @Override
    public void doStep() {     
    }
    
    private void calculateVoltageValue() {
        if (binaryValues[0].matches("[0-9]*")) {
            if (Integer.parseInt(binaryValues[0], 2) == 1)
                voltages[0] = Configuration.LOGIC_1_VOLTAGE * 2;
            else if (Integer.parseInt(binaryValues[0], 2) == 0)
                voltages[0] = Configuration.LOGIC_0_VOLTAGE;
        } else 
            voltages[0] = Configuration.LOGIC_0_VOLTAGE;
    }
    
    @Override
    public void stampVoltages() {        
        calculateVoltageValue();
        containerPanel.stampVoltageSource(0, joints[0], voltageSourceReference, voltages[0], binaryValues[0]);
    }
    
    @Override
    public EditInfo getEditInfo(int n) {
        if (n == 0)
            return new EditInfo("value", 0.0, 0, Long.MAX_VALUE).
                    addComponent(new JLabel("Value: ")).
                    addComponent(new JTextField(binaryValues[0], 10));
        return null;
    }
    
    @Override
    public void setEditValue(int n, EditInfo editInfo) {
        if (n == 0) {
            if (!editInfo.value.matches("[0-1xzXZ]*"))
                return;
            setBinaryValue(0, editInfo.value);    
            calculateVoltageValue();
        }
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
