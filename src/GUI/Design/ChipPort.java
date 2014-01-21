/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Design;

import Exceptions.ArchException;
import Exceptions.NoExtraParamsException;
import Simulation.Configuration;
import Simulation.Elements.BaseElement;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class ChipPort extends BaseElement {
    public ChipRectangule parent;

    public boolean isVertical = false, leftOrBottom = false, isOutput = false;
    Point post, stub, textPosition;
    int position;
    String portName;
    int spacing = -1;

    public ChipPort(int x, int y) {
        super(x, y);
    }
    
    public ChipPort(int position, String portName) {
        super(0, 0);
        this.position = position;
        this.portName = portName;
    }

    public ChipPort(int x, int y, int x2, int y2, String[] extraParams) throws ArchException {
        super(x, y, x2, y2, extraParams);

        if (extraParams == null || extraParams.length == 0) {
            throw new NoExtraParamsException("port name (extraParams[0] expected");
        }

        portName = extraParams[0];
    }

    @Override
    public void draw(Graphics g) {        
        if (Configuration.DEBUG_MODE) {
            Color old = g.getColor();
            g.setColor(Color.BLUE);
            g.drawRect(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
            g.setColor(old);
        }
        
        g.setColor(needsHighlight() ? BaseElement.selectedColor : BaseElement.defaultColor);
        drawThickLine(g, post, stub);
        
        Font f = new Font("SansSerif", 0, 10);
        g.setFont(f);
        
        int dispX = 0;
        if (isVertical) {
            AffineTransform fontAT = new AffineTransform();
            Font theFont = g.getFont();
            if (leftOrBottom)
                fontAT.rotate(3 * Math.PI / 2);
            else
                fontAT.rotate(Math.PI / 2);
            Font theDerivedFont = theFont.deriveFont(fontAT);
            g.setFont(theDerivedFont);
        }
        
        FontMetrics fm = g.getFontMetrics();        
        
        if (!leftOrBottom) {
            if (isVertical) {
                dispX = fm.getAscent()/2;
            } else {
                dispX = fm.stringWidth(portName);
            }
        } else {
            if (isVertical) {
                dispX = -g.getFontMetrics(f).getAscent()/2;
            } else {
                
            }
        }
        
        g.drawString(portName, textPosition.x - dispX,
                    textPosition.y + fm.getAscent() / 2);
        drawPost(g, post.x, post.y, 0);
    }

    @Override
    public void doStep() {
    }

    void setPoint(int px, int py, int dx, int dy, int dax, int day,
            int sx, int sy, int cspc) {
        int cspc2 = cspc * 2;
        int xa = px + (!isVertical ? cspc2 : cspc) * dx * position + sx;
        int ya = py + (!isVertical ? cspc : cspc2) * dy * position + sy;
        post = new Point(xa + dax * cspc * 4, ya + day * cspc * 4);
        stub = new Point(xa + dax * cspc, ya + day * cspc);
        textPosition = new Point(xa, ya);
        
        if (!isVertical)
            setBbox(post.x, post.y - 3, stub.x, post.y + 3);
        else
            setBbox(post.x - 3, post.y, post.x + 3, stub.y);
    }

    @Override
    public void move(int dx, int dy) {
        int maxPosX = parent.boundingBox.width / parent.cspc;
        int maxPosY = parent.boundingBox.height / parent.cspc;
        if (isVertical) {
            if (Math.abs(dy) > Math.abs(dx)) {                
                if (post.x > parent.boundingBox.x + parent.boundingBox.width - 5) {
                    if (!leftOrBottom)
                        position = 1;
                    else
                        position = maxPosY - 1;
                    leftOrBottom = false;                    
                } else if (post.x < parent.boundingBox.x + 5) {   
                    if (!leftOrBottom)
                        position = 1;
                    else
                        position = maxPosY - 1;
                    leftOrBottom = true;
                } else return;
                isVertical = false;
                parent.setPoints();
            } else {
                //<editor-fold defaultstate="collapsed" desc="Horizontal move">
                if (post.x + dx < parent.boundingBox.x || 
                        post.x + dx > parent.boundingBox.x + parent.boundingBox.width)
                    return;
                if (dx < 0)
                    position -= 1;
                if (dx > 0)
                    position += 1;
                post.x += dx;
                stub.x += dx;
                textPosition.x += dx;
                //</editor-fold>                
            }
        } else {
            if (Math.abs(dx) > Math.abs(dy)) {                
                if (post.y > parent.boundingBox.y + parent.boundingBox.height - 5) {
                    if (!leftOrBottom)
                        position = maxPosX - 1;
                    else
                        position = 1;
                    leftOrBottom = true;                       
                } else if (post.y < parent.boundingBox.y + 5) {
                    if (!leftOrBottom)
                        position = maxPosX - 1;
                    else
                        position = 1;
                    leftOrBottom = false;
                } else return;
                isVertical = true;
                parent.setPoints();
            } else {
                //<editor-fold defaultstate="collapsed" desc="Vertical move">
                if (post.y + dy < parent.boundingBox.y || 
                        post.y + dy > parent.boundingBox.y + parent.boundingBox.height)
                    return;
                if (dy < 0)
                    position -= 1;
                if (dy > 0)
                    position += 1;
                post.y += dy;
                stub.y += dy;
                textPosition.y += dy;
                //</editor-fold>                
            }
        }
        
        if (!isVertical)
            setBbox(post.x, post.y - 3, stub.x, post.y + 3);
        else
            setBbox(post.x - 3, post.y, post.x + 3, stub.y);
    }

    @Override
    public Element getXmlElement(Document document) {
        Element element = document.createElement("port");
        element.setAttribute("id", Integer.toString(ID));
        element.setAttribute("position", Integer.toString(position));
        element.setAttribute("isVertical", Boolean.toString(isVertical));
        element.setAttribute("leftOrBottom", Boolean.toString(leftOrBottom));
        element.setAttribute("portName", portName);
        element.setAttribute("isOutput", Boolean.toString(isOutput));
        
        return element;
    }
    
}
