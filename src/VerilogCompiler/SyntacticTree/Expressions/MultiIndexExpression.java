/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Expressions;

import VerilogCompiler.Interpretation.ExpressionValue;
import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SemanticCheck.SemanticCheck;
import VerilogCompiler.SemanticCheck.VariableInfo;
import VerilogCompiler.SyntacticTree.Range;
import VerilogCompiler.SyntacticTree.VNode;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class MultiIndexExpression extends PrimaryExpression {
    String identifier;
    ArrayList<Expression> indexes;
    Range range;

    public MultiIndexExpression(String identifier, ArrayList<Expression> indexes, Range range, int line, int column) {
        super(line, column);
        this.identifier = identifier;
        this.indexes = indexes;
        this.range = range;
    }

    @Override
    public String toString() {
        String stringIndexes = "";
        for (Expression expression : indexes) {
            stringIndexes += "[" + expression + "]";
        }
        return String.format("%s %s %s", identifier, stringIndexes, range == null?"":range);
    }

    @Override
    public ExpressionType validateSemantics() {
        if (indexes.size() == 2 && range != null || indexes.size() > 2) {
            ErrorHandler.getInstance().handleError(line, column, 
                    "only 2 dimensiones array are supported");
            return ExpressionType.ERROR;
        }
        if (!SemanticCheck.getInstance().variableIsRegistered(identifier)) {
            ErrorHandler.getInstance().handleError(line, column, 
                    identifier + " is not declared");
            return ExpressionType.ERROR;
        } else if (!SemanticCheck.getInstance().variableIsVector(identifier) && range != null) {
            ErrorHandler.getInstance().handleError(line, column, 
                    identifier + " is not a vector");
            return ExpressionType.ERROR;
        }
        if (indexes.size() > 1 && !SemanticCheck.getInstance().variableIsArrayOrVector(identifier)) {
             ErrorHandler.getInstance().handleError(line, column, 
                    identifier + " is not a vector/array");
            return ExpressionType.ERROR;
        }
        if (indexes.size() == 1 && range != null && 
                SemanticCheck.getInstance().variableIsArray(identifier) &&
                SemanticCheck.getInstance().variableIsVector(identifier))
            return ExpressionType.INTEGER;
        return ExpressionType.VECTOR;
    }

    @Override
    public ExpressionValue evaluate(SimulationScope simulationScope, String moduleName) {
        ExpressionValue index0 = indexes.get(0).evaluate(simulationScope, moduleName);
        ExpressionValue current = simulationScope.getVariableValue(moduleName, identifier);
        if (range != null) {
            ArrayList<ExpressionValue> values = (ArrayList<ExpressionValue>)current.value;
            if (values != null) {
                Integer indexValue = Integer.parseInt(index0.value.toString());
                ExpressionValue val = values.get(indexValue);
                
                ExpressionValue min = range.getMinValue().evaluate(simulationScope, moduleName);
                ExpressionValue max = range.getMaxValue().evaluate(simulationScope, moduleName);
                
                Integer minLimit = Integer.parseInt(min.value.toString());
                Integer maxLimit = Integer.parseInt(max.value.toString());
                
                int realMin = adjustPositionToSize(minLimit, simulationScope, moduleName);
                int realMax = adjustPositionToSize(maxLimit, simulationScope, moduleName);
                ArrayList<Integer> secondValues = (ArrayList<Integer>) val.value;
                
                if (realMin < 0 || realMax >= values.size())
                    /*ERROR!!!*/
                return new ExpressionValue(secondValues.subList(realMin, realMax), 0);
            } 
            return null;
        } else {
            if (indexes.size() == 2) {
                ExpressionValue index1 = indexes.get(1).evaluate(simulationScope, moduleName);
                ArrayList<ExpressionValue> values = (ArrayList<ExpressionValue>)current.value;
                
                Integer indexValue = Integer.parseInt(index0.value.toString());
                ExpressionValue val = values.get(indexValue);
                ArrayList<Integer> secondValues = (ArrayList<Integer>) val.value;
                
                Integer index2Value = Integer.parseInt(index1.value.toString());
                return new ExpressionValue(secondValues.get(index2Value), 32);
            } else {
                return null;
            }
        }
    }
    
    public int adjustPositionToSize(int position, 
            SimulationScope simulationScope, String moduleName) {
        VariableInfo info = simulationScope.getVariableInfo(moduleName, identifier);
        if (position < Math.min(info.LSB, info.MSB) || position > Math.max(info.LSB, info.MSB)) {
            return Integer.MAX_VALUE;
        }
        if (info.isBigEndian) {
            int min = info.LSB;
            return position - min;
        } else {
            int max = info.LSB;
            return max - position;
        }
    }

    @Override
    public VNode getCopy() {
        Range newRange = (Range)range.getCopy();
        ArrayList<Expression> newIndexes = new ArrayList<Expression>();
        for (Expression expression : indexes) {
            newIndexes.add((Expression)expression.getCopy());
        }
        return new MultiIndexExpression(identifier, newIndexes, newRange, line, column);
    }
    
}
