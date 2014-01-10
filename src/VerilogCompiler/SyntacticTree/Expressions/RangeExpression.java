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
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class RangeExpression extends PrimaryExpression {
    String identifier;
    Expression minValue;
    Expression maxValue;

    public RangeExpression(String identifier, Expression minValue, Expression maxValue, int line, int column) {
        super(line, column);
        this.identifier = identifier;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Expression getMinValue() {
        return minValue;
    }

    public void setMinValue(Expression minValue) {
        this.minValue = minValue;
    }

    public Expression getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Expression maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public String toString() {
        return String.format("%s[%s:%s]", identifier, minValue, maxValue);
    }

    @Override
    public ExpressionType validateSemantics() {
        if (!SemanticCheck.getInstance().variableIsRegistered(identifier)) {
            ErrorHandler.getInstance().handleError(line, column, 
                    identifier + " is not declared");
            return ExpressionType.ERROR;
        } else if (!SemanticCheck.getInstance().variableIsArrayOrVector(identifier)) {
            ErrorHandler.getInstance().handleError(line, column, 
                    identifier + " is not a vector/array");
            return ExpressionType.ERROR;
        }
        minValue.validateSemantics();
        maxValue.validateSemantics();
        
        return type = ExpressionType.VECTOR;
    }

    @Override
    public ExpressionValue evaluate(SimulationScope simulationScope, String moduleName) {
        switch (type) {
            case ERROR: case INTEGER: case ARRAY: return null;
            case VECTOR:
                ExpressionValue complete = simulationScope.getVariableValue(moduleName, identifier);
                ExpressionValue min = minValue.evaluate(simulationScope, moduleName);
                ExpressionValue max = maxValue.evaluate(simulationScope, moduleName);
                
                Integer minLimit = Integer.parseInt(min.value.toString());
                Integer maxLimit = Integer.parseInt(max.value.toString());
                
                int realMin = adjustPositionToSize(minLimit, simulationScope, moduleName);
                int realMax = adjustPositionToSize(maxLimit, simulationScope, moduleName);
                
                ArrayList<Integer> values = (ArrayList<Integer>) complete.value;
                
                if (realMin < 0 || realMax >= values.size())
                    /*ERROR!!!*/
                return new ExpressionValue(values.subList(realMin, realMax), 0);
            default: return null;
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
    
}
