/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataStructures;

import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class ModuleInfo {
    private String moduleName;
    private ArrayList<PortInfo> portsInfo;
    private boolean leaf = false;
    private String source;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setIsLeaf(boolean isLeaf) {
        this.leaf = isLeaf;
    }

    public ArrayList<PortInfo> getPortsInfo() {
        return portsInfo;
    }

    public void setPortsInfo(ArrayList<PortInfo> portsInfo) {
        this.portsInfo = portsInfo;
    }
    
    public PortInfo getPortInfo(int index) {
        return portsInfo.get(index);
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public ModuleInfo(ArrayList<PortInfo> portsInfo) {
        this.portsInfo = portsInfo;
    }

    public ModuleInfo(String moduleName, ArrayList<PortInfo> portsInfo) {
        this.moduleName = moduleName;
        this.portsInfo = portsInfo;
    }    

    public ModuleInfo() {
    }
    
    
}
