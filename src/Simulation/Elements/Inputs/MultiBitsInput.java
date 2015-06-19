/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation.Elements.Inputs;

import Exceptions.ArchException;
import GUI.Edit.EditInfo;
import GUI.Edit.InputTypeHandler;
import Simulation.Configuration;
import Simulation.Elements.BaseElement;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.math.BigInteger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class MultiBitsInput extends BaseElement {
    int textSpace = 10;
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
        lead1 = point2;
        lead1.x -= (textSpace * sign(dx));
    }
    
    @Override
    public int getPostCount() { return 1; }
    
    @Override
    public int getVoltageSourceCount() { 
        return 1;
    }

    @Override
    public boolean isPostOutput(int index) {
        return index == 0;
    }    
    
    @Override
    public void draw(Graphics g) {
        Font f = new Font("SansSerif", Font.BOLD, 16);
        g.setFont(f);
        g.setColor(needsHighlight() ? BaseElement.selectedColor : BaseElement.defaultColor);
        setBbox(point1, lead1, 0);
        drawText(g, binaryValues[0]);
        
        setVoltageColor(g, voltages[0]);
        if (binaryValues != null && binaryValues[0] != null && !needsHighlight()) {
            if (needsHighlight())
                g.setColor(BaseElement.selectedColor);
            else if (binaryValues[0].contains("z"))
                g.setColor(BaseElement.highImpedanceSignalColor);
            else if (binaryValues[0].contains("x"))
                g.setColor(BaseElement.unknownSignalColor);
                
        }
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
            long longValue = new BigInteger(binaryValues[0], 2).longValue();
            if (longValue >= 1)
                voltages[0] = Configuration.LOGIC_1_VOLTAGE * 2;
            else
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
            return new EditInfo("value",new InputTypeHandler(binaryValues[0]));
        
        return null;
    }
    
    @Override
    public void setEditValue(int n, EditInfo editInfo) {
        if (n == 0) {
            InputTypeHandler input = editInfo.getInputTypeHandler();
            String value = input == null ? editInfo.value : input.getCurrentAsBinary();
            if (!value.matches("[0-1xzXZ]*"))
                return;
            setBinaryValue(0, value);    
            calculateVoltageValue();
        }
    }
    
    @Override
    public Element getXmlElement(Document document) {
        Element element = super.getXmlElement(document);
        element.setAttribute("type", MultiBitsInput.class.getName());
        
        Element extraParam0 = document.createElement("param");
        extraParam0.setTextContent(binaryValues[0]);
        
        element.appendChild(extraParam0);
        
        return element;
    }
    
    @Override
    public boolean collidesWith(int x, int y) {
        Point minorX = new Point(point1.x > lead1.x ? lead1 : point1);
        Point minorY = new Point(lead1.y > point1.y ? point1 : lead1);
        
        if(sign(dx) == 1)
            minorX.x -= selectionSeparationMargin;
        
        int heightPointReduction;
        if(sign(dy) == 1){
            heightPointReduction = (pointRadious / 2);
            minorY.y += heightPointReduction;
        }else{
            heightPointReduction = (selectionSeparationMargin + (pointRadious / 2));
        }
        
        Rectangle horizontalRect = new Rectangle(minorX.x, 
                                                 lead1.y - selectionSeparationMargin, 
                                                 boundingBox.width + selectionSeparationMargin,
                                                 selectionSeparationMargin * 2 + 1);
        
        Rectangle verticalRect = new Rectangle(point1.x - selectionSeparationMargin,
                                               minorY.y, 
                                               selectionSeparationMargin * 2 + 1,
                                               boundingBox.height + selectionSeparationMargin - heightPointReduction);
        
        return verticalRect.contains(x, y) || horizontalRect.contains(x, y);
    }
    
    @Override
    public void selectRect(Rectangle r) {
        Point minorX = new Point(point1.x > lead1.x ? lead1 : point1);
        Point minorY = new Point(lead1.y > point1.y ? point1 : lead1);
        
        if(sign(dx) == 1)
            minorX.x -= selectionSeparationMargin;
        
        int heightPointReduction;
        if(sign(dy) == 1){
            heightPointReduction = (pointRadious / 2);
            minorY.y += heightPointReduction;
        }else{
            heightPointReduction = (selectionSeparationMargin + (pointRadious / 2));
        }
        
        Rectangle horizontalRect = new Rectangle(minorX.x, 
                                                 lead1.y - selectionSeparationMargin, 
                                                 boundingBox.width + selectionSeparationMargin,
                                                 selectionSeparationMargin * 2 + 1);
        
        Rectangle verticalRect = new Rectangle(point1.x - selectionSeparationMargin,
                                               minorY.y, 
                                               selectionSeparationMargin * 2 + 1,
                                               boundingBox.height + selectionSeparationMargin - heightPointReduction);
        
        boolean tempSelected = verticalRect.intersects(r) || horizontalRect.intersects(r);
        this.setSelected(tempSelected);
    }
}
