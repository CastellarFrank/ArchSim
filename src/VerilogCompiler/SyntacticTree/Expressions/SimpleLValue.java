/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Expressions;

import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SemanticCheck.SemanticCheck;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
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
    public void setValue(SimulationScope simulationScope, String moduleName, Object value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
