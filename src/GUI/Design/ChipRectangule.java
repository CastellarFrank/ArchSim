/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Design;

import Exceptions.ArchException;
import Exceptions.NoExtraParamsException;
import Simulation.Elements.BaseElement;
import VerilogCompiler.SyntacticTree.Others.Port;
import VerilogCompiler.SyntacticTree.PortDirection;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class ChipRectangule extends BaseElement {

    //<editor-fold defaultstate="collapsed" desc="Instance Attributes">
    int csize, cspc, cspc2;
    int rectPointsX[], rectPointsY[];
    int sizeX, sizeY, textX = 0, textY = 0, textWidth, textHeight;
    int southPosition = 0, northPosition = 0, westPosition = 0, eastPosition = 0;
    int maxLengthPortEast = 0, maxLengthPortWest = 0, width = 0;
    String moduleName;
    Rectangle textBoundingBox;
    boolean selectedText, portSelected;
    int portSelectedIndex = -1;
    ArrayList<ChipPort> ports;
    //</editor-fold>

    public ChipRectangule(int x, int y) {
        super(x, y);
    }

    public ChipRectangule(int x, int y, int x2, int y2, String[] extraParams) throws ArchException {
        super(x, y, x2, y2, extraParams);

        if (extraParams == null || extraParams.length == 0) {
            throw new NoExtraParamsException("module name (extraParams[0] expected");
        }

        moduleName = extraParams[0];
    }

    public void init(ArrayList<Port> portDefs) {
        ports = new ArrayList<ChipPort>();
        sizeX = sizeY = 4;

        setupPorts(portDefs);
        setSize(1);
        allocNodes();
        setPoints();
    }
    
    public void init(ArrayList<Port> portDefs, Element info) {
        ports = new ArrayList<ChipPort>();
        sizeX = sizeY = 4;        
                
        textX = Integer.parseInt(info.getAttribute("textX"));
        textY = Integer.parseInt(info.getAttribute("textY"));
        
        NodeList portInfos = info.getElementsByTagName("port");
        
        setupPorts(portDefs, portInfos);
        setSize(1);
        
        x = Integer.parseInt(info.getAttribute("x")) - cspc2;
        y = Integer.parseInt(info.getAttribute("y"));
        x2 = Integer.parseInt(info.getAttribute("x2"));
        y2 = Integer.parseInt(info.getAttribute("y2"));
        
        allocNodes();
        setPoints();
    }
    
    public void setupPorts(ArrayList<Port> portDefs, NodeList portInfos) {
        sizeX = 4;
        sizeY = 4;
        
        HashMap<String, Element> efficientPortInfo = new HashMap<String, Element>();
        for (int i = 0; i < portInfos.getLength(); i++) {
            Element port = (Element) portInfos.item(i);
            efficientPortInfo.put(port.getAttribute("portName"), port);
        }

        for (int i = 0; i < portDefs.size(); i++) {
            Port portInfo = portDefs.get(i);
            boolean isVertical, leftOrBottom, isOutput;
            Element portData = efficientPortInfo.get(portInfo.getIdentifier());
            
            isVertical = Boolean.parseBoolean(portData.getAttribute("isVertical"));
            leftOrBottom = Boolean.parseBoolean(portData.getAttribute("leftOrBottom"));
            isOutput = Boolean.parseBoolean(portData.getAttribute("isOutput"));
            
            int savedPosition = Integer.parseInt(portData.getAttribute("position"));
            
            if (portInfo.getDirection() == PortDirection.OUTPUT) {
                maxLengthPortEast = Math.max(maxLengthPortEast, portInfo.getIdentifier().length());
            }
            if (portInfo.getDirection() == PortDirection.INPUT) {
                maxLengthPortWest = Math.max(maxLengthPortWest, portInfo.getIdentifier().length());
            }
            ChipPort newPort = new ChipPort(savedPosition, portInfo.getIdentifier());
            switch (portInfo.getDirection()) {
                case OUTPUT:
                    eastPosition++;
                    break;
                case INPUT:
                    westPosition++;
                    break;
            }
            newPort.isOutput = isOutput;
            newPort.isVertical = isVertical;
            newPort.leftOrBottom = leftOrBottom;
            newPort.parent = this;
            
            ports.add(newPort);
        }
    }

    public void setupPorts(ArrayList<Port> portDefs) {
        sizeX = 4;
        sizeY = 4;
        
        for (int i = 0; i < portDefs.size(); i++) {
            Port portInfo = portDefs.get(i);
            if (portInfo.getDirection() == PortDirection.OUTPUT) {
                maxLengthPortEast = Math.max(maxLengthPortEast, portInfo.getIdentifier().length());
            }
            if (portInfo.getDirection() == PortDirection.INPUT) {
                maxLengthPortWest = Math.max(maxLengthPortWest, portInfo.getIdentifier().length());
            }
            int position;
            ChipPort newPort;
            switch (portInfo.getDirection()) {
                case OUTPUT:
                    position = eastPosition++;
                    newPort = new ChipPort(position * 2, portInfo.getIdentifier());
                    newPort.parent = this;
                    newPort.isOutput = true;
                    ports.add(newPort);
                    break;
                case INPUT:
                    position = westPosition++;
                    newPort = new ChipPort(position * 2, portInfo.getIdentifier());
                    newPort.parent = this;
                    newPort.leftOrBottom = true;
                    ports.add(newPort);
                    break;
            }
        }
    }

    void setSize(int s) {
        csize = s;
        cspc = (8 * s);
        cspc2 = (cspc * 2);
        width = cspc2 * ((maxLengthPortEast + maxLengthPortWest) / 10 + 1);
        width = Math.max(width, cspc2 * (moduleName.length() / 10 + 1));
    }

    @Override
    public void setPoints() {
        super.setPoints();
        int x0 = x + cspc2;
        int y0 = y;
        int xr = x0 - cspc;
        int yr = y0 - cspc;
        double maxVertical = Math.max(southPosition, northPosition);
        maxVertical = ((int) maxVertical / 4) + (maxVertical % 4 == 0 ? 0 : maxVertical % 4 == 1 || maxVertical % 4 == 2 ? 0.5 : 1);

        int xs = (int) (sizeX * width * (maxVertical == 0 ? 1 : maxVertical));
        double maxHorizontal = Math.max(westPosition, eastPosition);
        maxHorizontal = ((int) maxHorizontal / 4) + (maxHorizontal % 4 == 0 ? 0 : maxHorizontal % 4 == 1 || maxHorizontal % 4 == 2 ? 0.5 : 1);
        int ys = (int) (sizeY * cspc2 * (maxHorizontal == 0 ? 1 : maxHorizontal));
        rectPointsX = new int[]{xr, xr + xs, xr + xs, xr};
        rectPointsY = new int[]{yr, yr, yr + ys, yr + ys};
        setBbox(xr, yr, rectPointsX[2], rectPointsY[2]);

        int px = x0, py = y0;
        for (ChipPort port : ports) {
            int dx = 0, dy = 0,
                    dax = 0, day = 0, sx = 0, sy = 0;
            if (port.isVertical) {
                dx = 1;
                dy = 0;
                dax = 0;
                if (port.leftOrBottom) {
                    day = 1;
                    sy = ys - cspc2;
                } else {
                    day = -1;                    
                }
            } else {
                dx = 0;
                dy = 1;
                day = 0;
                if (port.leftOrBottom) {
                    dax = -1;
                } else {
                    dax = 1;
                    sx = xs - cspc2;
                }
            }
            port.containerPanel = this.containerPanel;
            port.setPoint(px, py, dx, dy, dax, day, sx, sy, cspc);
        }
    }

    @Override
    public void draw(Graphics g) {
        Color old = g.getColor();

        g.setColor(!selectedText && !portSelected && needsHighlight()
                ? BaseElement.selectedColor : BaseElement.defaultColor);
        drawThickPolygon(g, rectPointsX, rectPointsY, 4);
        g.setColor(old);

        Font newFont = new Font("SansSerif", Font.BOLD, 11 * csize);
        g.setFont(newFont);
        FontMetrics fm = g.getFontMetrics();
        if (textX == 0 || textY == 0) {
            textX = (rectPointsX[0] + rectPointsX[1] - fm.stringWidth(moduleName)) / 2;
            textY = rectPointsY[2] + fm.getAscent();            
        }
        textWidth = fm.stringWidth(moduleName);
        textHeight = fm.getAscent();
        old = g.getColor();
        if (selectedText) {
            g.setColor(BaseElement.selectedColor);
        }
        g.drawString(moduleName, textX, textY);
        textBoundingBox = new Rectangle(textX, textY - textHeight,
                textWidth, textHeight);
        g.setColor(old);

        for (ChipPort port : ports) {
            port.draw(g);
        }
    }

    @Override
    public void move(int dx, int dy) {
        int newX = textX + dx;
        int newY = textY + dy;

        if (selectedText) {
            Rectangle newRect = new Rectangle(boundingBox.x, boundingBox.y - textHeight / 2,
                    boundingBox.width - textWidth, boundingBox.height + 2 * textHeight);
            if (!newRect.contains(newX, newY)) {
                if (newRect.contains(textX, newY)) {
                    newX = textX;
                } else if (newRect.contains(newX, textY)) {
                    newY = textY;
                }
            }
        }
        
        if (portSelected) {
            ports.get(portSelectedIndex).move(dx, dy);
        } else {
            textX = newX;
            textY = newY;
            textBoundingBox.x = textX;
            textBoundingBox.y = textY - textHeight;
        }

        if (!selectedText && !portSelected) {
            super.move(dx, dy);

        }
    }

    @Override
    public boolean contains(int x, int y) {
        if (textBoundingBox.contains(x, y)) {
            selectedText = true;
        } else {
            selectedText = false;
        }
        portSelected = false;
        for (int i = 0; i < ports.size(); i++) {
            ChipPort port = ports.get(i);
            if (port.contains(x, y)) {
                portSelected = true;
                portSelectedIndex = i;
                port.setSelected(true);
            } else {
                port.setSelected(false);
            }
        }
        return super.contains(x, y) || textBoundingBox.contains(x, y) || portSelected;
    }

    @Override
    public void doStep() {
    }

    @Override
    public Element getXmlElement(Document document) {
        Element parent = super.getXmlElement(document);
        parent.setAttribute("type", ChipRectangule.class.getName());
        parent.setAttribute("x", Integer.toString(boundingBox.x));
        parent.setAttribute("y", Integer.toString(boundingBox.y)); 
        parent.setAttribute("x2", Integer.toString(boundingBox.x + boundingBox.width));
        parent.setAttribute("y2", Integer.toString(boundingBox.y + boundingBox.height));
        
        Element extraParam0 = document.createElement("param");
        extraParam0.setTextContent(moduleName);
        
        parent.appendChild(extraParam0);
        
        for (int i = 0; i < ports.size(); i++) {
            ChipPort port = ports.get(i);
            parent.appendChild(port.getXmlElement(document));
        }
        parent.setAttribute("textX", Integer.toString(textX));
        parent.setAttribute("textY", Integer.toString(textY));
        parent.setAttribute("moduleName", moduleName);
        return parent;
    }
    
}
