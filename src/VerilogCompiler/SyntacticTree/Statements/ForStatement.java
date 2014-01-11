/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Statements;

import VerilogCompiler.Interpretation.ExpressionValue;
import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SyntacticTree.Expressions.Expression;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class ForStatement extends Statement {
    Assignment initialValue;
    Expression condition;
    Assignment increment;
    Statement body;

    public ForStatement(Assignment initialValue, Expression condition, Assignment increment, Statement body, int line, int column) {
        super(line, column);
        this.initialValue = initialValue;
        this.condition = condition;
        this.increment = increment;
        this.body = body;
    }

    public Assignment getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(Assignment initialValue) {
        this.initialValue = initialValue;
    }

    public Expression getCondition() {
        return condition;
    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    public Assignment getIncrement() {
        return increment;
    }

    public void setIncrement(Assignment increment) {
        this.increment = increment;
    }

    public Statement getBody() {
        return body;
    }

    public void setBody(Statement body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return String.format("for (%s; %s; %s) \n %s", initialValue, condition, increment, body);
    }

    @Override
    public ExpressionType validateSemantics() {
        initialValue.validateSemantics();
        condition.validateSemantics();
        increment.validateSemantics();
        body.validateSemantics();
        
        return null;
    }

    @Override
    public void execute(SimulationScope simulationScope, String moduleName) {
        initialValue.execute(simulationScope, moduleName);
        ExpressionValue cond = condition.evaluate(simulationScope, moduleName);
        Integer intCondition = Integer.parseInt(cond.value.toString());
        while (intCondition == 1) {
            body.execute(simulationScope, moduleName);
            
            increment.execute(simulationScope, moduleName);
            
            cond = condition.evaluate(simulationScope, moduleName);
            intCondition = Integer.parseInt(cond.value.toString());
        }
    }
    
}
