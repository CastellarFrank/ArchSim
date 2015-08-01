/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Statements;

import VerilogCompiler.Interpretation.ExpressionValue;
import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SyntacticTree.Expressions.Expression;
import VerilogCompiler.SyntacticTree.Expressions.LValue;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class Assignment {
    int line, column;
    LValue lvalue;
    Expression expression;

    public Assignment(LValue lvalue, Expression expression, int line, int column) {
        this.line = line;
        this.column = column;
        this.lvalue = lvalue;
        this.expression = expression;
    }

    public LValue getLvalue() {
        return lvalue;
    }

    public void setLvalue(LValue lvalue) {
        this.lvalue = lvalue;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return String.format("%s = %s", lvalue, expression);
    }
    
    public ExpressionType validateSemantics() {
        ExpressionType lvalueType = lvalue.validateSemantics();
        ExpressionType valueType = expression.validateSemantics();
        
        if (lvalueType == ExpressionType.ERROR || valueType == ExpressionType.ERROR)
            return ExpressionType.ERROR;
        //TODO: ALGO FALTA AQUI!
        return null;
    }
    
    public Assignment getCopy() {
        return new Assignment(lvalue, expression, line, column);
    }
    
    public void execute(SimulationScope simulationScope, String moduleInstanceId) {
        ExpressionValue value = expression.evaluate(simulationScope, moduleInstanceId);
        lvalue.setValue(simulationScope, moduleInstanceId, value);
    }
    
    public void scheduleAssign(SimulationScope simulationScope, String moduleInstanceId) {
        ExpressionValue value = expression.evaluate(simulationScope, moduleInstanceId);
        simulationScope.getScope(moduleInstanceId).scheduleVariableAssign(lvalue, value);
    }
}
