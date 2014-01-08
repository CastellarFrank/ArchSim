/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Expressions;

import VerilogCompiler.SemanticCheck.ExpressionType;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class TernaryExpression extends Expression {
    Expression condition;
    Expression trueExpression;
    Expression falseExpression;

    public TernaryExpression(Expression condition, Expression trueExpression, Expression falseExpression, int line, int column) {
        super(line, column);
        this.condition = condition;
        this.trueExpression = trueExpression;
        this.falseExpression = falseExpression;
    }

    public Expression getCondition() {
        return condition;
    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    public Expression getTrueExpression() {
        return trueExpression;
    }

    public void setTrueExpression(Expression trueExpression) {
        this.trueExpression = trueExpression;
    }

    public Expression getFalseExpression() {
        return falseExpression;
    }

    public void setFalseExpression(Expression falseExpression) {
        this.falseExpression = falseExpression;
    }

    @Override
    public String toString() {
        return String.format("%s?%s:%s", condition, trueExpression, falseExpression);
    }

    @Override
    public ExpressionType validateSemantics() {
        condition.validateSemantics();
        trueExpression.validateSemantics();
        falseExpression.validateSemantics();
        
        return ExpressionType.INTEGER;
    }
    
}
