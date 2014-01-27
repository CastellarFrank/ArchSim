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
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class SimpleEventExpression extends EventExpression {
    Expression expression;

    public SimpleEventExpression(Expression expression, int line, int column) {
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
        return String.format("%s", expression);
    }

    @Override
    public ExpressionType validateSemantics() {
        return expression.validateSemantics();
    }

    @Override
    public ExpressionValue evaluate(SimulationScope simulationScope, String moduleName) {
        ExpressionValue exp = expression.evaluate(simulationScope, moduleName);
        Integer intValue = Integer.parseInt(exp.value.toString());
        
        return new ExpressionValue(intValue == 0 ? 0 : 1, 1);
    }

    @Override
    public VNode getCopy() {
        return new SimpleEventExpression((Expression)expression.getCopy(), line, column);
    }
    
}
