/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation.Elements.Gates;

import Exceptions.ArchException;
import Simulation.Elements.BaseElement;
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
        drawPosts(g);
        draw2Leads(g);
        g.setColor(needsHighlight() ? BaseElement.selectedColor : 
                BaseElement.defaultColor);
        drawThickPolygon(g, polygon);
        drawThickCircle(g, littleCircle.x, littleCircle.y, 3);
    }

    @Override
    public void doStep() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
