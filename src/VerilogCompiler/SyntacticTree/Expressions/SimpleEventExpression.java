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
    
}
