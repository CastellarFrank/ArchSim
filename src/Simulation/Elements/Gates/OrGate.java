/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation.Elements.Gates;

import Exceptions.ArchException;
import java.awt.Point;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class OrGate extends GateElement {

    public OrGate(int x, int y) {
        super(x, y);
    }

    public OrGate(int x, int y, int x2, int y2, String[] extraParams) throws ArchException {
        super(x, y, x2, y2, extraParams);
    }

    @Override
    public void setPoints() {
        super.setPoints();
        Point triPoints[] = newPointArray(38);
        if (this instanceof XorGate) {
            linePoints = new Point[5];
        }
        int i;
        for (i = 0; i != 16; i++) {
            double a = i / 16.;
            double b = 1 - a * a;
            interpolatePoint2(lead1, lead2,
                    triPoints[i], triPoints[32 - i],
                    .5 + a / 2, b * hs2);
        }
        double ww2 = (ww == 0) ? dn * 2 : ww * 2;
        for (i = 0; i != 5; i++) {
            double a = (i - 2) / 2.;
            double b = 4 * (1 - a * a) - 2;
            interpolatePoint(lead1, lead2,
                    triPoints[33 + i], b / (ww2), a * hs2);
            if (this instanceof XorGate) {
                linePoints[i] = interpolatePoint(lead1, lead2,
                        (b - 5) / (ww2), a * hs2);
            }
        }
        triPoints[16] = new Point(lead2);
        if (isInverting()) {
            pcircle = interpolatePoint(point1, point2, .5 + (ww + 4) / dn);
            lead2 = interpolatePoint(point1, point2, .5 + (ww + 8) / dn);
        }
        gatePolygon = createPolygon(triPoints);
    }

    @Override
    public boolean calcFunction() {
        int i;
        boolean f = false;
        for (i = 0; i != inputCount; i++) {
            f |= getInput(i);
        }
        return f;
    }
    
    @Override
    public Element getXmlElement(Document document) {
        Element element = super.getXmlElement(document);
        element.setAttribute("type", OrGate.class.getName());
        
        return element;
    }
}
