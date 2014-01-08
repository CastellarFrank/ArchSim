/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Statements;

import VerilogCompiler.SemanticCheck.ExpressionType;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
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
    
}
