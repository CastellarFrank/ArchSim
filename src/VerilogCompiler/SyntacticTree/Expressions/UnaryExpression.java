/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Expressions;

import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SyntacticTree.Operator;
import VerilogCompiler.Utils.StringUtils;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class UnaryExpression extends Expression {
    Operator expressionOperator;
    PrimaryExpression expression;

    public UnaryExpression(Operator expressionOperator, PrimaryExpression expression, int line, int column) {
        super(line, column);
        this.expressionOperator = expressionOperator;
        this.expression = expression;
    }

    public Operator getExpressionOperator() {
        return expressionOperator;
    }

    public void setExpressionOperator(Operator expressionOperator) {
        this.expressionOperator = expressionOperator;
    }

    public PrimaryExpression getExpression() {
        return expression;
    }

    public void setExpression(PrimaryExpression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return String.format("%s%s", 
                StringUtils.getInstance().OperatorToString(expressionOperator),
                expression);
    }

    @Override
    public ExpressionType validateSemantics() {
        return expression.validateSemantics();
    }
    
}
