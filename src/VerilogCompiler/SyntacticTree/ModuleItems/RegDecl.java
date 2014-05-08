/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.ModuleItems;

import VerilogCompiler.Interpretation.ExpressionValue;
import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.DataType;
import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SemanticCheck.SemanticCheck;
import VerilogCompiler.SemanticCheck.VariableInfo;
import VerilogCompiler.SyntacticTree.Expressions.SimpleNumberExpression;
import VerilogCompiler.SyntacticTree.Others.RegVariable;
import VerilogCompiler.SyntacticTree.Range;
import VerilogCompiler.SyntacticTree.VNode;
import VerilogCompiler.Utils.StringUtils;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
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
        boolean isVector = false;
        int size = 0;
        if (range != null) {
            range.validateSemantics();
            size = (int)((SimpleNumberExpression)range.getMinValue()).getUnsignedNumber();
            size -= (int)((SimpleNumberExpression)range.getMaxValue()).getUnsignedNumber();
            size = Math.abs(size) + 1;
            isVector = true;
        }
        
        for (RegVariable regVariable : regVariableList) {
            if (SemanticCheck.getInstance().variableIsRegistered(regVariable.getIdentifier())) {
                ErrorHandler.getInstance().handleError(line, column, 
                        regVariable.getIdentifier() + " is already defined");
            } else {
                int arraySize = regVariable.getSize();
                if (arraySize != 0)
                    size = arraySize;
                VariableInfo varInfo = new VariableInfo();
                varInfo.type = DataType.VARIABLE;
                varInfo.isVector = isVector;
                varInfo.isArray = regVariable.isArray();
                varInfo.LSB = 0;
                varInfo.MSB = size;
                varInfo.isBigEndian = true;
                if (isVector) {
                    Object[] values = new Object[size];
                    ExpressionValue value = new ExpressionValue(values, 0);
                    varInfo.value = value;
                }
                SemanticCheck.getInstance().registerVariable(regVariable.getIdentifier(), varInfo);
            }
            regVariable.validateSemantics();
        }
        return null;
    }

    @Override
    public void executeModuleItem(SimulationScope simulationScope, String moduleInstanceId) {
        /*TODO*/
    }

    @Override
    public VNode getCopy() {
        
        Range newRange = null;
        if (range != null) {
            newRange = (Range)range.getCopy();
        }
        ArrayList<RegVariable> newRgs = new ArrayList<RegVariable>();
        for (RegVariable regVariable : regVariableList) {
            newRgs.add((RegVariable)regVariable.getCopy());
        }
        RegDecl copy = new RegDecl(newRange, newRgs, line, column);
        return copy;
    }
    
}
