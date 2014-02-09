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
    public EditInfo getEditInfo(int n) {
        if (n == 0)
            return new EditInfo("from" , 0.0, 0, 0).
                    addComponent(new JLabel("From: ")).
                    addComponent(new JTextField(from + "", 10));
        if (n == 1)
            return new EditInfo("to" , 0.0, 0, 0).
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
        
        return element;
    }

    @Override
    boolean isWire() {
        return true;
    }
}
