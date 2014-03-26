/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation.Elements;

import Exceptions.ArchException;
import GUI.Edit.EditInfo;
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
        Point extra1 = new Point(point1.x, point2.y);
        drawThickLine(g, point1, extra1);
        drawThickLine(g, extra1, point2);
        setBbox(point1, point2, 3);
        drawPosts(g);
        //System.out.println("volt1 " + binaryValues[0]);
        //System.out.println("volt2 " + binaryValues[1]);
    }

    @Override
    public void stampVoltages() {
        String select = "z";
        if (binaryValues != null && binaryValues[0] != null) {
            int size = to - from + 1;
            if (size > binaryValues[0].length()) {
                String extra = new String(new char[size - binaryValues[0].length()])
                        .replace('\0', '0');
                binaryValues[0] = extra + binaryValues[0];
            }
            select = binaryValues[0].substring(from, to+1);
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
        if (n == 0)
            return new EditInfo("from" , 0.0, 0, binaryValues[0].length() - 1).
                    addComponent(new JLabel("From: ")).
                    addComponent(new JTextField(from + "", 10));
        if (n == 1)
            return new EditInfo("to" , 0.0, 0, binaryValues[0].length() - 1).
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
}
