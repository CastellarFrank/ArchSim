/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Expressions;

import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SyntacticTree.Operator;
import VerilogCompiler.Utils.StringUtils;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class BinaryExpression extends Expression {
    Expression left;
    Operator expressionOperator;
    Expression right;

    public BinaryExpression(Expression left, Operator expressionOperator, Expression right, int line, int column) {
        super(line, column);
        this.left = left;
        this.expressionOperator = expressionOperator;
        this.right = right;
    }

    public Expression getLeft() {
        return left;
    }

    public void setLeft(Expression left) {
        this.left = left;
    }

    public Operator getExpressionOperator() {
        return expressionOperator;
    }

    public void setExpressionOperator(Operator expressionOperator) {
        this.expressionOperator = expressionOperator;
    }

    public Expression getRight() {
        return right;
    }

    public void setRight(Expression right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", left, 
                StringUtils.getInstance().OperatorToString(expressionOperator), 
                right);
    }

    @Override
    public ExpressionType validateSemantics() {
        ExpressionType leftType = left.validateSemantics();
        ExpressionType rightType = right.validateSemantics();
        
        if (leftType == ExpressionType.INTEGER && rightType == ExpressionType.INTEGER)
            return ExpressionType.INTEGER;
        else {
            if (leftType == ExpressionType.ERROR || rightType == ExpressionType.ERROR)
                return ExpressionType.ERROR;
            ErrorHandler.getInstance().handleError(line, column, 
                    "left and right operands must be integer");
            return ExpressionType.ERROR;
        }
    }
    
}
