/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataStructures;

import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class ModuleInfo {
    private String moduleName;
    private ArrayList<PortInfo> portsInfo;

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
