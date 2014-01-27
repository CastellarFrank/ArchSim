/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Expressions;

import VerilogCompiler.Interpretation.ExpressionValue;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SyntacticTree.VNode;

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

    @Override
    public ExpressionValue evaluate(VerilogCompiler.Interpretation.SimulationScope simulationScope, String moduleName) {
        ExpressionValue cond = condition.evaluate(simulationScope, moduleName);
        ExpressionValue trueValue = trueExpression.evaluate(simulationScope, moduleName);
        ExpressionValue falseValue = falseExpression.evaluate(simulationScope, moduleName);
        
        Object value;
        long bits;
        if (Integer.parseInt(cond.value.toString()) == 1) {
            value = trueValue.value;
            bits = trueValue.bits;
        } else {
            value = falseValue.value;
            bits = falseValue.bits;
        }
        
        return new ExpressionValue(value, bits);
    }

    @Override
    public VNode getCopy() {
        return new TernaryExpression((Expression)condition.getCopy(), 
                (Expression)trueExpression.getCopy(), (Expression)falseExpression.getCopy(), 
                line, column);
    }
    
}
