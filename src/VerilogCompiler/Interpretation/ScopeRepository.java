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
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
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
        if (!scopesByModule.containsKey(moduleName))
            return null;
        
        return scopesByModule.get(moduleName).getCopy();
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
