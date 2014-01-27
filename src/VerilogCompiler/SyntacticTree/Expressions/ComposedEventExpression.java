/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Expressions;

import VerilogCompiler.Interpretation.ExpressionValue;
import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SyntacticTree.VNode;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class ComposedEventExpression extends EventExpression{
    EventExpression left;
    EventExpression right;

    public ComposedEventExpression(EventExpression left, EventExpression right, int line, int column) {
        super(line, column);
        this.left = left;
        this.right = right;
    }

    public EventExpression getLeft() {
        return left;
    }

    public void setLeft(EventExpression left) {
        this.left = left;
    }

    public EventExpression getRight() {
        return right;
    }

    public void setRight(EventExpression right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return String.format("%s or %s", left, right);
    }

    @Override
    public ExpressionType validateSemantics() {
        ExpressionType leftType = left.validateSemantics();
        ExpressionType rightType = right.validateSemantics();
        
        if (leftType == ExpressionType.INTEGER && rightType == ExpressionType.INTEGER ||
                leftType == ExpressionType.VECTOR && rightType == ExpressionType.INTEGER ||
                leftType == ExpressionType.INTEGER && rightType == ExpressionType.VECTOR ||
                leftType == ExpressionType.VECTOR && rightType == ExpressionType.VECTOR)
            return ExpressionType.INTEGER;
        else {
            ErrorHandler.getInstance().handleError(line, column, 
                    "left and right operands must be integer");
            return ExpressionType.ERROR;
        }
    }

    @Override
    public ExpressionValue evaluate(VerilogCompiler.Interpretation.SimulationScope simulationScope, String moduleName) {
        ExpressionValue l = left.evaluate(simulationScope, moduleName);
        ExpressionValue r = right.evaluate(simulationScope, moduleName);
        
        Integer leftValue = Integer.parseInt(l.toString());
        Integer rightValue = Integer.parseInt(r.toString());
        
        return new ExpressionValue(leftValue == 1 || rightValue == 1, 1);
    }

    @Override
    public VNode getCopy() {
        return new ComposedEventExpression((EventExpression)left.getCopy(), 
                (EventExpression)right.getCopy(), line, column);
    }
    
}
