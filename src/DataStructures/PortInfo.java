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

    public PortInfo(PortPosition position, String portName) {
        this.position = position;
        this.portName = portName;
    }
    
    public PortInfo(PortDirection direction, String portName) {
        switch (direction) {
            case INPUT:
                position = PortPosition.WEST;
                break;
            case OUTPUT:
                position = PortPosition.EAST;
                break;
        }
        this.portName = portName;
    }
}
