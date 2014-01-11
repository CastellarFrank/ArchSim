/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Statements;

import Exceptions.UnsuportedFeature;
import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SyntacticTree.Expressions.NumberExpression;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class NumberDelayControl extends DelayControlStatement {
    NumberExpression delay;

    public NumberDelayControl(NumberExpression delay, int line, int column) {
        super(line, column);
        this.delay = delay;
    }

    public NumberExpression getDelay() {
        return delay;
    }

    public void setDelay(NumberExpression delay) {
        this.delay = delay;
    }

    @Override
    public String toString() {
        return "#" + delay;
    }

    @Override
    public ExpressionType validateSemantics() {
        delay.validateSemantics();
        return null;
    }

    @Override
    public void execute(SimulationScope simulationScope, String moduleName) {
        throw new UnsuportedFeature("number delay control event on expression is not supported");
    }
    
}
