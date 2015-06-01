/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation.Elements;

import Exceptions.ArchException;
import GUI.Edit.EditInfo;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class Wire extends BaseElement {

    int from, to;
    
    public Wire(int x, int y) {
        super(x, y);
    }

    public Wire(int x, int y, int x2, int y2, String[] extraParams) throws ArchException {
        super(x, y, x2, y2, extraParams);
        if (extraParams != null && extraParams.length == 2) {
            from = Integer.parseInt(extraParams[0]);
            to = Integer.parseInt(extraParams[1]);
        }
    }

    public Wire(int x, int y, int x2, int y2, int flags) {
        super(x, y, x2, y2, flags);
    }

    @Override
    public void draw(Graphics g) {
        setVoltageColor(g, voltages[0]);
        if (binaryValues != null && binaryValues[0] != null && !needsHighlight()) {
            if (binaryValues[0].contains("z"))
                g.setColor(BaseElement.highImpedanceSignalColor);
            if (binaryValues[0].contains("x"))
                g.setColor(BaseElement.unknownSignalColor);
        }
        Point extra1 = new Point(point1.x, point2.y);
        drawThickLine(g, point1, extra1);
        drawThickLine(g, extra1, point2);
        setBbox(point1, point2, 0);
        drawPosts(g);
        //System.out.println("volt1 " + binaryValues[0]);
        //System.out.println("volt2 " + binaryValues[1]);
    }

    @Override
    public void stampVoltages() {
        String select = "z";
        if (binaryValues != null && binaryValues[1] != null) {
            String using = binaryValues[1];
            int index = 1;
            if (using == null || using.contains("z") || using .contains("x")) {
                if (binaryValues[0] != null)
                    index = 0;
            }
            
            using = binaryValues[index];
            if (using == null || using.contains("z") || using .contains("x")) {
                containerPanel.stampVoltageSource(joints[0], joints[1], voltageSourceReference, 0, select);
                return;
            }
            
            int size = to - from + 1;
            if (size > binaryValues[index].length()) {
                String extra = new String(new char[size - binaryValues[index].length()])
                        .replace('\0', '0');
                binaryValues[index] = extra + binaryValues[index];
            }
            int min = from;
            int max = to;
            if (max < min) {
                min = binaryValues[index].length() - min - 1;
                max = binaryValues[index].length() - max - 1;
                if (min < 0 || max < 0 || min > binaryValues[index].length()){
                    min = max = 0;
                }
            }
            select = binaryValues[index].substring(min, max + 1);
        }
        containerPanel.stampVoltageSource(joints[0], joints[1], 
                voltageSourceReference, 0, select);
    }
    
    @Override
    public int getVoltageSourceCount() { 
        return 1;
    }

    @Override
    public void doStep() {
        
    }
    
    @Override
    public EditInfo getEditInfo(int n) {
        String value = binaryValues[1];
        if (value == null || value.contains("x") || value.contains("z"))
            value = binaryValues[0];
        if (n == 0)
            return new EditInfo("from" , 0.0, 0, Integer.MAX_VALUE).
                    addComponent(new JLabel("From: ")).
                    addComponent(new JTextField(from + "", 10));
        if (n == 1)
            return new EditInfo("to" , 0.0, 0, Integer.MAX_VALUE).
                    addComponent(new JLabel("     To: ")).
                    addComponent(new JTextField(to + "", 10));
        return null;
    }
    
    @Override
    public void setEditValue(int n, EditInfo editInfo) {
        if (n == 0) {
            from = Integer.parseInt(editInfo.value);
        }
        if (n == 1) {
            to = Integer.parseInt(editInfo.value);
        }
    }

    @Override
    public Element getXmlElement(Document document) {
        Element element = super.getXmlElement(document);
        element.setAttribute("type", Wire.class.getName());
        
        Element extraParam0 = document.createElement("param");
        extraParam0.setTextContent(from + "");
        
        element.appendChild(extraParam0);
        
        Element extraParam1 = document.createElement("param");
        extraParam1.setTextContent(to + "");
        
        element.appendChild(extraParam1);
        
        return element;
    }

    @Override
    boolean isWire() {
        return true;
    }
    
    @Override
    public boolean collidesWith(int x, int y) {
        Point minorX = new Point(point1.x > point2.x ? point2 : point1);
        Point minorY = new Point(point2.y > point1.y ? point1 : point2);
        
        int widthPointReduction;
        if(dy == 0){
            // Removing selection of points
            widthPointReduction = ((pointRadious / 2) * 2) + selectionSeparationMargin;
            minorX.x += (pointRadious / 2);
        }else if(sign(dx) == 1){
            //corner is draw
            widthPointReduction = (pointRadious / 2);
            minorX.x -= selectionSeparationMargin;
        }else{
            //point is draw
            widthPointReduction = (pointRadious / 2);
            minorX.x += widthPointReduction;
        }
        
        int heightPointReduction;
        if(dx == 0){
            // Removing selection of points
            heightPointReduction = ((pointRadious / 2) * 2) + selectionSeparationMargin;
            minorY.y += (pointRadious / 2);
        }else if(sign(dy) == 1){
            //point is drew
            heightPointReduction = (pointRadious / 2);
            minorY.y += heightPointReduction;
        }else{
            //point is drew
            heightPointReduction = (pointRadious / 2);
            minorY.y -= selectionSeparationMargin;
        }
        Rectangle horizontalRect = null, verticalRect = null;
        if(dx != 0)
            horizontalRect = new Rectangle(minorX.x, 
                                                point2.y - selectionSeparationMargin, 
                                                boundingBox.width + selectionSeparationMargin - widthPointReduction,
                                                selectionSeparationMargin * 2 + 1);
        
        if(dy != 0)
            verticalRect = new Rectangle(point1.x - selectionSeparationMargin,
                                            minorY.y, 
                                            selectionSeparationMargin * 2 + 1,
                                            boundingBox.height + selectionSeparationMargin - heightPointReduction);
        
        return (verticalRect != null && verticalRect.contains(x, y)) || (horizontalRect!= null && horizontalRect.contains(x, y));
    }
    
    @Override
    public void selectRect(Rectangle r) {
        Point minorX = new Point(point1.x > point2.x ? point2 : point1);
        Point minorY = new Point(point2.y > point1.y ? point1 : point2);
        
        int widthPointReduction;
        if(dy == 0){
            // Removing selection of points
            widthPointReduction = ((pointRadious / 2) * 2) + selectionSeparationMargin;
            minorX.x += (pointRadious / 2);
        }else if(sign(dx) == 1){
            //corner is draw
            widthPointReduction = (pointRadious / 2);
            minorX.x -= selectionSeparationMargin;
        }else{
            //point is draw
            widthPointReduction = (pointRadious / 2);
            minorX.x += widthPointReduction;
        }
        
        int heightPointReduction;
        if(dx == 0){
            // Removing selection of points
            heightPointReduction = ((pointRadious / 2) * 2) + selectionSeparationMargin;
            minorY.y += (pointRadious / 2);
        }else if(sign(dy) == 1){
            //point is drew
            heightPointReduction = (pointRadious / 2);
            minorY.y += heightPointReduction;
        }else{
            //point is drew
            heightPointReduction = (pointRadious / 2);
            minorY.y -= selectionSeparationMargin;
        }
        Rectangle horizontalRect = null, verticalRect = null;
        if(dx != 0)
            horizontalRect = new Rectangle(minorX.x, 
                                                point2.y - selectionSeparationMargin, 
                                                boundingBox.width + selectionSeparationMargin - widthPointReduction,
                                                selectionSeparationMargin * 2 + 1);
        
        if(dy != 0)
            verticalRect = new Rectangle(point1.x - selectionSeparationMargin,
                                            minorY.y, 
                                            selectionSeparationMargin * 2 + 1,
                                            boundingBox.height + selectionSeparationMargin - heightPointReduction);
        
        selected = (verticalRect != null && verticalRect.intersects(r)) || (horizontalRect!= null && horizontalRect.intersects(r));
    }
}
