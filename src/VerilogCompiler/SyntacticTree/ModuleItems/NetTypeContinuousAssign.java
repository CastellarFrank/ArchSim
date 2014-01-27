/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.ModuleItems;

import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SyntacticTree.NetType;
import VerilogCompiler.SyntacticTree.Statements.Assignment;
import VerilogCompiler.SyntacticTree.VNode;
import VerilogCompiler.Utils.StringUtils;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class NetTypeContinuousAssign extends ContinuousAssign {
    NetType type;

    public NetTypeContinuousAssign(NetType type, ArrayList<Assignment> assignmentList, int line, int column) {
        super(assignmentList, line, column);
        this.type = type;
    }

    public NetType getType() {
        return type;
    }

    public void setType(NetType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("%s %s;", 
                StringUtils.getInstance().NetTypeToString(type),
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
        ArrayList<Assignment> assignments = new ArrayList<Assignment>();
        for (Assignment assignment : assignmentList) {
            assignments.add(assignment.getCopy());
        }
        return new NetTypeContinuousAssign(type, assignments, line, column);
    }
    
}
