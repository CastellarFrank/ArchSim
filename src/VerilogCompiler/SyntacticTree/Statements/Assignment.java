/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Statements;

import VerilogCompiler.Interpretation.ExpressionValue;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SyntacticTree.Expressions.Expression;
import VerilogCompiler.SyntacticTree.Expressions.LValue;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
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
    
    public void execute(VerilogCompiler.Interpretation.SimulationScope simulationScope, String moduleName) {
        ExpressionValue value = expression.evaluate(simulationScope, moduleName);
        lvalue.setValue(simulationScope, moduleName, value.value);
    }
}
