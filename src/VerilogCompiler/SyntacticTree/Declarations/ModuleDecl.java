/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Declarations;

import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SyntacticTree.ModuleItems.GateDecl;
import VerilogCompiler.SyntacticTree.ModuleItems.ModuleItem;
import VerilogCompiler.SyntacticTree.Others.Port;
import VerilogCompiler.SyntacticTree.VNode;
import VerilogCompiler.Utils.StringUtils;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class ModuleDecl extends Declaration {
    String moduleName;
    ArrayList<Port> portList;
    ArrayList<ModuleItem> moduleItemList;
    
    //<editor-fold defaultstate="collapsed" desc="Decorations">
    int inputPortCount = 0;
    int outputPortCount = 0;
    int inoutPortCount = 0;
    
    ArrayList<GateDecl> gates;
    
    //</editor-fold>

    public ModuleDecl(String moduleName, ArrayList<Port> portList, ArrayList<ModuleItem> moduleItemList, int line, int column) {
        super(line, column);
        this.moduleName = moduleName;
        this.portList = portList;
        this.moduleItemList = moduleItemList;
    }


    //<editor-fold defaultstate="collapsed" desc="Getters & Setters">
    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public ArrayList<Port> getPortList() {
        return portList;
    }
    
    public ArrayList<GateDecl> getGates() {
        if (gates == null) {
            gates = new ArrayList<GateDecl>();
            for (ModuleItem moduleItem : moduleItemList) {
                if (moduleItem instanceof GateDecl)
                    gates.add((GateDecl) moduleItem);
            }
        }
        
        return gates;
    }

    public void setPortList(ArrayList<Port> portList) {
        this.portList = portList;
    }

    public ArrayList<ModuleItem> getModuleItemList() {
        return moduleItemList;
    }

    public void setModuleItemList(ArrayList<ModuleItem> moduleItemList) {
        this.moduleItemList = moduleItemList;
    }
    //</editor-fold>    

    @Override
    public String toString() {
        return String.format("module %s (%s);\n%s\nendmodule",
                moduleName, StringUtils.getInstance().ListToString(portList, ", "),
                StringUtils.getInstance().ListToString(moduleItemList, "\n"));
    }

    @Override
    public ExpressionType validateSemantics() {
        for (Port port : portList) {
            switch(port.getDirection()) {
                case INOUT:
                    inoutPortCount++; break;
                case INPUT:
                    inputPortCount++; break;
                case OUTPUT:
                    outputPortCount++; break;
            }
            port.validateSemantics();
        }
        
        for (ModuleItem moduleItem : moduleItemList) {
            moduleItem.validateSemantics();
        }
        
        return null;
    }
    
    public void executeModule(String moduleInstanceId) {
        for (ModuleItem moduleItem : moduleItemList) {
            moduleItem.executeModuleItem(/*moduleInstanceId*/);
        }
    }

    @Override
    public VNode getCopy() {
        ArrayList<Port> portCopies = new ArrayList<Port>();
        for (Port port : portList) {
            portCopies.add((Port)port.getCopy());
        }
        ArrayList<ModuleItem> itemsCopies = new ArrayList<ModuleItem>();
        for (ModuleItem moduleItem : moduleItemList) {
            itemsCopies.add((ModuleItem)moduleItem.getCopy());
        }
        ModuleDecl copy = new ModuleDecl(moduleName, portCopies, itemsCopies, line, column);
        copy.getGates();
        return copy;
    }
    
}
