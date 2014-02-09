/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree;

import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SyntacticTree.Expressions.Expression;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class Range extends VNode {
    Expression minValue;
    Expression maxValue;

    public Range(Expression minValue, Expression maxValue, int line, int column) {
        super(line, column);
        this.minValue = minValue;
        this.maxValue = maxValue;
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
        return String.format("[%s:%s]", this.minValue, this.maxValue);
    }

    @Override
    public ExpressionType validateSemantics() {
        ExpressionType minReturnType = minValue.validateSemantics();
        ExpressionType  maxReturnType = maxValue.validateSemantics();
        
        if (minReturnType != ExpressionType.INTEGER || maxReturnType != ExpressionType.INTEGER)
        {
            ErrorHandler.getInstance().handleError(line, column, "range min and max value must be integer");
        }
        return null;
    }

    @Override
    public VNode getCopy() {
        return new Range((Expression)minValue.getCopy(), (Expression)maxValue.getCopy(), line, column);
    }
    
}
