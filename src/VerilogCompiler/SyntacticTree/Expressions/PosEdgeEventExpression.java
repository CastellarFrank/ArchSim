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
public class PosEdgeEventExpression extends EventExpression {
    Expression expression;
    long previousValue;
    boolean isXorZvalue = true;

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
    public ExpressionValue evaluate(SimulationScope simulationScope, String moduleInstanceId) {
        ExpressionValue newValue = expression.evaluate(simulationScope, moduleInstanceId);
        
        if(isXorZvalue){
            if (!newValue.xValue && !newValue.zValue){
                this.isXorZvalue = false;
                long newLongValue = Long.parseLong(newValue.value.toString());
                this.previousValue = newLongValue;
                if(newLongValue  == 1)
                    return new ExpressionValue(1, 1);
            }
        }else{
            if(this.previousValue == 0){
                if(newValue.xValue || newValue.zValue){
                    this.isXorZvalue = true;
                    return new ExpressionValue(1, 1);
                }else{
                    long newLongValue = Long.parseLong(newValue.value.toString());
                    this.previousValue = newLongValue;
                    if(newLongValue == 1)
                        return new ExpressionValue(1, 1);
                }
            }else{
                if(newValue.xValue || newValue.zValue){
                    this.isXorZvalue = true;
                }else{
                    this.previousValue = Long.parseLong(newValue.value.toString());
                }
            }
        }
        return new ExpressionValue(0, 1);
    }

    @Override
    public VNode getCopy() {
        PosEdgeEventExpression copy = new PosEdgeEventExpression((Expression)expression.getCopy(), line, column);
        copy.type = type;
        return copy;
    }

    @Override
    public void clearEvent() {
        this.isXorZvalue = true;
    }
}
