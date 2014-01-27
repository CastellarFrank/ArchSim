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
    HashMap<String, InstanceModuleScope> scopes;
    public static String currentModuleName = null;
    //</editor-fold>

    public SimulationScope() {
        scopes = new HashMap<String, InstanceModuleScope>();
    }
    
    public void register(String moduleInstanceId, InstanceModuleScope scope) {
        scopes.put(moduleInstanceId, scope);
    }
    
    public InstanceModuleScope getScope(String moduleInstanceId) {
        return scopes.get(moduleInstanceId);
    }
    
    public ExpressionValue getVariableValue(String moduleInstanceId, String variable) {
        return scopes.get(moduleInstanceId).getVariableValue(variable);
    }
    
    public VariableInfo getVariableInfo(String moduleInstanceId, String variable) {
        return scopes.get(moduleInstanceId).getVariableInfo(variable);
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
