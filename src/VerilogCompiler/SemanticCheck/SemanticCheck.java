/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SemanticCheck;

import DataStructures.ModuleRepository;
import VerilogCompiler.Interpretation.InstanceModuleScope;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class SemanticCheck {
    //<editor-fold defaultstate="collapsed" desc="Attributes">
    private Stack<Boolean> defaultCaseItemFound, proceduralBlocks;
    private boolean currentDefaultCaseItemFound = false, 
            currentInsideProceduralBlock = false;
    
    private HashMap<String, VariableInfo> declaredVariables;
    
    //</editor-fold>    

    //<editor-fold defaultstate="collapsed" desc="Utilitites">
    //<editor-fold defaultstate="collapsed" desc="Default Case">
    public boolean isDefaultCaseItemFound() {
        return currentDefaultCaseItemFound;
    }

    public void setDefaultCaseItemFound(boolean defaultCaseItemFound) {
        currentDefaultCaseItemFound = defaultCaseItemFound;
    }
    
    public void popDefaultcaseItemFound() {
        currentDefaultCaseItemFound = defaultCaseItemFound.peek();
        this.defaultCaseItemFound.pop();
    }
    
    public void saveDefaultCaseItemFound() {
        defaultCaseItemFound.push(currentDefaultCaseItemFound);
        currentDefaultCaseItemFound = false;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Variables">
    public void registerVariable(String variable, VariableInfo info) {
        declaredVariables.put(variable, info);
    }
    
    public boolean variableIsRegistered(String variable) {
        return declaredVariables.containsKey(variable);
    }
    
    public boolean variableIsArray(String variable) {
        if (!declaredVariables.containsKey(variable))
            return false;
        VariableInfo info = declaredVariables.get(variable);
        return info.isArray ;
    }
    
    public boolean variableIsVector(String variable) {
        if (!declaredVariables.containsKey(variable))
            return false;
        VariableInfo info = declaredVariables.get(variable);
        return info.isVector ;
    }
    
    public void setVariableIsArray(String variable, boolean isArray) {
        if (!declaredVariables.containsKey(variable))
            return;
        VariableInfo info = declaredVariables.get(variable);
        info.isArray = isArray;
    }
    
    public boolean variableIsArrayOrVector(String variable) {
        if (!declaredVariables.containsKey(variable))
            return false;
        VariableInfo info = declaredVariables.get(variable);
        return info.isArray || info.isVector;
    }
    
    public boolean variableIsNumeric(String variable) {
        if (!declaredVariables.containsKey(variable))
            return false;
        VariableInfo info = declaredVariables.get(variable);
        return info.isNumeric;
    }
    
    public boolean variableHasTypeVariable(String variable) {
        if (!declaredVariables.containsKey(variable))
            return false;
        VariableInfo info = declaredVariables.get(variable);
        return info.type == DataType.VARIABLE;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Procedural blocks">
    public boolean isInsideProceduralBlock() {
        return currentInsideProceduralBlock;
    }
    
    public void setInsideProceduralBlock(boolean insideProceduralBlock) {
        currentInsideProceduralBlock = insideProceduralBlock;
    }
    
    public void popInsideProceduralBlock() {
        currentInsideProceduralBlock = proceduralBlocks.peek();
        this.proceduralBlocks.pop();
    }
    
    public void saveInsideProceduralBlock() {
        proceduralBlocks.push(currentInsideProceduralBlock);
        currentInsideProceduralBlock = false;
    }
    //</editor-fold>
    
    public boolean isSomethingRegisteredWithThatName(String variableName) {
        return variableIsRegistered(variableName) ||
               ModuleRepository.getInstance().moduleIsRegistered(variableName);
    }
    
    public boolean moduleIsRegistered(String moduleName) {
        return ModuleRepository.getInstance().moduleIsRegistered(moduleName);
    }
    
    //</editor-fold>
    
    public InstanceModuleScope variablesToScope() {
        InstanceModuleScope scope = new InstanceModuleScope();
        for (Map.Entry<String, VariableInfo> entry : declaredVariables.entrySet()) {
            scope.registerVariable(entry.getKey(), entry.getValue().getCopy());
        }
        
        return scope;
    }
    
    public void resetAll() {
        defaultCaseItemFound.clear();
        proceduralBlocks.clear();
        declaredVariables.clear();
    }
        
    private SemanticCheck() {
        defaultCaseItemFound = new Stack<Boolean>();
        declaredVariables = new HashMap<String, VariableInfo>();
        proceduralBlocks = new Stack<Boolean>();
    }
    
    public static SemanticCheck getInstance() {
        return SemanticCheckHolder.INSTANCE;
    }
    
    private static class SemanticCheckHolder {

        private static final SemanticCheck INSTANCE = new SemanticCheck();
    }
}
