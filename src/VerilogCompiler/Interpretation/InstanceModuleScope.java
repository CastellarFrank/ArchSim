/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.Interpretation;

import Simulation.Configuration;
import VerilogCompiler.SemanticCheck.VariableInfo;
import VerilogCompiler.SyntacticTree.Expressions.LValue;
import VerilogCompiler.SyntacticTree.ModuleItems.AlwaysBlock;
import VerilogCompiler.SyntacticTree.ModuleItems.InitialBlock;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class InstanceModuleScope {
    //<editor-fold defaultstate="collapsed" desc="Attributes">
    private HashMap<String, VariableInfo> declaredVariables;
    Vector<InitialBlock> initialBlocks = new Vector<InitialBlock>();
    Vector<AlwaysBlock> alwaysBlocks = new Vector<AlwaysBlock>();
    HashMap<LValue, ExpressionValue> nonBlockingValuesToAssign;
    //</editor-fold>
    
    public HashMap<String, VariableInfo> getVariables() {
        return declaredVariables;
    }
    
    public Set<String> getVariableNames() {
        return declaredVariables.keySet();
    }
    
    public InstanceModuleScope() {
        declaredVariables = new HashMap<String, VariableInfo>();
        nonBlockingValuesToAssign = new HashMap<LValue, ExpressionValue>();
    }
    
    public void scheduleVariableAssign(LValue variable, ExpressionValue newValue) {
        nonBlockingValuesToAssign.put(variable, newValue);
    }
    
    public void executeNonBlockingAssigns() {
        if (Configuration.DEBUG_MODE)
            System.out.println("Executing non blocking assignments");
        
        for (Map.Entry<LValue, ExpressionValue> entry: nonBlockingValuesToAssign.entrySet()) {
            entry.getKey().setValue(this, entry.getValue());
        }
    }
    
    public String dumpToString() {
        String result = "";
        for (Map.Entry<String, VariableInfo> variable : declaredVariables.entrySet()) {
            ExpressionValue value = variable.getValue().value;
            String stringValue = value + "";
            if (value != null && value.value != null && value.value instanceof Object[])
                stringValue = Convert.arrayToString((Object[])value.value);
            result += variable.getKey() + " = " + stringValue + "\n";
        }
        return result;
    }
    
    public void registerVariable(String variable, VariableInfo info) {
        declaredVariables.put(variable, info);
    }
    
    public ExpressionValue getVariableValue(String variable) {
        return declaredVariables.get(variable).value;
    }
    
    public void setVariableValue(String variable, ExpressionValue value) {
        declaredVariables.get(variable).value = value;
    }
    
    public VariableInfo getVariableInfo(String variable) {
        return declaredVariables.get(variable);
    }
    
    public InstanceModuleScope getCopy() {
        InstanceModuleScope copy = new InstanceModuleScope();
        for (InitialBlock initialBlock : initialBlocks) {
            copy.initialBlocks.add((InitialBlock)initialBlock.getCopy());
        }
        for (AlwaysBlock alwaysBlock : alwaysBlocks) {
            copy.alwaysBlocks.add(alwaysBlock);
        }
        for (Map.Entry<String, VariableInfo> entry : declaredVariables.entrySet()) {
            copy.registerVariable(entry.getKey(), entry.getValue().getCopy());
        }
        return copy;
    }
    
    public void init(SimulationScope scope, String instanceModuleId) {
        for (InitialBlock initialBlock : initialBlocks) {
            initialBlock.executeModuleItem(scope, instanceModuleId);
        }
    }
    
    public void runStep(SimulationScope scope, String instanceModuleId) {
        for (AlwaysBlock alwaysBlock : alwaysBlocks) {
            alwaysBlock.execute(scope, instanceModuleId);
        }
    }
}
