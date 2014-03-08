/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SemanticCheck;

import VerilogCompiler.Interpretation.ExpressionValue;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class VariableInfo {
    public boolean isPort = false;
    public boolean isArray = false;
    public boolean isVector = false;
    public boolean isNumeric = false;
    public boolean isModuleInstance = false;
    public boolean isBigEndian = false; /*BIG ENDIAN = MSB...LSB; */
    public DataType type;
    public ArrayList<DataType> acceptedTypes = new ArrayList<DataType>();
    public ExpressionValue value;

    public VariableInfo() {
        value = new ExpressionValue();
    }    
    
    public int MSB = 0, LSB = 0;
    
    public void addAcceptedType(DataType type) {
        if (!acceptedTypes.contains(type))
            acceptedTypes.add(type);
    }
    
    public void setLimits(int LSB, int MSB) {
        this.LSB = LSB;
        this.MSB = MSB;
        if (LSB < MSB)
            isBigEndian = true;
    }
    
    public ExpressionValue getExpressionValue() {
        return value;
    }
    
    public VariableInfo getCopy() {
        VariableInfo copy = new VariableInfo();
        copy.LSB = LSB;
        copy.MSB = MSB;
        copy.isPort = isPort;
        copy.isArray = isArray;
        copy.isVector = isVector;
        copy.isNumeric = isNumeric;
        copy.isModuleInstance = isModuleInstance;
        copy.isBigEndian = isBigEndian;
        copy.acceptedTypes = acceptedTypes;
        copy.value = value.getCopy();
        
        return copy;
    }
}
