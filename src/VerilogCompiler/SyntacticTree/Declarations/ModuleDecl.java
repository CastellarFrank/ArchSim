/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Declarations;

import VerilogCompiler.Interpretation.InstanceModuleScope;
import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SemanticCheck.SemanticCheck;
import VerilogCompiler.SyntacticTree.ModuleItems.GateDecl;
import VerilogCompiler.SyntacticTree.ModuleItems.InitialBlock;
import VerilogCompiler.SyntacticTree.ModuleItems.ModuleInstantiation;
import VerilogCompiler.SyntacticTree.ModuleItems.ModuleItem;
import VerilogCompiler.SyntacticTree.Others.Port;
import VerilogCompiler.SyntacticTree.VNode;
import VerilogCompiler.Utils.StringUtils;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class ModuleDecl extends Declaration {
    String moduleName;
    ArrayList<Port> portList;
    ArrayList<ModuleItem> moduleItemList;
    String parentModuleInstanceId;
    
    //<editor-fold defaultstate="collapsed" desc="Decorations">
    int inputPortCount = 0;
    int outputPortCount = 0;
    int inoutPortCount = 0;
    
    boolean hasModuleInstances = false;
    
    ArrayList<GateDecl> gates;
    
    InstanceModuleScope scope;
    
    //</editor-fold>

    public ModuleDecl(String moduleName, ArrayList<Port> portList, ArrayList<ModuleItem> moduleItemList, int line, int column) {
        super(line, column);
        this.moduleName = moduleName;
        this.portList = portList;
        this.moduleItemList = moduleItemList;
    }

    public boolean hasModuleInstances() {
        return hasModuleInstances;
    }

    public void setHasModuleInstances(boolean hasModuleInstances) {
        this.hasModuleInstances = hasModuleInstances;
    }

    public String getParentModuleInstanceId() {
        return parentModuleInstanceId;
    }

    public void setParentModuleInstanceId(String parentModuleInstanceId) {
        this.parentModuleInstanceId = parentModuleInstanceId;
    }

    public InstanceModuleScope getScope() {
        return scope;
    }

    public void setScope(InstanceModuleScope scope) {
        this.scope = scope;
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
    
    public ArrayList<ModuleInstantiation> getModuleInstances() {
        ArrayList<ModuleInstantiation> inst = new ArrayList<ModuleInstantiation>();
        
        for (ModuleItem item : getModuleItemList()) {
            if (item instanceof ModuleInstantiation)
                inst.add((ModuleInstantiation) item);
            
        }
        return inst;
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
            if (moduleItem instanceof ModuleInstantiation)
                hasModuleInstances = true;
            moduleItem.validateSemantics();
        }
        
        scope = SemanticCheck.getInstance().variablesToScope();
        
        return null;
    }
    
    public void initModule(SimulationScope simulationScope, String moduleInstanceId) {
        for (ModuleItem moduleItem : moduleItemList) {
            moduleItem.initModuleItem(simulationScope, moduleInstanceId);
        }
    }
    
    public void executeModule(SimulationScope simulationScope, String moduleInstanceId) {
        for (ModuleItem moduleItem : moduleItemList) {
            if (moduleItem instanceof InitialBlock)
                continue;
            moduleItem.executeModuleItem(simulationScope, moduleInstanceId);
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
        
        //<editor-fold defaultstate="collapsed" desc="Copy decorations">
        copy.hasModuleInstances = this.hasModuleInstances;
        copy.inoutPortCount = this.inoutPortCount;
        copy.outputPortCount = this.outputPortCount;
        copy.inputPortCount = this.inputPortCount;
        
        copy.scope = scope.getCopy();
        //</editor-fold>
        
        return copy;
    }
    
}
