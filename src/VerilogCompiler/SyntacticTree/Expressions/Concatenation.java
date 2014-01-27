/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Expressions;

import Exceptions.UnsuportedFeature;
import Simulation.Configuration;
import VerilogCompiler.Interpretation.ExpressionValue;
import VerilogCompiler.Interpretation.MathHelper;
import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SyntacticTree.VNode;
import VerilogCompiler.Utils.StringUtils;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class Concatenation extends LValue {
    ArrayList<Expression> expressionList;
    
    public Concatenation(ArrayList<Expression> expressionList, int line, int column) {
        super(line, column);
        this.expressionList = expressionList;
    }

    public ArrayList<Expression> getExpressionList() {
        return expressionList;
    }

    public void setExpressionList(ArrayList<Expression> expressionList) {
        this.expressionList = expressionList;
    }

    @Override
    public String toString() {
        return String.format("{%s}", 
                StringUtils.getInstance().ListToString(expressionList, ", "));
    }

    @Override
    public ExpressionType validateSemantics() {
        for (Expression expression : expressionList) {
            if (expression.validateSemantics() == ExpressionType.ERROR) {
                ErrorHandler.getInstance().handleError(line, column, 
                        "concatenation elements must be array or numeric");
            }
        }
        return ExpressionType.ARRAY;
    }

    @Override
    public void setValue(SimulationScope simulationScope, 
            String moduleName,Object value) {
        String binaryRep = Integer.toBinaryString((Integer)value);
        if (Configuration.DEBUG_MODE)
            System.out.println("binary number " + binaryRep);
        int intValue = (Integer) value;
        int currentPos = 31;
        for (int i = expressionList.size() - 1; i >= 0; i--) {
            if (currentPos < 0) break;
            IdentifierExpression expression = (IdentifierExpression) expressionList.get(i);
            if (expression == null)
                throw new UnsuportedFeature("concatenation members can only be identifiers");
            ExpressionValue loc = simulationScope.getVariableValue(moduleName, expression.getIdentifier());
            long min = currentPos - loc.bits + 1;
            if (min < 0) min = 0;
            long portion = MathHelper.getBitSelection(intValue, min, currentPos);
            loc.value = new Long(portion);
            
            currentPos -= min - 1;
        }
    }

    @Override
    public VNode getCopy() {
        ArrayList<Expression> copies = new ArrayList<Expression>();
        for (Expression expression : expressionList) {
            copies.add((Expression)expression.getCopy());
        }
        return new Concatenation(expressionList, line, column);
    }
    
}
