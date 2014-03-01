/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Expressions;

import VerilogCompiler.Interpretation.Convert;
import VerilogCompiler.Interpretation.ExpressionValue;
import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SyntacticTree.Operator;
import VerilogCompiler.SyntacticTree.VNode;
import VerilogCompiler.Utils.StringUtils;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
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

        ArrayList<ExpressionType> validTypes = new ArrayList<ExpressionType>();
        validTypes.add(ExpressionType.INTEGER);
        validTypes.add(ExpressionType.VECTOR);

        if (validTypes.contains(leftType) && validTypes.contains(rightType)) {
            return ExpressionType.INTEGER;
        } else {
            if (leftType == ExpressionType.ERROR || rightType == ExpressionType.ERROR) {
                return ExpressionType.ERROR;
            }
            ErrorHandler.getInstance().handleError(line, column,
                    "left and right operands must be integer");
            return ExpressionType.ERROR;
        }
    }

    @Override
    public ExpressionValue evaluate(VerilogCompiler.Interpretation.SimulationScope simulationScope, String moduleName) {
        ExpressionValue l = left.evaluate(simulationScope, moduleName);
        ExpressionValue r = right.evaluate(simulationScope, moduleName);

        if (l.value == null || r.value == null) {
            if (l.xValue && r.xValue || l.zValue && r.zValue) {
                return new ExpressionValue(l.zValue && r.zValue, l.xValue && r.xValue);
            }
            return new ExpressionValue();
        }

        int leftRadix = Convert.baseToRadix(l.base), rightRadix = Convert.baseToRadix(r.base);

        String leftValue = l.getValueAsString();
        String rightValue = r.getValueAsString();

        long maxBits = Math.max(l.bits, r.bits);

        switch (expressionOperator) {
            case _OP_ADD:
                return new ExpressionValue(Convert.decimalToBinary(Integer.parseInt(leftValue, leftRadix)
                        + Integer.parseInt(rightValue, rightRadix)),
                        maxBits + 1);
            case _OP_MINUS:
                return new ExpressionValue(Convert.decimalToBinary(Integer.parseInt(leftValue, leftRadix)
                        - Integer.parseInt(rightValue, rightRadix)),
                        maxBits + 1);
            case _OP_TIMES:
                return new ExpressionValue(Convert.decimalToBinary(Integer.parseInt(leftValue, leftRadix)
                        * Integer.parseInt(rightValue, rightRadix)),
                        l.bits + r.bits);
            case _OP_DIV:
                return new ExpressionValue(Convert.decimalToBinary(Integer.parseInt(leftValue, leftRadix)
                        / Integer.parseInt(rightValue, rightRadix)),
                        l.bits - r.bits + 1);
            case _OP_MOD:
                return new ExpressionValue(Convert.decimalToBinary(Integer.parseInt(leftValue, leftRadix)
                        % Integer.parseInt(rightValue, rightRadix)),
                        Math.min(l.bits, r.bits));
            case _OP_EQ:
                return new ExpressionValue(Integer.parseInt(leftValue, leftRadix) 
                        == Integer.parseInt(rightValue, rightRadix) ? 1 : 0,
                        1);
            case _OP_NOTEQ:
                return new ExpressionValue(Integer.parseInt(leftValue) != Integer.parseInt(rightValue) ? 1 : 0,
                        1);
            case _OP_LOG_AND:
                return new ExpressionValue(Integer.parseInt(leftValue) == 1
                        && Integer.parseInt(rightValue) == 1 ? 1 : 0, 1);
            case _OP_LOG_OR:
                return new ExpressionValue(Integer.parseInt(leftValue) == 1
                        || Integer.parseInt(rightValue) == 1 ? 1 : 0, 1);
            case _OP_LST:
                return new ExpressionValue(Integer.parseInt(leftValue) < Integer.parseInt(rightValue) ? 1 : 0,
                        1);
            case _OP_LSTEQ:
                return new ExpressionValue(Integer.parseInt(leftValue) <= Integer.parseInt(rightValue) ? 1 : 0,
                        1);
            case _OP_GRT:
                return new ExpressionValue(Integer.parseInt(leftValue) > Integer.parseInt(rightValue) ? 1 : 0,
                        1);
            case _OP_GRTEQ:
                return new ExpressionValue(Integer.parseInt(leftValue) >= Integer.parseInt(rightValue) ? 1 : 0,
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
                return new ExpressionValue(Integer.parseInt(leftValue) ^ ~Integer.parseInt(rightValue),
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

    @Override
    public VNode getCopy() {
        return new BinaryExpression((Expression) left.getCopy(), expressionOperator, (Expression) right.getCopy(), line, column);
    }
}
