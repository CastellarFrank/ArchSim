/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataStructures;

import Simulation.Elements.PortPosition;
import VerilogCompiler.SyntacticTree.PortDirection;

/**
 * This class contains information about a module port.
 * These attributes are:
 * <ul>
 *  <li>port position</li>
 *  <li>port name</li>
 *  <li>port index</li>
 *  <li>if port is an ouput port or it doesn't</li>
 * </ul>
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class PortInfo {
    
    /** Position of this port **/
    public PortPosition position;
    
    /** Name of this port **/
    public String portName;
    
    /** Index of this port**/
    public int index;
    
    /** Tells if this port is an output port or not **/
    public boolean isOutput = false;

    /**
     * Constructor
     * @param position position of the port
     * @param portName name of this port
     * @param index index of this port
     */
    public PortInfo(PortPosition position, String portName, int index) {
        this.position = position;
        this.portName = portName;
        this.index = index;
    }
    
    /**
     * Constructor
     * @param direction direction used to determine port position
     * @param portName name of this port
     * @param index index of this port
     * @see PortDirection
     */
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
