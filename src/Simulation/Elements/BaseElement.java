/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation.Elements;

import Exceptions.ArchException;
import GUI.ContainerPanel;
import GUI.Edit.EditInfo;
import GUI.Edit.Editable;
import Simulation.Configuration;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.lang.reflect.Array;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public abstract class BaseElement implements Editable {

    //<editor-fold defaultstate="collapsed" desc="Static Members">
    public static Color selectedColor, whiteColor, defaultColor, postColor = Color.DARK_GRAY;
    public static Color highSignalColor, lowSignalColor,
            unknownSignalColor, highImpedanceSignalColor;
    public static Color textColor;
    public static Font font;    
    public static final double pi = 3.14159265358979323846;
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Instance Members">
    public int ID, voltageSourceReference;
    public Rectangle boundingBox;
    public boolean selected;
    public int x, y, x2, y2, flags, joints[];
    public int dx, dy, dsign;
    public double dn, dpx1, dpy1;
    public Point point1, point2, lead1, lead2;
    public boolean noDiagonal;
    public ContainerPanel containerPanel;
    public double current, curcount;
    private boolean isMovable = true;
    private String id;
    public double voltages[];
    public String binaryValues[];
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public BaseElement(int x, int y) {
        this.x = x;
        this.y = y;
        this.x2 = x;
        this.y2 = y;
        this.flags = 0;
        allocNodes();
        initBoundingBox();
    }

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public BaseElement(int x, int y, int x2, int y2, int flags) {
        this.x = x;
        this.y = y;
        this.x2 = x2;
        this.y2 = y2;
        this.flags = flags;
        allocNodes();
        initBoundingBox();
    }

    /**
     * If you are planning to define custom components you must overload this.
     *
     * @param x starting point's x coordinate
     * @param y starting point's y coordinate
     * @param x2 final point's x coordinate
     * @param y2 final point's y coordinate
     * @param extraParams more params if needed for inheritance
     * @throws ArchException
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public BaseElement(int x, int y, int x2, int y2, String[] extraParams) throws ArchException {
        this.x = x;
        this.y = y;
        this.x2 = x2;
        this.y2 = y2;
        this.flags = 0;
        allocNodes();
        initBoundingBox();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Initialization Methods">
    public void initBoundingBox() {
        boundingBox = new Rectangle();
        boundingBox.setBounds(min(x, x2), min(y, y2),
                abs(x2 - x) + 1, abs(y2 - y) + 1);
    }

    public void allocNodes() {
        joints = new int[getPostCount() + getInternalNodeCount()];
        voltages = new double[getPostCount() + getInternalNodeCount()];
        binaryValues = new String[getPostCount() + getInternalNodeCount()];
        for (int i = 0; i < binaryValues.length; i++) {
            binaryValues[i] = "z";
        }
    }

    public int getPostCount() {
        return 2;
    }

    public int getInternalNodeCount() {
        return 0;
    }

    public void calcLeads(int len) {
        if (dn < len || len == 0) {
            lead1 = point1;
            lead2 = point2;
            return;
        }
        lead1 = interpolatePoint(point1, point2, (dn - len) / (2 * dn));
        lead2 = interpolatePoint(point1, point2, (dn + len) / (2 * dn));
    }

    public void draw2Leads(Graphics g) {
        Color old = g.getColor();
        // draw first lead
        setVoltageColor(g, voltages[0]);
        drawThickLine(g, point1, lead1);

        // draw second lead
        setVoltageColor(g, voltages[1]);
        drawThickLine(g, lead2, point2);

        g.setColor(old);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    //</editor-fold>

    public Element getXmlElement(Document document) {
        Element element = document.createElement("element");
        element.setAttribute("id", Integer.toString(ID));
        element.setAttribute("x", Integer.toString(x));
        element.setAttribute("y", Integer.toString(y));
        element.setAttribute("x2", Integer.toString(x2));
        element.setAttribute("y2", Integer.toString(y2));

        return element;
    }

    @Override
    public EditInfo getEditInfo(int n) {
        return null;
    }

    @Override
    public void setEditValue(int n, EditInfo ei) {
    }
    
    public void setBinaryValue(int index, String value) {
        binaryValues[index] = value;
    }
    
    public void setContainerPanel(ContainerPanel containerPanel) {
        this.containerPanel = containerPanel;
    }

    public void setVoltageColor(Graphics g, double voltage) {
        if (needsHighlight()) {
            g.setColor(BaseElement.selectedColor);
        } else {
            if (voltage >= Configuration.LOGIC_1_VOLTAGE) {
                g.setColor(BaseElement.highSignalColor);
            } else {
                g.setColor(BaseElement.lowSignalColor);
            }
        }
    }
    
    public void setMultiBitsValue(int index, String value) {
        if (index >= binaryValues.length || index < 0) {
            System.out.println("out of range...");
            return;
        }
        binaryValues[index] = value;
    }

    public void setVoltage(int voltageIndex, double newVoltage) {
        if (voltageIndex >= voltages.length || voltageIndex < 0) {
            return;
        }
        voltages[voltageIndex] = newVoltage;
    }

    public void setVoltageSourceReference(int referenceIndex, int reference) {
        if (referenceIndex > getVoltageSourceCount()) {
            return;
        }
        voltageSourceReference = reference;
    }

    public int getVoltageSourceReference() {
        return voltageSourceReference;
    }

    public void setCurrent(double current) {
        this.current = current;
    }

    public void setJointIndex(int postIndex, int jointIndex) {
        joints[postIndex] = jointIndex;
    }

    public boolean collidesWith(int x, int y) {
        return boundingBox.contains(x, y);
    }

    //<editor-fold defaultstate="collapsed" desc="Abstract Methods">
    public abstract void draw(Graphics g);

    public abstract void doStep();

    public void delete() {
    }
    
    public boolean isNonLinear() { return false; }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Static Methods">
    static int abs(int x) {
        return x < 0 ? -x : x;
    }

    static int sign(int x) {
        return (x < 0) ? -1 : (x == 0) ? 0 : 1;
    }

    static int min(int a, int b) {
        return (a < b) ? a : b;
    }

    static int max(int a, int b) {
        return (a > b) ? a : b;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters & Setters">
    public int getVoltageSourceCount() {
        return 0;
    }

    Rectangle getBoundingBox() {
        return boundingBox;
    }

    public void setMovable(boolean isMovable) {
        this.isMovable = isMovable;
    }

    public boolean isMovable() {
        return isMovable;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Selection related">
    public boolean needsHighlight() {
        if (containerPanel == null) {
            return false;
        }
        return containerPanel.mouseComponent == this || selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean x) {
        selected = x;
    }

    public void selectRect(Rectangle r) {
        selected = r.intersects(boundingBox);
    }

    public void drag(int newX, int newY) {
        if (noDiagonal) {
            if (Math.abs(x - newX) < Math.abs(y - newY)) {
                newX = x;
            } else {
                newY = y;
            }
        }
        int difx = x2 - x, dify = y2 - y;
        x = newX;
        y = newY;
        x2 = x + difx;
        y2 = y + dify;

        setPoints();
    }

    public void movePoint2(int newX2, int newY2) {
        x2 = newX2;
        y2 = newY2;

        setPoints();
    }

    public void movePoint(int n, int dx, int dy) {
        if (n == 0) {
            x += dx;
            y += dy;
        } else {
            x2 += dx;
            y2 += dy;
        }
        setPoints();
    }

    public boolean allowMove(int x, int y) {
        if (!isMovable) {
            return false;
        }
        return true;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Utils">
    public Point interceptionPoint(Point pointALine1, Point pointBLine1,
            Point pointALine2, Point pointBLine2) {
        Point result = new Point();
        double m1 = (0.0 + pointALine1.y - pointBLine1.y) / ((pointALine1.x - pointBLine1.x));
        double b = pointALine1.y - m1 * pointALine1.x;
        if (pointALine2.x == pointBLine2.x) {
            result.x = pointBLine2.x;
            result.y = (int) Math.floor(m1 * result.x + b);
            //result.y = (int) (((pointALine1.y - pointBLine1.y + 0.0)/(pointALine1.x - pointBLine1.x))*(pointALine2.x - pointALine1.x) + pointALine1.y);
        } else {
            double m2 = (0.0 + pointALine2.y - pointBLine2.y) / ((pointALine2.x - pointBLine2.x));
            double b2 = pointALine2.y - m2 * pointALine2.x;
            /*
             *    y = m1 * x + b  => -m1 * x + y = b
             *    y = m2 * x + b2 => -m2 * x + y = b2
             */
            double D = (m2 - m1);
            double Dx = b - b2;
            double Dy = -m1 * b2 + m2 * b;

            result.x = (int) (Dx / D);
            result.y = (int) (Dy / D);
        }
        return result;
    }

    public Point[] newPointArray(int n) {
        Point a[] = new Point[n];
        while (n > 0) {
            a[--n] = new Point();
        }
        return a;
    }

    boolean isWire() {
        return false;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Interpolation">
    public Point interpolatePoint(Point a, Point b, double f) {
        Point p = new Point();
        interpolatePoint(a, b, p, f);
        return p;
    }

    public void interpolatePoint(Point a, Point b, Point c, double f) {
        c.x = (int) Math.floor(a.x * (1 - f) + b.x * f + .48);
        c.y = (int) Math.floor(a.y * (1 - f) + b.y * f + .48);
    }

    public void interpolatePoint(Point a, Point b, Point c, double f, double g) {
        int gx = b.y - a.y;
        int gy = a.x - b.x;
        g /= Math.sqrt(gx * gx + gy * gy);
        c.x = (int) Math.floor(a.x * (1 - f) + b.x * f + g * gx + .48);
        c.y = (int) Math.floor(a.y * (1 - f) + b.y * f + g * gy + .48);
    }

    public Point interpolatePoint(Point a, Point b, double f, double g) {
        Point p = new Point();
        interpolatePoint(a, b, p, f, g);
        return p;
    }

    public void interpolatePoint2(Point a, Point b, Point c, Point d, double f, double g) {
        int xpd = b.x - a.x;
        int ypd = b.y - a.y;
        int gx = b.y - a.y;
        int gy = a.x - b.x;
        g /= Math.sqrt(gx * gx + gy * gy);
        c.x = (int) Math.floor(a.x * (1 - f) + b.x * f + g * gx + .48);
        c.y = (int) Math.floor(a.y * (1 - f) + b.y * f + g * gy + .48);
        d.x = (int) Math.floor(a.x * (1 - f) + b.x * f - g * gx + .48);
        d.y = (int) Math.floor(a.y * (1 - f) + b.y * f - g * gy + .48);
    }

    public Point linearInterpolation(Point a, Point b, double percentaje) {
        Point c = new Point();
        linearInterpolation(a, b, c, percentaje);
        return c;
    }

    public Point linearInterpolation(Point a, Point b, Point c, double percentage) {
        int Dx = a.x - b.x;
        int Dy = a.y - b.y;

        c.x = (int) Math.floor(a.x + Dx * percentage);
        c.y = (int) Math.floor(a.y + Dy * percentage);
        return c;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Drawing related">    
    public void drawDots(Graphics g, Point pa, Point pb, double pos) {
        //if (sim.stoppedCheck.getState() || pos == 0 || !sim.dotsCheckItem.getState())
        //    return;
        int Dx = pb.x - pa.x;
        int Dy = pb.y - pa.y;
        double Dn = Math.sqrt(Dx * Dx + Dy * Dy);
        g.setColor(Color.yellow);
        int ds = 16;
        pos %= ds;
        if (pos < 0) {
            pos += ds;
        }
        double di;
        for (di = pos; di < Dn; di += ds) {
            int x0 = (int) (pa.x + di * Dx / Dn);
            int y0 = (int) (pa.y + di * Dy / Dn);
            g.fillRect(x0 - 1, y0 - 1, 4, 4);
        }
    }

    public void drawPost(Graphics g, int x0, int y0, int n) {
        if (containerPanel.newElementBeenDrawn == null && !needsHighlight()
                && containerPanel.getJoint(n) != null
                && containerPanel.getJoint(n).references.size() == 2) {
            return;
        }
        /*if (containerPanel.currentMouseMode == MouseMode.SELECT_DRAG
                || containerPanel.currentMouseMode == MouseMode.MOVE_ALL) {
            return;
        }*/
        drawPost(g, x0, y0);
    }

    public void drawPost(Graphics g, int x0, int y0) {
        Color old = g.getColor();
        g.setColor(BaseElement.postColor);
        g.fillOval(x0 - 3, y0 - 3, 7, 7);
        g.setColor(old);
    }

    public void drawPosts(Graphics g) {
        int i;
        for (i = 0; i != getPostCount(); i++) {
            Point p = getPost(i);
            drawPost(g, p.x, p.y, joints[i]);
        }
    }

    public Point getPost(int n) {
        return (n == 0) ? point1 : (n == 1) ? point2 : null;
    }

    public void setBbox(int x1, int y1, int x2, int y2) {
        if (x1 > x2) {
            int q = x1;
            x1 = x2;
            x2 = q;
        }
        if (y1 > y2) {
            int q = y1;
            y1 = y2;
            y2 = q;
        }
        boundingBox.setBounds(x1, y1, x2 - x1 + 1, y2 - y1 + 1);
    }

    public void setBbox(Point p1, Point p2, double w) {
        setBbox(p1.x, p1.y, p2.x, p2.y);
        int gx = p2.y - p1.y;
        int gy = p1.x - p2.x;
        int dpx = (int) (dpx1 * w);
        int dpy = (int) (dpy1 * w);
        adjustBbox(p1.x + dpx, p1.y + dpy, p1.x - dpx, p1.y - dpy);
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
        x2 += dx;
        y2 += dy;
        boundingBox.move(dx, dy);
        setPoints();
    }
    
    public boolean contains(int x, int y) {
        return boundingBox.contains(x, y);
    }

    public void setPoints() {
        dx = x2 - x;
        dy = y2 - y;
        dn = Math.sqrt(dx * dx + dy * dy);
        dpx1 = dy / dn;
        dpy1 = -dx / dn;
        dsign = (dy == 0) ? sign(dx) : sign(dy);
        point1 = new Point(x, y);
        point2 = new Point(x2, y2);
    }

    public Polygon calcArrow(Point a, Point b, double al, double aw) {
        Polygon poly = new Polygon();
        Point p1 = new Point();
        Point p2 = new Point();
        int adx = b.x - a.x;
        int ady = b.y - a.y;
        double l = Math.sqrt(adx * adx + ady * ady);
        poly.addPoint(b.x, b.y);
        interpolatePoint2(a, b, p1, p2, 1 - al / l, aw);
        poly.addPoint(p1.x, p1.y);
        poly.addPoint(p2.x, p2.y);
        return poly;
    }

    public Polygon createPolygon(Point a, Point b, Point c) {
        Polygon p = new Polygon();
        p.addPoint(a.x, a.y);
        p.addPoint(b.x, b.y);
        p.addPoint(c.x, c.y);
        return p;
    }

    public Polygon createPolygon(Point a, Point b, Point c, Point d) {
        Polygon p = new Polygon();
        p.addPoint(a.x, a.y);
        p.addPoint(b.x, b.y);
        p.addPoint(c.x, c.y);
        p.addPoint(d.x, d.y);
        return p;
    }

    public Polygon createPolygon(Point a[]) {
        Polygon p = new Polygon();
        int i;
        for (i = 0; i != a.length; i++) {
            p.addPoint(a[i].x, a[i].y);
        }
        return p;
    }

    public void adjustBbox(int x1, int y1, int x2, int y2) {
        if (x1 > x2) {
            int q = x1;
            x1 = x2;
            x2 = q;
        }
        if (y1 > y2) {
            int q = y1;
            y1 = y2;
            y2 = q;
        }
        x1 = min(boundingBox.x, x1);
        y1 = min(boundingBox.y, y1);
        x2 = max(boundingBox.x + boundingBox.width - 1, x2);
        y2 = max(boundingBox.y + boundingBox.height - 1, y2);
        boundingBox.setBounds(x1, y1, x2 - x1, y2 - y1);
    }

    public void adjustBbox(Point p1, Point p2) {
        adjustBbox(p1.x, p1.y, p2.x, p2.y);
    }

    public boolean isCenteredText() {
        return false;
    }

    public void drawText(Graphics g, String text) {
        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(text);
        int textX = x2, oldTextX = x2;

        if (x < x2) {
            //textX += w;
        } else {
            textX -= w;
        }

        g.drawString(text, textX, (int) (y2 + fm.getAscent() / 2.0));
        adjustBbox(textX, y - fm.getAscent() / 2, 
                oldTextX, y + fm.getAscent() / 2 + fm.getDescent());
    }

    public void drawCenteredText(Graphics g, String s, int x, int y, boolean cx) {
        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(s);
        if (cx) {
            x -= w / 2;
        }
        g.drawString(s, x, y + fm.getAscent() / 2);
        adjustBbox(x, y - fm.getAscent() / 2,
                x + w, y + fm.getAscent() / 2 + fm.getDescent());
    }

    public static void drawThickLine(Graphics g, int x, int y, int x2, int y2) {
        g.drawLine(x, y, x2, y2);
        g.drawLine(x + 1, y, x2 + 1, y2);
        g.drawLine(x, y + 1, x2, y2 + 1);
        g.drawLine(x + 1, y + 1, x2 + 1, y2 + 1);
    }

    public static void drawThickLine(Graphics g, Point pa, Point pb) {
        g.drawLine(pa.x, pa.y, pb.x, pb.y);
        g.drawLine(pa.x + 1, pa.y, pb.x + 1, pb.y);
        g.drawLine(pa.x, pa.y + 1, pb.x, pb.y + 1);
        g.drawLine(pa.x + 1, pa.y + 1, pb.x + 1, pb.y + 1);
    }

    public static void drawThickPolygon(Graphics g, int xs[], int ys[], int c) {
        int i;
        for (i = 0; i != c - 1; i++) {
            drawThickLine(g, xs[i], ys[i], xs[i + 1], ys[i + 1]);
        }
        drawThickLine(g, xs[i], ys[i], xs[0], ys[0]);
    }

    public static void drawThickPolygon(Graphics g, Polygon p) {
        drawThickPolygon(g, p.xpoints, p.ypoints, p.npoints);
    }

    public static void drawThickCircle(Graphics g, int cx, int cy, int ri) {
        int a;
        double m = pi / 180;
        double r = ri * .98;
        for (a = 0; a != 360; a += 20) {
            double ax = Math.cos(a * m) * r + cx;
            double ay = Math.sin(a * m) * r + cy;
            double bx = Math.cos((a + 20) * m) * r + cx;
            double by = Math.sin((a + 20) * m) * r + cy;
            drawThickLine(g, (int) ax, (int) ay, (int) bx, (int) by);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Simulation">
    public void stampVoltages() {
    }

    public boolean thereIsConnectionBetween(int elementA, int elementB) {
        return true;
    }

    public boolean hasGroundConnection(int index) {
        return false;
    }
    //</editor-fold>
}
