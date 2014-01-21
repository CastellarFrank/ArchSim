/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataStructures;

import Simulation.Elements.PortPosition;
import VerilogCompiler.SyntacticTree.PortDirection;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class PortInfo {
    public PortPosition position;
    public String portName;
    public int index;
    public boolean isOutput = false;

    public PortInfo(PortPosition position, String portName, int index) {
        this.position = position;
        this.portName = portName;
        this.index = index;
    }
    
    public PortInfo(PortDirection direction, String portName, int index) {
        switch (direction) {
            case INPUT:
                position = PortPosition.WEST;
                break;
            case OUTPUT:
                position = PortPosition.EAST;
                break;
        }
        this.portName = portName;
        this.index = index;
    }
}
