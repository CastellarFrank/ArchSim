/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Expressions;

import VerilogCompiler.Interpretation.ExpressionValue;
import VerilogCompiler.Interpretation.InstanceModuleScope;
import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SemanticCheck.SemanticCheck;
import VerilogCompiler.SemanticCheck.VariableInfo;
import VerilogCompiler.SyntacticTree.VNode;
import java.math.BigInteger;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class SimpleLValue extends LValue {

    String identifier;

    public SimpleLValue(String identifier, int line, int column) {
        super(line, column);
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return identifier;
    }

    @Override
    public ExpressionType validateSemantics() {
        if (!SemanticCheck.getInstance().variableIsRegistered(identifier)) {
            ErrorHandler.getInstance().handleError(line, column,
                    identifier + " is not declared");
            return ExpressionType.ERROR;
        } else {
            if (SemanticCheck.getInstance().variableHasTypeVariable(identifier) &&
                    !SemanticCheck.getInstance().isInsideProceduralBlock()) {
                ErrorHandler.getInstance().handleError(line, column, identifier + 
                        " cannot be assign outside a procedural block");
            }
            if (SemanticCheck.getInstance().variableIsArray(identifier)) {
                return ExpressionType.ARRAY;
            }
            if (SemanticCheck.getInstance().variableIsVector(identifier)) {
                return ExpressionType.VECTOR;
            } 
        }

        return ExpressionType.INTEGER;
    }

    @Override
    public void setValue(SimulationScope simulationScope, String instanceModuleId, Object value) {
        ExpressionValue address = simulationScope.getVariableValue(instanceModuleId, identifier);
        VariableInfo info = simulationScope.getVariableInfo(instanceModuleId, identifier);
        int size = Math.abs(info.MSB - info.LSB) + 1;
        String format = "%0" + size + "d";
        if (value != null) {
            try {  
                Integer realVal = 0;
                if (value instanceof String) {
                    realVal = new BigInteger(value.toString()).intValue();
                } else if (value instanceof Integer) {
                    realVal = (Integer) value;
                } else if (value instanceof BigInteger) {
                    realVal = ((BigInteger) value).intValue();
                }
                String adjustedValue = value.toString();
                
                if (size != value.toString().length()) {
                    adjustedValue = String.format(format, realVal);
                }
                
                int index = adjustedValue.toString().length() - size;
                
                String portion = adjustedValue.toString().substring(adjustedValue.toString().length() - size);
                
                //Integer newVal = Integer.parseInt(portion);
                address.setValue(portion);
            }catch (Exception e) {
                System.out.println("error format: " + format);
                System.out.println(value);
                e.printStackTrace();
                System.exit(0);
            }
        } else {
            address.setValue(value);
        }
    }

    @Override
    public VNode getCopy() {
        return new SimpleLValue(identifier, line, column);
    }

    @Override
    public void setValue(InstanceModuleScope scope, ExpressionValue value) {
        scope.setVariableValue(identifier, value);
    }
}
