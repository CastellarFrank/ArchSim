/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Statements;

import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SyntacticTree.VNode;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class AssignStatement extends Statement {
    Assignment assignment;

    public AssignStatement(Assignment assignment, int line, int column) {
        super(line, column);
        this.assignment = assignment;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    @Override
    public String toString() {
        return "assign " + assignment.toString();
    }

    @Override
    public ExpressionType validateSemantics() {
        return assignment.validateSemantics();
    }

    @Override
    public void execute(SimulationScope simulationScope, String moduleName) {
        assignment.execute(simulationScope, moduleName);
    }

    @Override
    public VNode getCopy() {
        return new AssignStatement((Assignment)assignment.getCopy(), line, column);
    }
    
}
