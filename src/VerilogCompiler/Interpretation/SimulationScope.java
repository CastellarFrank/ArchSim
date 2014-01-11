/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.Interpretation;

import VerilogCompiler.SemanticCheck.VariableInfo;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class SimulationScope {
    //<editor-fold defaultstate="collapsed" desc="Attributes">
    HashMap<String, ModuleScope> scopes;
    public static String currentModuleName = null;
    //</editor-fold>

    public SimulationScope() {
        scopes = new HashMap<String, ModuleScope>();
    }
    
    public void register(String moduleName, ModuleScope scope) {
        scopes.put(moduleName, scope);
    }
    
    public ModuleScope getScope(String moduleName) {
        return scopes.get(moduleName);
    }
    
    public ExpressionValue getVariableValue(String moduleName, String variable) {
        return scopes.get(moduleName).getVariableValue(variable);
    }
    
    public ExpressionValue getVariableValueFromCurrentModule(String variable) {
        return scopes.get(SimulationScope.currentModuleName).getVariableValue(variable);
    }
    
    public VariableInfo getVariableInfo(String moduleName, String variable) {
        return scopes.get(moduleName).getVariableInfo(variable);
    }
    
    public void init() {
        Set<String> keys = scopes.keySet();
        for (String key : keys) {
            scopes.get(key).init();
        }
    }
    
    public void runStep() {
        Set<String> keys = scopes.keySet();
        for (String key : keys) {
            scopes.get(key).runStep(this, key);
        }
    }
}
