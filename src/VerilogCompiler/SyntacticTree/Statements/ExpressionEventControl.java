/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Statements;

import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SyntacticTree.Expressions.EventExpression;
import VerilogCompiler.SyntacticTree.VNode;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class ExpressionEventControl extends EventControlStatement {
    EventExpression eventExpression;

    public ExpressionEventControl(EventExpression eventExpression, int line, int column) {
        super(line, column);
        this.eventExpression = eventExpression;
    }

    public EventExpression getEventExpression() {
        return eventExpression;
    }

    public void setEventExpression(EventExpression eventExpression) {
        this.eventExpression = eventExpression;
    }

    @Override
    public String toString() {
        return String.format("@(%s)", eventExpression.toString());
    }

    @Override
    public ExpressionType validateSemantics() {
        eventExpression.validateSemantics();
        return null;
    }

    @Override
    public void execute(SimulationScope simulationScope, String moduleName) {
        eventExpression.evaluate(simulationScope, moduleName);
    }

    @Override
    public VNode getCopy() {
        return new ExpressionEventControl((EventExpression)eventExpression.getCopy(), line, column);
    }
    
}
