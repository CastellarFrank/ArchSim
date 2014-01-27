/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Expressions;

import VerilogCompiler.Interpretation.ExpressionValue;
import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SemanticCheck.SemanticCheck;
import VerilogCompiler.SyntacticTree.VNode;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class IdentifierExpression extends PrimaryExpression {
    String identifier;

    public IdentifierExpression(String identifier, int line, int column) {
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
        if (SemanticCheck.getInstance().variableIsRegistered(identifier)) {
            if (SemanticCheck.getInstance().variableIsArray(identifier))
                return type = ExpressionType.ARRAY;
            else {
                if (SemanticCheck.getInstance().variableIsVector(identifier))
                    return type = ExpressionType.VECTOR;
                else
                    return type = ExpressionType.INTEGER;
            }
        } else {
            ErrorHandler.getInstance().handleError(line, column, 
                    identifier + " is not declared");
        }
        return null;
    }

    @Override
    public ExpressionValue evaluate(SimulationScope simulationScope, String moduleName) {
        switch (type) {
            case ERROR: return null;
            case INTEGER: 
            case ARRAY:
                return simulationScope.getVariableValue(moduleName, identifier);
            default: return null;
        }
    }

    @Override
    public VNode getCopy() {
        return new IdentifierExpression(identifier, line, column);
    }
    
}
