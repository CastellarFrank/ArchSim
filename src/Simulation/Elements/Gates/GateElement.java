/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation.Elements.Gates;

import Exceptions.ArchException;
import GUI.Edit.EditInfo;
import Simulation.Configuration;
import Simulation.Elements.BaseElement;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public abstract class GateElement extends BaseElement {

    //<editor-fold defaultstate="collapsed" desc="Instance Attributes">
    int inputCount = 2, ww;
    Point inPosts[], inGates[];    
    boolean lastOutput;
    Polygon gatePolygon;
    Point pcircle, linePoints[];
    int gsize, gwidth, gwidth2, gheight, hs2;
    //</editor-fold>    

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public GateElement(int x, int y) {
        super(x, y);
        inputCount = 2;
        setSize(1);
        setPoints();
    }

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public GateElement(int x, int y, int x2, int y2, String[] extraParams) throws ArchException {
        super(x, y, x2, y2, extraParams);
        if (extraParams != null && extraParams.length > 0)
            setInputCount(Integer.parseInt(extraParams[0]));
        setSize(1);
        setPoints();
    }
    //</editor-fold>   

    public void setSize(int s) {
        gsize = s;
        gwidth = 7 * s;
        gwidth2 = 14 * s;
        gheight = 8 * s;
    }

    public boolean isInverting() {
        return false;
    }

    public void setInputCount(int inputCount) {
        this.inputCount = inputCount;
        allocNodes();
        setPoints();
    }
    
    @Override
    public void draw(Graphics g) {
        
        if (Configuration.DEBUG_MODE) {
            Color old = g.getColor();
            g.setColor(Color.BLUE);
            g.drawRect(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
            g.setColor(old);
        }
        
        int i;
        for (i = 0; i != inputCount; i++) {
            setVoltageColor(g, voltages[i]);
            drawThickLine(g, inPosts[i], inGates[i]);
        }
        setVoltageColor(g, voltages[inputCount]);
        drawThickLine(g, lead2, point2);
        g.setColor(needsHighlight() ? BaseElement.selectedColor : BaseElement.defaultColor);
        drawThickPolygon(g, gatePolygon);
        if (linePoints != null) {
            for (i = 0; i != linePoints.length - 1; i++) {
                drawThickLine(g, linePoints[i], linePoints[i + 1]);
            }
        }
        if (isInverting()) {
            drawThickCircle(g, pcircle.x, pcircle.y, 3);
        }
        //curcount = updateDotCount(current, curcount);
        //drawDots(g, lead2, point2, 0);
        drawPosts(g);
    }
    
    @Override
    public void setVoltageColor(Graphics g, double voltage) {
        if (needsHighlight()) {
            g.setColor(BaseElement.selectedColor);
        } else {
            g.setColor(BaseElement.defaultColor);
        }
    }

    @Override
    public Point getPost(int n) {
        if (n == inputCount) {
            return point2;
        }
        return inPosts[n];
    }

    @Override
    public void setPoints() {
        super.setPoints();
        /*if (dn > 150 && this == containerPanel.newElementBeenDrawn) {
            setSize(2);
        }*/
        int hs = gheight;
        int i;
        ww = gwidth2;
        if (ww > dn / 2) {
            ww = (int) (dn / 2);
        }
        if (isInverting() && ww + 8 > dn / 2) {
            ww = (int) (dn / 2 - 8);
        }
        calcLeads(ww * 2);
        inPosts = new Point[inputCount];
        inGates = new Point[inputCount];
        allocNodes();
        int i0 = -inputCount / 2;
        for (i = 0; i != inputCount; i++, i0++) {
            if (i0 == 0 && (inputCount & 1) == 0) {
                i0++;
            }
            inPosts[i] = interpolatePoint(point1, point2, 0, hs * i0);
            inGates[i] = interpolatePoint(lead1, lead2, 0, hs * i0);
        }
        hs2 = gwidth * (inputCount / 2 + 1);
        setBbox(point1, point2, hs2);
    }

    @Override
    public int getPostCount() {
        return inputCount + 1;
    }

    public boolean getInput(int index) {
        return voltages[index] > Configuration.LOGIC_1_VOLTAGE;
    }

    abstract boolean calcFunction();

    @Override
    public void doStep() {
        boolean f = calcFunction();
        if (isInverting()) {
            f = !f;
        }
        lastOutput = f;
        double res = f ? 5 : 0;
        //sim.updateVoltageSource(0, nodes[inputCount], voltSource, res);
    }

    @Override
    public EditInfo getEditInfo(int n) {
        if (n == 0)
            return new EditInfo("inputCount", inputCount, 2, 32).
                    addComponent(new JLabel("Inputs: ")).
                    addComponent(new JTextField(inputCount+"", 10));
        return null;
    }

    @Override
    public void setEditValue(int n, EditInfo editInfo) {
        if (n == 0) {
            setInputCount((int)editInfo.value);
            
        }
    }
    
}
