/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Expressions;

import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SemanticCheck.SemanticCheck;
import VerilogCompiler.SyntacticTree.VNode;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class OneIndexLValue extends LValue {
    String identifier;
    Expression expression;

    public OneIndexLValue(String identifier, Expression expression, int line, int column) {
        super(line, column);
        this.identifier = identifier;
        this.expression = expression;
    }


    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", identifier, expression);
    }

    @Override
    public ExpressionType validateSemantics() {
        if (!SemanticCheck.getInstance().variableIsRegistered(identifier)) {
            ErrorHandler.getInstance().handleError(line, column, 
                    identifier + " is not declared");
            return ExpressionType.ERROR;
        } else if (!SemanticCheck.getInstance().variableIsArrayOrVector(identifier)) {
            ErrorHandler.getInstance().handleError(line, column, 
                    identifier + " is not a vector/array");
            return ExpressionType.ERROR;
        }
        ExpressionType result = expression.validateSemantics();
        if (result != ExpressionType.INTEGER && 
                result != ExpressionType.VECTOR) {
            ErrorHandler.getInstance().handleError(line, column, 
                    "index must be an integer");
            return ExpressionType.ERROR;
        }
        if (SemanticCheck.getInstance().variableIsVector(identifier) && 
                !SemanticCheck.getInstance().variableIsArray(identifier))
            return ExpressionType.INTEGER;
        return ExpressionType.VECTOR;
    }

    @Override
    public void setValue(SimulationScope simulationScope, String moduleName, Object value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public VNode getCopy() {
        return new OneIndexLValue(identifier, (Expression)expression.getCopy(), line, column);
    }
    
}
