/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation.Elements;

import DataStructures.ModuleInfo;
import DataStructures.ModuleRepository;
import DataStructures.PortInfo;
import Exceptions.ArchException;
import Exceptions.ModuleNotFoundException;
import Simulation.Configuration;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class ModuleChip extends BaseElement {

    //<editor-fold defaultstate="collapsed" desc="Instance Attributes">
    ModuleInfo moduleInfo;
    PortElement ports[];
    int csize, cspc, cspc2;
    int rectPointsX[], rectPointsY[];
    int sizeX, sizeY;
    int southPosition = 0, northPosition = 0, westPosition = 0, eastPosition = 0;
    int maxLengthPortEast = 0, maxLengthPortWest = 0, width = 0;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public ModuleChip(int x, int y, String[] extraParams) throws ArchException {
        super(x, y);

        initialize(extraParams);
    }

    public ModuleChip(int x, int y, int x2, int y2, String[] extraParams) throws ArchException {
        super(x, y, x2, y2, extraParams);

        initialize(extraParams);
    }

    public ModuleChip(ModuleInfo info, int x, int y, int x2, int y2, int flags, String[] extraParams) 
            throws ArchException {
        super(x, y, x2, y2, flags);

        initialize(extraParams);
    }

    private void initialize(String[] extraParams) throws ModuleNotFoundException {
        moduleInfo = ModuleRepository.getInstance().getModuleInfo(extraParams[0]);
        if (moduleInfo == null) {
            throw new ModuleNotFoundException(extraParams[0]);
        }
        setupPorts(moduleInfo);
        setSize(1);
        allocNodes();
        setPoints();
    }
    //</editor-fold>    

    public void setupPorts(ModuleInfo info) {
        sizeX = 4;
        sizeY = 4;
        ports = new PortElement[info.getPortsInfo().size()];

        for (int i = 0; i < ports.length; i++) {
            PortInfo portInfo = info.getPortInfo(i);
            if (portInfo.position == PortPosition.EAST)
                maxLengthPortEast = max(maxLengthPortEast, portInfo.portName.length());
            if (portInfo.position == PortPosition.WEST)
                maxLengthPortWest = max(maxLengthPortWest, portInfo.portName.length());
            int position = 0;
            switch (portInfo.position) {
                case EAST:
                    position = eastPosition++;
                    break;
                case NORTH:
                    position = northPosition++;
                    break;
                case SOUTH:
                    position = southPosition++;
                    break;
                case WEST:
                    position = westPosition++;
                    break;
            }
            ports[i] = new PortElement(position, portInfo.position, portInfo.portName);
        }
    }

    public ModuleInfo getModuleInfo() {
        return moduleInfo;
    }

    public String getModuleName() {
        if (moduleInfo != null) {
            return moduleInfo.getModuleName();
        }
        return null;
    }

    void setSize(int s) {
        csize = s;
        cspc = (8 * s);
        cspc2 = (cspc * 2);
        width = cspc2 * ((maxLengthPortEast + maxLengthPortWest)/10 + 1);
        width = max(width, cspc2 * (moduleInfo.getModuleName().length()/10+1));
    }

    @Override
    public void setPoints() {
        super.setPoints();
        /*if (x2 - x > sizeX * cspc2 && containerPanel != null && this == containerPanel.newElementBeenDrawn) {
            setSize(2);
        }*/
        int hs = cspc;
        int x0 = x + cspc2;
        int y0 = y;
        int xr = x0 - cspc;
        int yr = y0 - cspc;
        int xs = sizeX * width;
        double max = max(westPosition, eastPosition);
        max = ((int) max / 4) + (max % 4 == 0 ? 0 : max % 4 == 1 || max % 4 == 2 ? 0.5 : 1);
        int ys = (int) (sizeY * cspc2 * (max == 0? 1: max));
        rectPointsX = new int[]{xr, xr + xs, xr + xs, xr};
        rectPointsY = new int[]{yr, yr, yr + ys, yr + ys};
        setBbox(xr, yr, rectPointsX[2], rectPointsY[2]);
        int i;
        for (i = 0; i != getPostCount(); i++) {
            PortElement p = ports[i];
            switch (p.side) {
                case NORTH:
                    p.setPoint(x0, y0, 1, 0, 0, -1, 0, 0);
                    break;
                case SOUTH:
                    p.setPoint(x0, y0, 1, 0, 0, 1, 0, ys - cspc2);
                    break;
                case WEST:
                    p.setPoint(x0, y0, 0, 1, -1, 0, 0, 0);
                    break;
                case EAST:
                    p.setPoint(x0, y0, 0, 1, 1, 0, xs - cspc2, 0);
                    break;
            }
        }
    }

    @Override
    public void draw(Graphics g) {
        if (Configuration.DEBUG_MODE) {
            Color old = g.getColor();
            g.setColor(Color.BLUE);
            g.drawRect(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
            g.setColor(old);
        }
        drawChip(g);
    }

    public void drawChip(Graphics g) {
        int i;
        Font f = new Font("SansSerif", 0, 10 * csize);
        g.setFont(f);
        FontMetrics fm = g.getFontMetrics();
        for (i = 0; i != getPostCount(); i++) {
            PortElement p = ports[i];
            int displacement = 0;            
            setVoltageColor(g, voltages[i]);
            Point a = p.post;
            Point b = p.stub;
            drawThickLine(g, a, b);
            g.setColor(BaseElement.textColor);
            int sw = fm.stringWidth(p.text);
            if (p.side == PortPosition.EAST)
                displacement = sw;
            g.drawString(p.text, p.textloc.x - displacement,
                    p.textloc.y + fm.getAscent() / 2);
        }
        g.setColor(needsHighlight() ? BaseElement.selectedColor : BaseElement.defaultColor);
        drawThickPolygon(g, rectPointsX, rectPointsY, 4);
        for (i = 0; i != getPostCount(); i++) {
            drawPost(g, ports[i].post.x, ports[i].post.y, joints[i]);
        }
        
        Font newFont = new Font("SansSerif", Font.BOLD, 11 * csize);
        g.setFont(newFont);
        g.drawString(moduleInfo.getModuleName(), 
                (rectPointsX[0] + rectPointsX[1] - fm.stringWidth(moduleInfo.getModuleName()))/2, 
                rectPointsY[2] - (fm.getAscent())/2);
    }

    @Override
    public void doStep() {
    }

    @Override
    public int getPostCount() {
        if (ports == null) {
            return 0;
        }
        return ports.length;
    }

    @Override
    public Point getPost(int n) {
        return ports[n].post;
    }

    @Override
    public Element getXmlElement(Document document) {
        Element element = super.getXmlElement(document);
        element.setAttribute("type", ModuleChip.class.getName());
        
        Element extraParam0 = document.createElement("param");
        extraParam0.setTextContent(moduleInfo.getModuleName());
        
        element.appendChild(extraParam0);

        return element;
    }

    class PortElement {
        //<editor-fold defaultstate="collapsed" desc="Instance Attributes">

        Point post, stub;
        Point textloc;
        int pos, voltSource, bubbleX, bubbleY;
        PortPosition side;
        String text;
        boolean lineOver, bubble, clock, output, value, state;
        double curcount, current;
        //</editor-fold>

        PortElement(int p, PortPosition side, String text) {
            pos = p;
            this.side = side;
            this.text = text;
        }

        void setPoint(int px, int py, int dx, int dy, int dax, int day,
                int sx, int sy) {
            /*if ((flags & FLAG_FLIP_X) != 0) {
             dx = -dx;
             dax = -dax;
             px += cspc2*(sizeX-1);
             sx = -sx;
             }*/
            /*if ((flags & FLAG_FLIP_Y) != 0) {
             dy = -dy;
             day = -day;
             py += cspc2*(sizeY-1);
             sy = -sy;
             }*/
            int xa = px + cspc2 * dx * pos + sx;
            int ya = py + cspc2 * dy * pos + sy;
            post = new Point(xa + dax * cspc2, ya + day * cspc2);
            stub = new Point(xa + dax * cspc, ya + day * cspc);
            textloc = new Point(xa, ya);
            /*if (bubble) {
             bubbleX = xa+dax*10*csize;
             bubbleY = ya+day*10*csize;
             }
             if (clock) {
             clockPointsX = new int[3];
             clockPointsY = new int[3];
             clockPointsX[0] = xa+dax*cspc-dx*cspc/2;
             clockPointsY[0] = ya+day*cspc-dy*cspc/2;
             clockPointsX[1] = xa;
             clockPointsY[1] = ya;
             clockPointsX[2] = xa+dax*cspc+dx*cspc/2;
             clockPointsY[2] = ya+day*cspc+dy*cspc/2;
             }*/
        }
    }
}
