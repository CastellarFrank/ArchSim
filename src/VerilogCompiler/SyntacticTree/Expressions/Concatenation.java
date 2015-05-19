/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Expressions;

import Exceptions.UnsuportedFeature;
import Simulation.Configuration;
import VerilogCompiler.Interpretation.ExpressionValue;
import VerilogCompiler.Interpretation.InstanceModuleScope;
import VerilogCompiler.Interpretation.MathHelper;
import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SemanticCheck.VariableInfo;
import VerilogCompiler.SyntacticTree.VNode;
import VerilogCompiler.Utils.StringUtils;
import java.math.BigInteger;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
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
            String moduleName, Object value) {
        if (Configuration.DEBUG_MODE)
            System.out.println("binary number " + value);
        long intValue = 0;
        if (value instanceof Integer) {
            intValue = (Integer) value;
        } else if (value instanceof Long) {
            intValue = (Long) value;
        } else if (value instanceof BigInteger) {
            BigInteger tmp = (BigInteger) value;
            BigInteger s2 = new BigInteger(tmp.toString(), 2);
            intValue = s2.longValue();
        }
        int currentPos = 0;
        int maxQuantityToConsume = 64;
        for (int i = expressionList.size() - 1; i >= 0; i--) {
            if (currentPos + 1> maxQuantityToConsume) break;
            IdentifierExpression expression = (IdentifierExpression) expressionList.get(i);
            if (expression == null)
                throw new UnsuportedFeature("concatenation members can only be identifiers");
            VariableInfo varInfo = simulationScope.getVariableInfo(moduleName, expression.getIdentifier());
            ExpressionValue loc = simulationScope.getVariableValue(moduleName, expression.getIdentifier());
            
            int quantity = currentPos + 1 + varInfo.signalSize > maxQuantityToConsume
                           ? maxQuantityToConsume - (currentPos + 1)
                           :varInfo.signalSize;
            
            long portion = MathHelper.getBitSelection(intValue, currentPos, quantity);
            loc.value = new BigInteger(Long.toBinaryString(portion));
            
            currentPos += varInfo.signalSize;
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

    @Override
    public void setValue(InstanceModuleScope scope, ExpressionValue value) {
        String binaryRep = Integer.toBinaryString((Integer)value.value);
        if (Configuration.DEBUG_MODE)
            System.out.println("binary number " + binaryRep);
        long intValue = (Integer) value.value;
        
        int currentPos = 0;
        int maxQuantityToConsume = 64;
        for (int i = expressionList.size() - 1; i >= 0; i--) {
            if (currentPos < 0) break;
            IdentifierExpression expression = (IdentifierExpression) expressionList.get(i);
            if (expression == null)
                throw new UnsuportedFeature("concatenation members can only be identifiers");
            ExpressionValue loc = scope.getVariableValue(expression.getIdentifier());
            VariableInfo varInfo = scope.getVariableInfo(expression.getIdentifier());
            
            int quantity = currentPos + 1 + varInfo.signalSize > maxQuantityToConsume
                           ? maxQuantityToConsume - (currentPos + 1)
                           :varInfo.signalSize;
            
            long portion = MathHelper.getBitSelection(intValue, currentPos, quantity);
            loc.value = portion;
            currentPos += varInfo.signalSize;
        }
    }
    
}
