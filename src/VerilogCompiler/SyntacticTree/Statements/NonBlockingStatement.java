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
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class NonBlockingStatement extends Statement {
    LValue lvalue;
    Expression  expression;

    public NonBlockingStatement(LValue lvalue, Expression expression, int line, int column) {
        super(line, column);
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
        return String.format("%s <= %s", lvalue, expression);
    }

    @Override
    public ExpressionType validateSemantics() {
        lvalue.validateSemantics();
        expression.validateSemantics();
        return null;
    }

    @Override
    public void execute(SimulationScope simulationScope, String moduleName) {
        ExpressionValue value = expression.evaluate(simulationScope, moduleName);
        /*TODO: Registrar la asignacion para futura ejecucion, 
         * solo se evalua la expression para tener el valor listo*/
    }
    
}
