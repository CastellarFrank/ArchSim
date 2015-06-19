/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation.Elements.Inputs;

import Exceptions.ArchException;
import Simulation.Elements.BaseElement;
import java.awt.Graphics;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class ClockInput extends BaseElement {
    int textSpace = 10;
    public ClockInput(int x, int y) throws ArchException {
        super(x, y);
        binaryValues[0] = "0";
    }

    public ClockInput(int x, int y, int x2, int y2, String[] extraParams) throws ArchException {
        super(x, y, x2, y2, extraParams);
        binaryValues[0] = extraParams[0];
    }

    public ClockInput(int x, int y, int x2, int y2, int flags) throws ArchException {
        super(x, y, x2, y2, flags);
        binaryValues[0] = "0";
    }
    
    @Override
    public void setPoints() {
        super.setPoints();        
        lead1 = point2;
        lead1.x -= (textSpace * sign(dx));
    }
    
    @Override
    public int getPostCount() { return 1; }

    @Override
    public void draw(Graphics g) {
    }
    
    @Override
    public int getVoltageSourceCount() {
        return 1;
    }

    @Override
    public void doStep() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isPostOutput(int index) {
      return index == 0;
    }
}
