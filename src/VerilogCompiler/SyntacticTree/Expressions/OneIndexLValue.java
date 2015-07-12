/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Expressions;

import VerilogCompiler.Interpretation.Convert;
import VerilogCompiler.Interpretation.ExpressionValue;
import VerilogCompiler.Interpretation.InstanceModuleScope;
import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SemanticCheck.SemanticCheck;
import VerilogCompiler.SyntacticTree.VNode;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class OneIndexLValue extends LValue {
    String identifier;
    Expression expression;

    public OneIndexLValue(String identifier, Expression expression, int line, int column) {
        super(line, column);
        this.identifier = identifier;
        this.expression = expression;
    }


    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", identifier, expression);
    }

    @Override
    public ExpressionType validateSemantics() {
        if (!SemanticCheck.getInstance().variableIsRegistered(identifier)) {
            ErrorHandler.getInstance().handleError(line, column, 
                    identifier + " is not declared");
            return ExpressionType.ERROR;
        } else if (!SemanticCheck.getInstance().variableIsArrayOrVector(identifier)) {
            ErrorHandler.getInstance().handleError(line, column, 
                    identifier + " is not a vector/array");
            return ExpressionType.ERROR;
        }
        ExpressionType result = expression.validateSemantics();
        if (result != ExpressionType.INTEGER && 
                result != ExpressionType.VECTOR) {
            ErrorHandler.getInstance().handleError(line, column, 
                    "index must be an integer");
            return ExpressionType.ERROR;
        }
        if (SemanticCheck.getInstance().variableIsVector(identifier) && 
                !SemanticCheck.getInstance().variableIsArray(identifier))
            return ExpressionType.INTEGER;
        return ExpressionType.VECTOR;
    }

    @Override
    public void setValue(SimulationScope simulationScope, String moduleInstanceId, Object value) {
        ExpressionValue index = expression.evaluate(simulationScope, moduleInstanceId);
        if (index.xValue || index.zValue)
            return;
        int intIndex = Convert.getInteger(index);
        ExpressionValue address = simulationScope.getScope(moduleInstanceId).getVariableValue(identifier);
        if(value != null && !value.toString().matches("[xXzZ]")){
            address.xValue = false;
            address.zValue = false;
        } 
        ((Object[])address.value)[intIndex] = value;
    }

    @Override
    public VNode getCopy() {
        return new OneIndexLValue(identifier, (Expression)expression.getCopy(), line, column);
    }

    @Override
    public void setValue(InstanceModuleScope scope, ExpressionValue value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
