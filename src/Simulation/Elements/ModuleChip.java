/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation.Elements;

import DataStructures.ModuleInfo;
import DataStructures.ModuleRepository;
import Exceptions.ModuleDesignNotFoundException;
import GUI.ContainerPanel;
import Simulation.Configuration;
import VerilogCompiler.Interpretation.InstanceModuleScope;
import VerilogCompiler.Interpretation.ModuleInstanceIdGenerator;
import VerilogCompiler.SyntacticTree.Declarations.ModuleDecl;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.math.BigInteger;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class ModuleChip extends BaseElement {

    //<editor-fold defaultstate="collapsed" desc="Instance Attributes">
    ModuleInfo moduleInfo;
    PortElement ports[];
    int csize, cspc, cspc2;
    int rectPointsX[], rectPointsY[];
    int sizeX, sizeY, textX, textY;
    int southPosition = 0, northPosition = 0, westPosition = 0, eastPosition = 0;
    int maxLengthPortEast = 0, maxLengthPortWest = 0, width = 0;
    String moduleName, moduleInstanceId;
    ModuleDecl moduleInstance;
    ArrayList<Integer> voltageSources = new ArrayList<Integer>();
    
    public boolean isInitialized = false;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public ModuleChip(int x, int y, String[] extraParams) {
        super(x, y);

        sizeX = 4;
        sizeY = 4;
    }

    /**
     * Constructor.
     * @param x
     * @param y
     * @param x2
     * @param y2
     * @param extraParams
     * @throws ModuleDesignNotFoundException if the name specified in extraParams 
     * doesn't exist or extraParams is <code>null</code>
     */
    public ModuleChip(int x, int y, int x2, int y2, String[] extraParams) {
        super(x, y, x2, y2, extraParams);

        sizeX = 4;
        sizeY = 4;
        newInit(x, y, extraParams[0]);
    }

    public ModuleChip(ModuleInfo info, int x, int y, int x2, int y2, int flags, String[] extraParams) {
        super(x, y, x2, y2, flags);

        sizeX = 4;
        sizeY = 4;
    }

    @Override
    public void setContainerPanel(ContainerPanel containerPanel) {
        super.setContainerPanel(containerPanel);
        moduleInstance = ModuleRepository.getInstance().getModuleLogic(moduleName);
        moduleInstanceId = ModuleInstanceIdGenerator.generate();
        InstanceModuleScope scope = moduleInstance.getScope();
        this.containerPanel.simulationScope.register(moduleInstanceId, scope);
    }   

    private void newInit(int realX, int realY, String moduleName) {
        Element element = ModuleRepository.getInstance().getDesignPrototype(moduleName);
        if (element == null) {
            throw new ModuleDesignNotFoundException(moduleName + "'s design was not found");
        }
        this.moduleName = element.getAttribute("moduleName");
        textX = Integer.parseInt(element.getAttribute("textX"));
        textY = Integer.parseInt(element.getAttribute("textY"));

        x = Integer.parseInt(element.getAttribute("x"));
        y = Integer.parseInt(element.getAttribute("y"));
        x2 = Integer.parseInt(element.getAttribute("x2"));
        y2 = Integer.parseInt(element.getAttribute("y2"));
        
        int ddx = realX - x;
        int ddy = realY - y;
        x = realX;
        y = realY;
        
        x2 = x2 + ddx;
        y2 = y2 + ddy;
        
        textX += ddx;
        textY += ddy;
        
        setBoundingBox(x, y, x2, y2);

        setupPorts(element.getElementsByTagName("port"));

        setSize(1);
        allocNodes();
        setPoints();
    }

    private void setupPorts(NodeList ports) {
        this.ports = new PortElement[ports.getLength()];

        for (int i = 0; i < ports.getLength(); i++) {
            Element port = (Element) ports.item(i);
            Boolean isVertical = Boolean.parseBoolean(port.getAttribute("isVertical"));
            Boolean leftOrBottom = Boolean.parseBoolean(port.getAttribute("leftOrBottom"));
            Integer position = Integer.parseInt(port.getAttribute("position"));
            String portName = port.getAttribute("portName");
            Boolean isOutput = Boolean.parseBoolean(port.getAttribute("isOutput"));

            PortPosition side;

            if (isVertical) {
                if (leftOrBottom) {
                    southPosition++;
                    side = PortPosition.SOUTH;
                } else {
                    northPosition++;
                    side = PortPosition.NORTH;
                }
            } else {
                if (leftOrBottom) {
                    maxLengthPortWest = max(maxLengthPortWest, portName.length());
                    westPosition++;
                    side = PortPosition.WEST;
                } else {
                    maxLengthPortEast = max(maxLengthPortEast, portName.length());
                    eastPosition++;
                    side = PortPosition.EAST;
                }
            }
            if (isOutput) {
                voltageSources.add(i);
            }
            
            PortElement elem = new PortElement(position, side, portName);
            elem.isVertical = isVertical;
            elem.leftOrBottom = leftOrBottom;
            elem.isOutput = isOutput;
            this.ports[i] = elem;
        }
    }

    //</editor-fold>    


    public String getModuleInstanceId() {
        return moduleInstanceId;
    }
    
    @Override
    public void stampVoltages() {
        for (int i = 0; i < getPostCount(); i++) {
            if (ports[i].isOutput && voltageSources.contains(i)) {
                containerPanel.stampVoltageSource(0, joints[i], ports[i].voltageSourceIndex,
                        0, ports[i].previousValue);
            }
        }
    }
    
    @Override
    public int getVoltageSourceCount() {
        return voltageSources.size();
    }

    @Override
    public void setVoltageSourceReference(int referenceIndex, int reference) {
        for (int i = 0; i < getPostCount(); i++) {
            PortElement port = ports[i];
            if (voltageSources.contains(i) && port.isOutput && referenceIndex-- == 0) {
                port.voltageSourceIndex = reference;
                return;
            }
        }
    }
    
    @Override
    public boolean hasGroundConnection(int index) {
        return voltageSources.contains(index) && ports[index].isOutput;
    }

    @Override
    public boolean thereIsConnectionBetween(int elementA, int elementB) {
        return false;
    }    

    public String getModuleName() {
        return moduleName;
    }

    void setSize(int s) {
        csize = s;
        cspc = (8 * s);
        cspc2 = (cspc * 2);
        width = cspc2 * ((maxLengthPortEast + maxLengthPortWest) / 10 + 1);
        width = max(width, cspc2 * (moduleName.length() / 10 + 1));
    }

    private void setBoundingBox(int x, int y, int x2, int y2) {
        boundingBox.setBounds(x, y, Math.abs(x2 - x), Math.abs(y2 - y));
    }

    @Override
    public void setPoints() {
        super.setPoints();
        int x0 = x + cspc2;
        int y0 = y;
        int xr = x0 - cspc;
        int yr = y0 - cspc;
        int xs = x2 - x;
        int ys = y2 - y;
        rectPointsX = new int[]{xr, x2 + cspc, x2 + cspc, xr};
        rectPointsY = new int[]{yr, yr, y2 - cspc, y2 - cspc};
        setBoundingBox(xr, yr, rectPointsX[2], rectPointsY[2]);
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
    public void move(int dx, int dy) {
        textX += dx;
        textY += dy;
        super.move(dx, dy);
    }

    @Override
    public void draw(Graphics g) {
        if (Configuration.DEBUG_MODE) {
            Color old = g.getColor();
            g.setColor(Color.BLUE);
            g.drawRect(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
            g.setColor(old);
        }
        otherDraw(g);
    }

    public void otherDraw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Font f = new Font("SansSerif", 0, 10 * csize);
        g2d.setFont(f);

        for (int i = 0; i != getPostCount(); i++) {
            PortElement p = ports[i];
            p.voltage = voltages[i];
            p.draw(g2d, joints[i]);
        }

        Color old = g2d.getBackground();
        g2d.setColor(needsHighlight() ? BaseElement.selectedColor : BaseElement.defaultColor);
        drawThickPolygon(g2d, rectPointsX, rectPointsY, 4);        

        Font newFont = new Font("SansSerif", Font.BOLD, 11 * csize);
        g2d.setFont(newFont);
        g2d.drawString(moduleName, textX, textY);
        g2d.setColor(old);
    }

    @Override
    public void doStep() {
        if (!isInitialized) {
            moduleInstance.initModule(containerPanel.simulationScope, moduleInstanceId);
            isInitialized = true;
        }
        int postCount = getPostCount();
        for (int i = 0; i < postCount; i++) {
            if (ports[i].isOutput) {
                continue;
            }
            String portName = ports[i].portName;
            String multibitsValue = binaryValues[i];
            if (multibitsValue == null || multibitsValue.contains("z")) {
                containerPanel.simulationScope.getVariableValue(moduleInstanceId, portName).zValue = true;
            } else if (multibitsValue.contains("x")) {
                containerPanel.simulationScope.getVariableValue(moduleInstanceId, portName).xValue = true;
            }else {
                BigInteger converted = new BigInteger(multibitsValue);
                containerPanel.simulationScope
                        .getVariableValue(moduleInstanceId, portName)
                        .setValue(converted);
            } 
        }
        moduleInstance.executeModule(containerPanel.simulationScope, moduleInstanceId);
        containerPanel.simulationScope.executeScheduledNonBlockingAssigns();
        if (Configuration.DEBUG_MODE) {
            System.out.println(containerPanel.simulationScope.dumpToString(moduleInstanceId));
        }
        for (int i = 0; i < postCount; i++) {
            if (ports[i].isOutput) {
                /*String value = containerPanel.simulationScope
                        .getVariableValue(moduleInstanceId, ports[i].portName)
                        .getValueAsString();*/
                String value = containerPanel.simulationScope
                        .getFormattedValue(moduleInstanceId, ports[i].portName);
                binaryValues[i] = value == null ? "z" : value;
                ports[i].previousValue = binaryValues[i];
            }
        }
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
        extraParam0.setTextContent(moduleName);

        element.appendChild(extraParam0);

        return element;
    }

    class PortElement {
        //<editor-fold defaultstate="collapsed" desc="Instance Attributes">

        Point post, stub;
        Point textPosition;
        int position, voltageSourceIndex, bubbleX, bubbleY;
        PortPosition side;
        String portName;
        boolean lineOver, bubble, clock, value, state;
        double curcount, current;
        boolean isVertical, leftOrBottom, isOutput;
        double voltage;
        String multibits;
        String previousValue;
        //</editor-fold>

        PortElement(int p, PortPosition side, String text) {
            position = p;
            this.side = side;
            this.portName = text;
        }

        PortElement(int position, String text) {
            this.portName = text;
            this.position = position;
        }

        void setPoint(int px, int py, int dx, int dy, int dax, int day,
                int sx, int sy) {
            int cspc2 = cspc * 2;
            int xa = px + (!isVertical ? cspc2 : cspc) * dx * position + sx;
            int ya = py + (!isVertical ? cspc : cspc2) * dy * position + sy;
            post = new Point(xa + dax * cspc * 4, ya + day * cspc * 4);
            stub = new Point(xa + dax * cspc, ya + day * cspc);
            textPosition = new Point(xa, ya);

            /*if (!isVertical)
             setBbox(post.x, post.y - 3, stub.x, post.y + 3);
             else
             setBbox(post.x - 3, post.y, post.x + 3, stub.y);*/
        }

        public void draw(Graphics g, int jointIndex) {
            Color old = g.getColor();
            setVoltageColor(g, voltage);
            drawThickLine(g, post, stub);
            g.setColor(old);
            
            Font f = new Font("SansSerif", 0, 10);
            g.setFont(f);

            int dispX = 0;
            if (isVertical) {
                AffineTransform fontAT = new AffineTransform();
                Font theFont = g.getFont();
                if (leftOrBottom) {
                    fontAT.rotate(3 * Math.PI / 2);
                } else {
                    fontAT.rotate(Math.PI / 2);
                }
                Font theDerivedFont = theFont.deriveFont(fontAT);
                g.setFont(theDerivedFont);
            }

            FontMetrics fm = g.getFontMetrics();

            if (!leftOrBottom) {
                if (isVertical) {
                    dispX = fm.getAscent() / 2;
                } else {
                    dispX = fm.stringWidth(portName);
                }
            } else {
                if (isVertical) {
                    dispX = -g.getFontMetrics(f).getAscent() / 2;
                } else {
                }
            }
            g.setColor(BaseElement.textColor);
            g.drawString(portName, textPosition.x - dispX,
                    textPosition.y + fm.getAscent() / 2);
            g.setColor(old);
            drawPost(g, post.x, post.y, jointIndex);
        }
    }
}
