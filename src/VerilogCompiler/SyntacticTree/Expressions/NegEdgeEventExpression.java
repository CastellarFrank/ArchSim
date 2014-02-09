/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Expressions;

import VerilogCompiler.Interpretation.ExpressionValue;
import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SyntacticTree.VNode;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class NegEdgeEventExpression extends EventExpression {
    Expression expression;
    ExpressionValue previousValue = new ExpressionValue(0, 1);

    public NegEdgeEventExpression(Expression expression, int line, int column) {
        super(line, column);
        this.expression = expression;
    }


    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return String.format("negedge %s", expression);
    }

    @Override
    public ExpressionType validateSemantics() {
        expression.validateSemantics();
        return ExpressionType.EDGE;
    }

    @Override
    public ExpressionValue evaluate(SimulationScope simulationScope, String moduleName) {
        ExpressionValue newValue = expression.evaluate(simulationScope, moduleName);
        Integer nValue = Integer.parseInt(newValue.value.toString());
        Integer oldValue = Integer.parseInt(previousValue.value.toString());
        
        previousValue = newValue;
        
        if (oldValue == 1 && nValue == 0)
            return new ExpressionValue(1, 1);
        else 
            return new ExpressionValue(0, 1);
    }

    @Override
    public VNode getCopy() {
        return new NegEdgeEventExpression((Expression)expression.getCopy(), line, column);
    }
    
}
