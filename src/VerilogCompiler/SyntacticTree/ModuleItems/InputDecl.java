/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.ModuleItems;

import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SemanticCheck.SemanticCheck;
import VerilogCompiler.SemanticCheck.VariableInfo;
import VerilogCompiler.SyntacticTree.Range;
import VerilogCompiler.SyntacticTree.VNode;
import VerilogCompiler.Utils.StringUtils;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class InputDecl extends ModuleItem {
    Range range;
    ArrayList<Variable> variableList;

    public InputDecl(Range range, ArrayList<Variable> variableList, int line, int column) {
        super(line, column);
        this.range = range;
        this.variableList = variableList;
    }

    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    public ArrayList<Variable> getVariableList() {
        return variableList;
    }

    public void setVariableList(ArrayList<Variable> variableList) {
        this.variableList = variableList;
    }

    @Override
    public String toString() {
        return String.format("input %s %s;", range==null?"":range.toString(), 
                StringUtils.getInstance().ListToString(variableList, ","));
    }

    @Override
    public ExpressionType validateSemantics() {
        boolean isArray = false;
        if (range != null) {
            range.validateSemantics();
            isArray = true;
        }
        
        for (Variable variable : variableList) {
            if (SemanticCheck.getInstance().variableIsRegistered(variable.getIdentifier())) {
                ErrorHandler.getInstance().handleError(line, column, 
                        variable.getIdentifier() + " is already defined");
            } else {
                VariableInfo varInfo = new VariableInfo();
                varInfo.isArray = isArray;
                SemanticCheck.getInstance().registerVariable(variable.getIdentifier(), varInfo);
            }
            variable.validateSemantics();
        }
        return null;
    }

    @Override
    public void executeModuleItem() {
        /*TODO*/
    }

    @Override
    public VNode getCopy() {
        Range newRange = (Range)range.getCopy();
        ArrayList<Variable> vars = new ArrayList<Variable>();
        for (Variable variable : variableList) {
            vars.add((Variable)variable.getCopy());
        }
        return new InputDecl(newRange, vars, line, column);
    }
    
}
