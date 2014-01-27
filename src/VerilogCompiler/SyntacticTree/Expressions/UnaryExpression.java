/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Expressions;

import VerilogCompiler.Interpretation.ExpressionValue;
import VerilogCompiler.Interpretation.MathHelper;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SyntacticTree.Operator;
import VerilogCompiler.SyntacticTree.VNode;
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

    @Override
    public ExpressionValue evaluate(VerilogCompiler.Interpretation.SimulationScope simulationScope, String moduleName) {
        ExpressionValue value = expression.evaluate(simulationScope, moduleName);
        Integer intValue = Integer.parseInt(value.value.toString());
        
        switch (expressionOperator) {
            case _OP_ADD:
                return new ExpressionValue(intValue, value.bits);
            case _OP_MINUS:
                return new ExpressionValue(-intValue, value.bits);
            case _OP_LOG_NEG:
                return new ExpressionValue(intValue == 0 ? 1: 0, 1);
            case _OP_BIT_NEG:
                return new ExpressionValue(~intValue, value.bits);
            case _OP_BIT_AND:
                return new ExpressionValue(MathHelper.unaryAnd(intValue), value.bits);
            case _OP_BIT_OR:
                return new ExpressionValue(MathHelper.unaryOr(intValue), value.bits);
            case _OP_BIT_NAND:
                return new ExpressionValue(~MathHelper.unaryAnd(intValue), value.bits);
            case _OP_BIT_NOR:
                return new ExpressionValue(~MathHelper.unaryOr(intValue), value.bits);
            case _OP_BIT_XOR:
                return new ExpressionValue(MathHelper.unaryXor(intValue), value.bits);
            case _OP_BIT_XNOR:
                return new ExpressionValue(~MathHelper.unaryXor(intValue), value.bits);
            default:
                return null;
        }
        
    }

    @Override
    public VNode getCopy() {
        return new UnaryExpression(expressionOperator, (PrimaryExpression)expression.getCopy(), 
                line, column);
    }
    
}
