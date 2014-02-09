/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Statements;

import VerilogCompiler.Interpretation.ExpressionValue;
import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SyntacticTree.Expressions.Expression;
import VerilogCompiler.SyntacticTree.VNode;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class RepeatStatement extends Statement {
    Expression condition;
    Statement body;

    public RepeatStatement(Expression condition, Statement body, int line, int column) {
        super(line, column);
        this.condition = condition;
        this.body = body;
    }

    public Expression getCondition() {
        return condition;
    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    public Statement getBody() {
        return body;
    }

    public void setBody(Statement body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return String.format("repeat (%s) %s", condition, body);
    }

    @Override
    public ExpressionType validateSemantics() {
        condition.validateSemantics();
        body.validateSemantics();
        return null;
    }

    @Override
    public void execute(SimulationScope simulationScope, String moduleName) {
        ExpressionValue value = condition.evaluate(simulationScope, moduleName);
        Integer intValue = Integer.parseInt(value.value.toString());
        
        for (int i = 0; i < intValue; i++) {
            body.execute(simulationScope, moduleName);
        }
    }

    @Override
    public VNode getCopy() {
        return new RepeatStatement((Expression)condition.getCopy(), 
                (Statement)body.getCopy(), line, column);
    }
    
}
