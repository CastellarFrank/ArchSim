/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.Interpretation;

import VerilogCompiler.SemanticCheck.VariableInfo;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class ScopeRepository {
    
    HashMap<String, InstanceModuleScope> scopesByModule;
    
    public void register(String moduleName, InstanceModuleScope scope) {
        if (scopesByModule.containsKey(moduleName)) return;
        scopesByModule.put(moduleName, scope);
    }
    
    private InstanceModuleScope getInstanceScope(String moduleName) {
        if (scopesByModule.containsKey(moduleName))
            return scopesByModule.get(moduleName);
        return null;
    }
    
    public InstanceModuleScope getCopyOfScope(String moduleName) {
        InstanceModuleScope newScope = new InstanceModuleScope();
        InstanceModuleScope reference = getInstanceScope(moduleName);
        HashMap<String, VariableInfo> variables = reference.getVariables();
        
        for (Map.Entry<String, VariableInfo> entry : variables.entrySet()) {
            newScope.registerVariable(entry.getKey(), entry.getValue().getCopy());
        }
        
        return newScope;
    }
    
    private ScopeRepository() {
        scopesByModule = new HashMap<String, InstanceModuleScope>();
    }
    
    public static ScopeRepository getInstance() {
        return ScopeRepositoryHolder.INSTANCE;
    }
    
    private static class ScopeRepositoryHolder {

        private static final ScopeRepository INSTANCE = new ScopeRepository();
    }
}
