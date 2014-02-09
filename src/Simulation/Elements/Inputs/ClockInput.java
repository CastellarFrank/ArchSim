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

    public ClockInput(int x, int y) throws ArchException {
        super(x, y);
    }

    public ClockInput(int x, int y, int x2, int y2, String[] extraParams) throws ArchException {
        super(x, y, x2, y2, extraParams);
    }

    public ClockInput(int x, int y, int x2, int y2, int flags) throws ArchException {
        super(x, y, x2, y2, flags);
    }

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
    
}
