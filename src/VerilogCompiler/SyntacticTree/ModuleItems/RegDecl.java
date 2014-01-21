/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.ModuleItems;

import VerilogCompiler.SemanticCheck.DataType;
import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SemanticCheck.SemanticCheck;
import VerilogCompiler.SemanticCheck.VariableInfo;
import VerilogCompiler.SyntacticTree.Others.RegVariable;
import VerilogCompiler.SyntacticTree.Range;
import VerilogCompiler.Utils.StringUtils;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class RegDecl extends ModuleItem {
    Range range;
    ArrayList<RegVariable> regVariableList;

    public RegDecl(Range range, ArrayList<RegVariable> regVariableList, int line, int column) {
        super(line, column);
        this.range = range;
        this.regVariableList = regVariableList;
    }

    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    public ArrayList<RegVariable> getRegVariableList() {
        return regVariableList;
    }

    public void setRegVariableList(ArrayList<RegVariable> regVariableList) {
        this.regVariableList = regVariableList;
    }

    @Override
    public String toString() {
        return String.format("reg %s %s;", range==null?"":range.toString(), 
                StringUtils.getInstance().ListToString(regVariableList, ","));
    }

    @Override
    public ExpressionType validateSemantics() {
        boolean isArray = false;
        if (range != null) {
            range.validateSemantics();
            isArray = true;
        }
        
        for (RegVariable regVariable : regVariableList) {
            if (SemanticCheck.getInstance().variableIsRegistered(regVariable.getIdentifier())) {
                ErrorHandler.getInstance().handleError(line, column, 
                        regVariable.getIdentifier() + " is already defined");
            } else {
                VariableInfo varInfo = new VariableInfo();
                varInfo.type = DataType.VARIABLE;
                varInfo.isArray = isArray;
                SemanticCheck.getInstance().registerVariable(regVariable.getIdentifier(), varInfo);
            }
            regVariable.validateSemantics();
        }
        return null;
    }

    @Override
    public void executeModuleItem() {
        /*TODO*/
    }
    
}
