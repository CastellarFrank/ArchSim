/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation.Elements;

import Exceptions.ArchException;
import GUI.Edit.EditInfo;
import Simulation.Configuration;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class Multiplexor extends BaseElement {

    //<editor-fold defaultstate="collapsed" desc="Instance Attributes">
    int inputCount, controlSignalCount;
    Point inPosts[], inMult[], inControl[], inControlMult[], vLeadBottom, vLeadTop;
    Polygon multPolygon;
    final int FLAG_SMALL = 1;
    int size, width, width2, height, height2, hs2, ww;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Multiplexor(int x, int y) {
        super(x, y);
        noDiagonal = true;
        setInputCount(2);
        setSize(1);
    }

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public Multiplexor(int x, int y, int x2, int y2, String[] extraParams) throws ArchException {
        super(x, y, x2, y2, extraParams);
        noDiagonal = true;
        setSize(1);
        setInputCount(Integer.parseInt(extraParams[0]));
        setPoints();
    }

    public Multiplexor(int x, int y, int x2, int y2, int flags) {
        super(x, y, x2, y2, flags);
        noDiagonal = true;
    }
    //</editor-fold>    

    @Override
    public int getInternalNodeCount() {
        return controlSignalCount;
    }

    @Override
    public void allocNodes() {
        super.allocNodes();
        inControl = new Point[controlSignalCount];
        inControlMult = new Point[controlSignalCount];
    }

    @Override
    public int getPostCount() {
        return inputCount + controlSignalCount + 1;
    }

    public boolean getInput(int index) {
        return voltages[index] > Configuration.LOGIC_1_VOLTAGE;
    }

    public boolean getControlSignal(int index) {
        return voltages[getPostCount() + index] > Configuration.LOGIC_1_VOLTAGE;
    }

    public final void setInputCount(int inputCount) {
        this.inputCount = inputCount;
        this.controlSignalCount = (int) Math.round(Math.ceil(Math.log10(inputCount) / Math.log10(2)));
        allocNodes();
        setPoints();
    }

    @Override
    public void movePoint(int n, int dx, int dy) {
        if (n == 0) {
            x += dx;
        } else {
            x2 += dx;
        }
        y += dy;
        y2 += dy;
        setPoints();
    }

    public final void setSize(int s) {
        size = s;
        width = 7 * s;
        width2 = 14 * s;
        height = 8 * s;
        height2 = 16 * s;
    }

    void calcVerticalLeads(int len) {
        if (dn < len || len == 0) {
            vLeadBottom = point1;
            vLeadTop = point2;
            return;
        }
        vLeadBottom = interpolatePoint(point1, point2, (dn - len) / (2 * dn));
        vLeadTop = interpolatePoint(point1, point2, (dn + len) / (2 * dn));
    }

    @Override
    public void setPoints() {
        super.setPoints();
        /*if (dn > 150 && this == containerPanel.newElementBeenDrawn) {
            setSize(2);
        }*/
        int hs = height;
        int i;
        ww = width2; // was 24
        if (ww > dn / 2) {
            ww = (int) (dn / 2);
        }
        calcLeads(ww * (controlSignalCount / 2 + 2));
        calcVerticalLeads(height2);
        inPosts = new Point[inputCount];
        inMult = new Point[inputCount];
        allocNodes();
        int i0 = -inputCount / 2;
        for (i = 0; i != inputCount; i++, i0++) {
            if (i0 == 0 && (inputCount & 1) == 0) {
                i0++;
            }
            inPosts[i] = interpolatePoint(point1, point2, 0, hs * i0);
            inMult[i] = interpolatePoint(lead1, lead2, 0, hs * i0);
        }
        hs2 = width * (inputCount / 2 + 1) + 20;

        setBbox(point1, point2, hs2);

        multPolygon = createMultiplexorPolygon();

        i0 = -controlSignalCount;
        for (i = 0; i != controlSignalCount; i++, i0++) {
            inControl[i] = linearInterpolation(lead2, lead1,
                    (1.0 * i0) / (controlSignalCount + 1));
            inControl[i].y = boundingBox.y - 10;
            if (containerPanel != null) {
                inControl[i].x = containerPanel.snapGrid(inControl[i].x);
                inControl[i].y = containerPanel.snapGrid(inControl[i].y);
            }
            inControlMult[i] = interceptionWithPolygon(inControl[i],
                    new Point(inControl[i].x, inControl[i].y + 1));

        }
    }

    private Point interceptionWithPolygon(Point pointA, Point pointB) {
        Point lp1 = new Point(lead1.x, boundingBox.y);
        Point lp2 = new Point(lead2.x, (int) (boundingBox.y + boundingBox.height * 0.2));
        return interceptionPoint(lp1, lp2, pointA, pointB);
    }

    private Polygon createMultiplexorPolygon() {
        Point[] points = new Point[4];
        points[0] = new Point();
        points[0].x = lead1.x;
        points[0].y = boundingBox.y + boundingBox.height;

        points[1] = new Point();
        points[1].x = lead2.x;
        points[1].y = (int) (points[0].y - boundingBox.height * 0.20);

        points[3] = new Point();
        points[3].x = lead1.x;
        points[3].y = boundingBox.y;

        points[2] = new Point();
        points[2].x = lead2.x;
        points[2].y = (int) (points[3].y + boundingBox.height * 0.20);

        return createPolygon(points);
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
            drawThickLine(g, inPosts[i], inMult[i]);
        }
        setVoltageColor(g, voltages[inputCount]);
        drawThickLine(g, lead2, point2);

        for (i = 0; i != controlSignalCount; i++) {
            setVoltageColor(g, voltages[inputCount + i]);
            drawThickLine(g, inControl[i], inControlMult[i]);
        }

        g.setColor(needsHighlight() ? BaseElement.selectedColor : BaseElement.defaultColor);
        drawThickPolygon(g, multPolygon);
        //curcount = updateDotCount(current, curcount);
        //drawDots(g, lead2, point2, 0);
        drawPosts(g);
        drawControlSignals(g);
    }

    //<editor-fold defaultstate="collapsed" desc="Draw Signals">
    public void drawSignal(Graphics g, int x0, int y0, int n) {
        /*if (panel.dragElm == null && !needsHighlight() &&
         
         panel.getCircuitNode(n).links.size() == 2)
         return;
         if (panel.mouseMode == CirSim.MODE_DRAG_ROW ||
         panel.mouseMode == CirSim.MODE_DRAG_COLUMN)
         return;*/
        drawSignal(g, x0, y0);
    }

    public void drawSignal(Graphics g, int x0, int y0) {
        Color old = g.getColor();
        g.setColor(BaseElement.postColor);
        g.fillOval(x0 - 3, y0 - 3, 7, 7);
        g.setColor(old);
    }
    //</editor-fold>

    public void drawControlSignals(Graphics g) {
        int i;
        for (i = 0; i != controlSignalCount; i++) {
            Point p = getSignal(i);
            drawPost(g, p.x, p.y, joints[inputCount + i + 1]);  
        }
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
        if (n > inputCount) {
            return getSignal(n - inputCount - 1);
        }
        return inPosts[n];
    }

    public Point getSignal(int n) {
        return inControl[n];
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
            setInputCount(Integer.parseInt(editInfo.value));
        }
    }

    @Override
    public boolean isPostOutput(int index) {
        return index == inputCount + controlSignalCount;
    }

    @Override
    public void doStep() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Element getXmlElement(Document document) {
        Element element = super.getXmlElement(document);
        element.setAttribute("type", Multiplexor.class.getName());

        Element extraParam0 = document.createElement("param");
        extraParam0.setTextContent(Integer.toString(inputCount));
        
        element.appendChild(extraParam0);

        return element;
    }
}
