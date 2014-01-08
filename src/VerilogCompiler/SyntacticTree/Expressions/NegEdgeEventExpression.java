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
public class NegEdgeEventExpression extends EventExpression {
    Expression expression;

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
    
}
