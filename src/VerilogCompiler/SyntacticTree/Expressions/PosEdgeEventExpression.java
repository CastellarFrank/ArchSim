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
public class PosEdgeEventExpression extends EventExpression {
    Expression expression;
    ExpressionValue previousValue = new ExpressionValue(1, 1);

    public PosEdgeEventExpression(Expression expression, int line, int column) {
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
        return String.format("posedge %s", expression);
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
        
        if (oldValue == 0 && nValue == 1)
            return new ExpressionValue(1, 1);
        else 
            return new ExpressionValue(0, 1);
    }

    @Override
    public VNode getCopy() {
        return new PosEdgeEventExpression((Expression)expression.getCopy(), line, column);
    }
    
}
