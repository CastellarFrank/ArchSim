/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.ModuleItems;

import VerilogCompiler.SyntacticTree.Statements.Assignment;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public abstract class ContinuousAssign extends ModuleItem {
    ArrayList<Assignment> assignmentList;

    public ContinuousAssign(ArrayList<Assignment> assignmentList, int line, int column) {
        super(line, column);
        this.assignmentList = assignmentList;
    }

    public ArrayList<Assignment> getAssignmentList() {
        return assignmentList;
    }

    public void setAssignmentList(ArrayList<Assignment> assignmentList) {
        this.assignmentList = assignmentList;
    }
    
}
