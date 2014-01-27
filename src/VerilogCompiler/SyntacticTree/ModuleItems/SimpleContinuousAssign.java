/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.ModuleItems;

import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SyntacticTree.Statements.Assignment;
import VerilogCompiler.SyntacticTree.VNode;
import VerilogCompiler.Utils.StringUtils;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class SimpleContinuousAssign extends ContinuousAssign {

    public SimpleContinuousAssign(ArrayList<Assignment> assignmentList, int line, int column) {
        super(assignmentList, line, column);
    }

    @Override
    public String toString() {
        return String.format("assign %s;", 
                StringUtils.getInstance().ListToString(assignmentList, ","));
    }

    @Override
    public ExpressionType validateSemantics() {
        for (Assignment assignment : assignmentList) {
            assignment.validateSemantics();
        }
        return null;
    }

    @Override
    public void executeModuleItem() {
        /*TODO*/
    }

    @Override
    public VNode getCopy() {
        ArrayList<Assignment> newAssigns = new ArrayList<Assignment>();
        for (Assignment assignment : assignmentList) {
            newAssigns.add((Assignment)assignment.getCopy());
        }
        return new SimpleContinuousAssign(newAssigns, line, column);
    }
    
}
