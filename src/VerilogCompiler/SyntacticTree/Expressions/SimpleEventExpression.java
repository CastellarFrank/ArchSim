/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Expressions;

import VerilogCompiler.Interpretation.ExpressionValue;
import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SyntacticTree.VNode;
import java.math.BigInteger;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class SimpleEventExpression extends EventExpression {
    Expression expression;
    long previousValue;
    boolean isXorZValue = true;

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
        ExpressionValue newValue = expression.evaluate(simulationScope, moduleName);
        
        if(isXorZValue){
            if(!newValue.xValue && !newValue.zValue){
                this.isXorZValue = false;
                this.previousValue = new BigInteger(newValue.value.toString()).longValue();
                return new ExpressionValue(1, 1);
            }   
        }else{
            if(newValue.xValue || newValue.zValue){
                this.isXorZValue = true;
                return new ExpressionValue(1, 1);
            }else{
                long prevLongValue = previousValue;
                long newLongValue = new BigInteger(newValue.value.toString()).longValue();
                this.previousValue = newLongValue;
                if(prevLongValue != newLongValue)
                    return new ExpressionValue(1, 1);
            }
        }
        
        return new ExpressionValue(0, 1);
    }

    @Override
    public VNode getCopy() {
        return new SimpleEventExpression((Expression)expression.getCopy(), line, column);
    }

    @Override
    public void clearEvent() {
        this.isXorZValue = true;
    }
    
}
