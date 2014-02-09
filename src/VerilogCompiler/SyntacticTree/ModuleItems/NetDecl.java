/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.ModuleItems;

import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.DataType;
import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SemanticCheck.SemanticCheck;
import VerilogCompiler.SemanticCheck.VariableInfo;
import VerilogCompiler.SyntacticTree.NetType;
import VerilogCompiler.SyntacticTree.Range;
import VerilogCompiler.SyntacticTree.VNode;
import VerilogCompiler.Utils.StringUtils;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class NetDecl extends ModuleItem {
    NetType type;
    Range range;
    ArrayList<Variable> variableList;

    public NetDecl(NetType type, Range range, ArrayList<Variable> variableList, int line, int column) {
        super(line, column);
        this.type = type;
        this.range = range;
        this.variableList = variableList;
    }

    public NetType getType() {
        return type;
    }

    public void setType(NetType type) {
        this.type = type;
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
        return String.format("%s %s %s;", 
                StringUtils.getInstance().NetTypeToString(type),
                range==null?"":range.toString(),
                StringUtils.getInstance().ListToString(variableList, ","));
    }

    @Override
    public ExpressionType validateSemantics() {
        boolean isVector = false;
        if (range != null) {
            range.validateSemantics();
            isVector = true;
        }
        
        for (Variable variable : variableList) {            
            if (SemanticCheck.getInstance().variableIsRegistered(variable.getIdentifier())) {
                ErrorHandler.getInstance().handleError(line, column, 
                        variable.getIdentifier() + " is already defined");
            } else {
                VariableInfo varInfo = new VariableInfo();
                varInfo.type = DataType.NET;
                varInfo.isVector = isVector;
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
        Range newRange = (Range)range.getCopy();
        ArrayList<Variable> vars = new ArrayList<Variable>();
        for (Variable variable : variableList) {
            vars.add((Variable)variable.getCopy());
        }
        return new NetDecl(type, newRange, vars, line, column);
    }
    
}
