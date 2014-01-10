/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Expressions;

import VerilogCompiler.Interpretation.ExpressionValue;
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

    @Override
    public ExpressionValue evaluate(VerilogCompiler.Interpretation.SimulationScope simulationScope, String moduleName) {
        ExpressionValue l = left.evaluate(simulationScope, moduleName);
        ExpressionValue r = right.evaluate(simulationScope, moduleName);
        
        String leftValue = l.value.toString();
        String rightValue = r.value.toString();
        
        int maxBits = Math.max(l.bits, r.bits);
        
        switch (expressionOperator) {
        case _OP_ADD:
            return new ExpressionValue(Integer.parseInt(leftValue) + Integer.parseInt(rightValue), 
                    maxBits + 1);
        case _OP_MINUS:
            return new ExpressionValue(Integer.parseInt(leftValue) - Integer.parseInt(rightValue), 
                    maxBits + 1);
        case _OP_TIMES:
            return new ExpressionValue(Integer.parseInt(leftValue) * Integer.parseInt(rightValue), 
                    l.bits + r.bits);
        case _OP_DIV:
            return new ExpressionValue(Integer.parseInt(leftValue) / Integer.parseInt(rightValue), 
                    l.bits - r.bits + 1);
        case _OP_MOD:
            return new ExpressionValue(Integer.parseInt(leftValue) % Integer.parseInt(rightValue), 
                    Math.min(l.bits, r.bits));
        case _OP_EQ:
            return new ExpressionValue(Integer.parseInt(leftValue) == Integer.parseInt(rightValue), 
                    1);
        case _OP_NOTEQ:
            return new ExpressionValue(Integer.parseInt(leftValue) != Integer.parseInt(rightValue), 
                    1);
        case _OP_LOG_AND:
            return new ExpressionValue(Integer.parseInt(leftValue) == 1 && 
                    Integer.parseInt(rightValue) == 1, 1);
        case _OP_LOG_OR:
            return new ExpressionValue(Integer.parseInt(leftValue) == 1 || 
                    Integer.parseInt(rightValue) == 1, 1);
        case _OP_LST:
            return new ExpressionValue(Integer.parseInt(leftValue) < Integer.parseInt(rightValue), 
                    1);
        case _OP_LSTEQ:
            return new ExpressionValue(Integer.parseInt(leftValue) <= Integer.parseInt(rightValue), 
                    1);
        case _OP_GRT:
            return new ExpressionValue(Integer.parseInt(leftValue) > Integer.parseInt(rightValue), 
                    1);
        case _OP_GRTEQ:
            return new ExpressionValue(Integer.parseInt(leftValue) >= Integer.parseInt(rightValue), 
                    1);
        case _OP_BIT_AND:
            return new ExpressionValue(Integer.parseInt(leftValue) & Integer.parseInt(rightValue), 
                    maxBits);
        case _OP_BIT_OR:
            return new ExpressionValue(Integer.parseInt(leftValue) | Integer.parseInt(rightValue), 
                    maxBits);
        case _OP_BIT_XOR:
            return new ExpressionValue(Integer.parseInt(leftValue) ^ Integer.parseInt(rightValue), 
                    maxBits);
        case _OP_BIT_XNOR:
            return new ExpressionValue(Integer.parseInt(leftValue) ^~ Integer.parseInt(rightValue), 
                    maxBits);
        case _OP_L_SHIFT:
            return new ExpressionValue(Integer.parseInt(leftValue) << Integer.parseInt(rightValue), 
                    l.bits);
        case _OP_R_SHIFT:
            return new ExpressionValue(Integer.parseInt(leftValue) >> Integer.parseInt(rightValue), 
                    l.bits);
        case _OP_R_ARIT_SHIFT:
            return new ExpressionValue(Integer.parseInt(leftValue) >>> Integer.parseInt(rightValue), 
                    l.bits);
        case _OP_L_ARIT_SHIFT:
            return new ExpressionValue(Integer.parseInt(leftValue) << Integer.parseInt(rightValue), 
                    l.bits);
        }
        
        return null;
    }
    
}
