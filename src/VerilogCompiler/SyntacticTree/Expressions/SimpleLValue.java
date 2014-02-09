/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Expressions;

import VerilogCompiler.Interpretation.ExpressionValue;
import VerilogCompiler.Interpretation.InstanceModuleScope;
import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SemanticCheck.SemanticCheck;
import VerilogCompiler.SyntacticTree.VNode;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class SimpleLValue extends LValue {

    String identifier;

    public SimpleLValue(String identifier, int line, int column) {
        super(line, column);
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return identifier;
    }

    @Override
    public ExpressionType validateSemantics() {
        if (!SemanticCheck.getInstance().variableIsRegistered(identifier)) {
            ErrorHandler.getInstance().handleError(line, column,
                    identifier + " is not declared");
            return ExpressionType.ERROR;
        } else {
            if (SemanticCheck.getInstance().variableHasTypeVariable(identifier) &&
                    !SemanticCheck.getInstance().isInsideProceduralBlock()) {
                ErrorHandler.getInstance().handleError(line, column, identifier + 
                        " cannot be assign outside a procedural block");
            }
            if (SemanticCheck.getInstance().variableIsArray(identifier)) {
                return ExpressionType.ARRAY;
            }
            if (SemanticCheck.getInstance().variableIsVector(identifier)) {
                return ExpressionType.VECTOR;
            } 
        }

        return ExpressionType.INTEGER;
    }

    @Override
    public void setValue(SimulationScope simulationScope, String instanceModuleId, Object value) {
        ExpressionValue address = simulationScope.getVariableValue(instanceModuleId, identifier);
        address.setValue(value);
    }

    @Override
    public VNode getCopy() {
        return new SimpleLValue(identifier, line, column);
    }

    @Override
    public void setValue(InstanceModuleScope scope, ExpressionValue value) {
        scope.setVariableValue(identifier, value);
    }
}
