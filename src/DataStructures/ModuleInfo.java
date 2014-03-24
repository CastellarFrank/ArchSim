/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataStructures;

import java.util.ArrayList;

/**
 * Class that contains metadata of a module.
 * Metadata includes:
 * <ul>
 *  <li>module name</li>
 *  <li>ports information</li>
 *  <li>is a leaf</li>
 *  <li>its Verilog source code</li>
 * </ul>
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class ModuleInfo {
    private String moduleName;
    private ArrayList<PortInfo> portsInfo;
    private boolean leaf = false;
    /**
     *It says if this module information instance is valid or not.
     */
    public boolean valid = true;
    private String source;

    /**
     * Returns this <code>ModuleInfo</code> source code
     * @return a Verilog source code.
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets this <code>ModuleInfo</code> source code
     * @param source Verilog source code.
     */
    public void setSource(String source) {
        this.source = source;
        hashCode();
    }

    /**
     * Tells if this <code>ModuleInfo</code> represents a leaf module.
     * @return <code>true</code> if the module represented by this metadata
     * does not contain other module instances, <code>false</code> otherwise.
     */
    public boolean isLeaf() {
        return leaf;
    }

    /**
     * Sets this object's leaf attribute
     * @param isLeaf value to be set.
     */
    public void setIsLeaf(boolean isLeaf) {
        this.leaf = isLeaf;
    }

    /**
     * Returns this <code>ModuleInfo</code> ports info.
     * @return a list of <code>PortInfo</code>
     */
    public ArrayList<PortInfo> getPortsInfo() {
        return portsInfo;
    }

    /**
     * Sets this module info's list of <code>PortInfo</code>
     * @param portsInfo a list of <code>PortInfo</code>
     */
    public void setPortsInfo(ArrayList<PortInfo> portsInfo) {
        this.portsInfo = portsInfo;
    }
    
    /**
     * Retrieve a <code>PortInfo</code> by its index.
     * @param index position on the list
     * @return the <code>PortInfo</code> at <code>index</code> position if
     * <code>index >= 0</code> and <code>index &lst= portsInfo.size - 1</code>
     */
    public PortInfo getPortInfo(int index) {
        return portsInfo.get(index);
    }

    /**
     * Returns the name of the module definition that this metadata represents
     * @return the name of the module represented by this metadata.
     */
    public String getModuleName() {
        return moduleName;
    }

    /**
     * Sets this module info's name.
     * @param moduleName name of the module definition that this metadata represents
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * Constructor
     * @param portsInfo list of <code>PortInfo</code>
     */
    public ModuleInfo(ArrayList<PortInfo> portsInfo) {
        this.portsInfo = portsInfo;
    }

    /**
     * Constructor
     * @param moduleName name of the module definition that this metadata represents
     * @param portsInfo list of <code>PortInfo</code>
     */
    public ModuleInfo(String moduleName, ArrayList<PortInfo> portsInfo) {
        this.moduleName = moduleName;
        this.portsInfo = portsInfo;
    }    

    /**
     * Constructor
     */
    public ModuleInfo() {
    }    
}
