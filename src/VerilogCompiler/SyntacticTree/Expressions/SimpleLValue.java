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
    public void setValue(SimulationScope simulationScope, String instanceModuleId, ExpressionValue expressionValue) {
        ExpressionValue address = simulationScope.getVariableValue(instanceModuleId, identifier);
        VariableInfo info = simulationScope.getVariableInfo(instanceModuleId, identifier);
        int size = info.signalSize;
        String format = "%0" + size + "d";
        boolean xValue = expressionValue.xValue, zValue = expressionValue.zValue;
        
        if(xValue){
            address.setToXValue();
            return;
        }else if(zValue){
            address.setToZValue();
            return;
        }
        
        Object value = expressionValue.value;
        if(value == null){
            address.setValue(value);
            return;
        }
        
        try {  
            Integer realVal = 0;
            String adjustedValue = null;
            if (value instanceof String) {
                String stringValue = value.toString();
                if(stringValue.matches("[xX]")){
                    address.setToXValue();
                    return;
                }else if(stringValue.matches("[zZ]")){
                    address.setToZValue();
                    return;
                }
                realVal = new BigInteger(stringValue, 2).intValue();
                adjustedValue = value.toString();
            } else if (value instanceof Integer) {
                realVal = (Integer) value;
            } else if (value instanceof BigInteger) {
                realVal = ((BigInteger) value).intValue();
                adjustedValue = ((BigInteger) value).toString();
            }
            if (adjustedValue == null)
                adjustedValue = realVal.toString();

            if (size != value.toString().length()) {
                adjustedValue = String.format("%1$" + size + "s", adjustedValue).replace(' ', '0');
                //adjustedValue = String.format(format, realVal);
            }

            int index = adjustedValue.toString().length() - size;

            String portion = adjustedValue.toString().substring(adjustedValue.toString().length() - size);

            //Integer newVal = Integer.parseInt(portion);
            address.setValue(portion);
        }catch (Exception e) {
            System.out.println("error format: " + format);
            System.out.println(value);
            e.printStackTrace();
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
