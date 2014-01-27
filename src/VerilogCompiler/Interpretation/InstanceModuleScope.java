/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.Interpretation;

import VerilogCompiler.SemanticCheck.VariableInfo;
import VerilogCompiler.SyntacticTree.ModuleItems.AlwaysBlock;
import VerilogCompiler.SyntacticTree.ModuleItems.InitialBlock;
import java.util.HashMap;
import java.util.Vector;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class InstanceModuleScope {
    //<editor-fold defaultstate="collapsed" desc="Attributes">
    private HashMap<String, VariableInfo> declaredVariables;
    Vector<InitialBlock> initialBlocks = new Vector<InitialBlock>();
    Vector<AlwaysBlock> alwaysBlocks = new Vector<AlwaysBlock>();
    //</editor-fold>
    
    public HashMap<String, VariableInfo> getVariables() {
        return declaredVariables;
    }

    public InstanceModuleScope() {
        declaredVariables = new HashMap<String, VariableInfo>();
    }
    
    public void registerVariable(String variable, VariableInfo info) {
        declaredVariables.put(variable, info);
    }
    
    public ExpressionValue getVariableValue(String variable) {
        return declaredVariables.get(variable).value;
    }
    
    public VariableInfo getVariableInfo(String variable) {
        return declaredVariables.get(variable);
    }
    
    public void init() {
        for (InitialBlock initialBlock : initialBlocks) {
            initialBlock.executeModuleItem();
        }
    }
    
    public void runStep(SimulationScope scope, String moduleName) {
        for (AlwaysBlock alwaysBlock : alwaysBlocks) {
            alwaysBlock.execute(scope, moduleName);
        }
    }
}
