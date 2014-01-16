/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation.Elements.Gates;

import Exceptions.ArchException;
import Simulation.Configuration;
import Simulation.Elements.BaseElement;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class NotGate extends BaseElement {

    public NotGate(int x, int y) {
        super(x, y);
    }

    public NotGate(int x, int y, int x2, int y2, String[] extraParams) throws ArchException {
        super(x, y, x2, y2, extraParams);
    }

    Polygon polygon;
    Point littleCircle;

    @Override
    public void setPoints() {
        super.setPoints();
        int hs = 16;
        int ww = 16;
        if (ww > dn / 2) {
            ww = (int) (dn / 2);
        }
        lead1 = interpolatePoint(point1, point2, .5 - ww / dn);
        lead2 = interpolatePoint(point1, point2, .5 + (ww + 2) / dn);
        littleCircle = interpolatePoint(point1, point2, .5 + (ww - 2) / dn);
        Point triPoints[] = newPointArray(3);
        interpolatePoint2(lead1, lead2, triPoints[0], triPoints[1], 0, hs);
        triPoints[2] = interpolatePoint(point1, point2, .5 + (ww - 5) / dn);
        polygon = createPolygon(triPoints);
        setBbox(point1, point2, hs);
    }

    @Override
    public void draw(Graphics g) {
        if (Configuration.DEBUG_MODE) {
            Color old = g.getColor();
            g.setColor(Color.BLUE);
            g.drawRect(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
            g.setColor(old);
        }
        
        Color old = g.getColor();
        g.setColor(needsHighlight() ? BaseElement.selectedColor : 
                BaseElement.defaultColor);
        drawThickPolygon(g, polygon);
        draw2Leads(g);        
        drawThickCircle(g, littleCircle.x, littleCircle.y, 3);
        
        g.setColor(old);
        drawPosts(g);
    }
    
    @Override
    public int getVoltageSourceCount() { 
        return 1;
    }

    @Override
    public void stampVoltages() {
        containerPanel.stampVoltageSource(0, joints[1], voltageSourceReference);
    }
    
    @Override
    public boolean hasGroundConnection(int index) {
        return (index == 1);
    }
    
    @Override
    public boolean thereIsConnectionBetween(int elementA, int elementB) {
        return false;
    }

    @Override
    public void doStep() {
        double output;
        if (voltages[0] >= Configuration.LOGIC_1_VOLTAGE)
            output = Configuration.LOGIC_0_VOLTAGE;
        else
            output = Configuration.LOGIC_1_VOLTAGE * 2;
        containerPanel.updateVoltageSource(voltageSourceReference, output);
    }
}
