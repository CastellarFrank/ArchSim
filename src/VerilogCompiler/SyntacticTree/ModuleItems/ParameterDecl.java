/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.ModuleItems;

import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SemanticCheck.SemanticCheck;
import VerilogCompiler.SemanticCheck.VariableInfo;
import VerilogCompiler.Utils.StringUtils;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class ParameterDecl extends ModuleItem {
    ArrayList<ParameterAssign> paramAssignList;

    public ParameterDecl(ArrayList<ParameterAssign> paramAssignList, int line, int column) {
        super(line, column);
        this.paramAssignList = paramAssignList;
    }

    public ArrayList<ParameterAssign> getParamAssignList() {
        return paramAssignList;
    }

    public void setParamAssignList(ArrayList<ParameterAssign> paramAssignList) {
        this.paramAssignList = paramAssignList;
    }

    @Override
    public String toString() {
        return String.format("parameter %s;", 
                StringUtils.getInstance().ListToString(paramAssignList, ","));
    }

    @Override
    public ExpressionType validateSemantics() {
        for (ParameterAssign parameterAssign : paramAssignList) {
            if (SemanticCheck.getInstance().variableIsRegistered(parameterAssign.getIdentifier())) {
                ErrorHandler.getInstance().handleError(line, column, 
                        parameterAssign.getIdentifier() + " is already defined");
            } else {
                VariableInfo varInfo = new VariableInfo();
                SemanticCheck.getInstance().registerVariable(parameterAssign.getIdentifier(), varInfo);
            }
            parameterAssign.validateSemantics();
        }
        return null;
    }
    
}
