/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.ModuleItems;

import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SemanticCheck.SemanticCheck;
import VerilogCompiler.SemanticCheck.VariableInfo;
import VerilogCompiler.SyntacticTree.VNode;
import VerilogCompiler.Utils.StringUtils;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class IntDeclaration extends ModuleItem {

    ArrayList<Variable> variables;

    public IntDeclaration(ArrayList<Variable> variables, int line, int column) {
        super(line, column);
        this.variables = variables;
    }
        
    @Override
    public String toString() {
        return "integer " + StringUtils.getInstance().ListToString(variables, ", ");
    }

    @Override
    public ExpressionType validateSemantics() {
        for (Variable variable : variables) {
            if (SemanticCheck.getInstance().variableIsRegistered(variable.getIdentifier())) {
                ErrorHandler.getInstance().handleError(line, column, 
                        variable.getIdentifier() + " is already defined");
            } else {
                VariableInfo varInfo = new VariableInfo();
                varInfo.isNumeric = true;
                SemanticCheck.getInstance().registerVariable(variable.getIdentifier(), varInfo);
            }
            variable.validateSemantics();
        }
        return null;
    }

    @Override
    public void executeModuleItem(SimulationScope simulationScope, String moduleInstanceId) {
        /*TODO*/
    }

    @Override
    public VNode getCopy() {
        ArrayList<Variable> newVariables = new ArrayList<Variable>();
        for (Variable variable : variables) {
            newVariables.add((Variable)variable.getCopy());
        }
        return new IntDeclaration(newVariables, line, column);
    }
    
}
