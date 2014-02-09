/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.Interpretation;

import Simulation.Configuration;
import VerilogCompiler.SemanticCheck.VariableInfo;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
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
    
    public void unregister(String moduleInstaceId) {
        if (Configuration.DEBUG_MODE)
            System.out.println("unregistering " + moduleInstaceId + " instance module scope");
        scopes.remove(moduleInstaceId);
    }
    
    public void executeScheduledNonBlockingAssigns() {
        for (Map.Entry<String, InstanceModuleScope> scope: scopes.entrySet()) {
            scope.getValue().executeNonBlockingAssigns();
        }
    }
    
    public String dumpToString(String moduleInstanceId) {
        return getScope(moduleInstanceId).dumpToString();
    }
    
    public InstanceModuleScope getScope(String moduleInstanceId) {
        return scopes.get(moduleInstanceId);
    }
    
    public ExpressionValue getVariableValue(String moduleInstanceId, String variable) {
        if (scopes.containsKey(moduleInstanceId))
            return scopes.get(moduleInstanceId).getVariableValue(variable);
        return null;
    }
    
    public VariableInfo getVariableInfo(String moduleInstanceId, String variable) {
        if (scopes.containsKey(moduleInstanceId))
            return scopes.get(moduleInstanceId).getVariableInfo(variable);
        return null;
    }
    
    public void init() {
        Set<String> keys = scopes.keySet();
        for (String key : keys) {
            scopes.get(key).init(this, key);
        }
    }
    
    public void runStep() {
        Set<String> keys = scopes.keySet();
        for (String key : keys) {
            scopes.get(key).runStep(this, key);
        }
    }
}
