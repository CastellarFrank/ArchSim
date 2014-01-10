/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.Interpretation;

import VerilogCompiler.SemanticCheck.VariableInfo;
import java.util.HashMap;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class ModuleScope {
    //<editor-fold defaultstate="collapsed" desc="Attributes">
    private HashMap<String, VariableInfo> declaredVariables;
    //</editor-fold>

    public ModuleScope() {
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
}
