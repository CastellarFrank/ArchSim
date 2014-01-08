/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Expressions;

import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SemanticCheck.SemanticCheck;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class RangeExpression extends PrimaryExpression {
    String identifier;
    Expression minValue;
    Expression maxValue;

    public RangeExpression(String identifier, Expression minValue, Expression maxValue, int line, int column) {
        super(line, column);
        this.identifier = identifier;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Expression getMinValue() {
        return minValue;
    }

    public void setMinValue(Expression minValue) {
        this.minValue = minValue;
    }

    public Expression getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Expression maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public String toString() {
        return String.format("%s[%s:%s]", identifier, minValue, maxValue);
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
        minValue.validateSemantics();
        maxValue.validateSemantics();
        
        return ExpressionType.ARRAY;
    }
    
}
