/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.Interpretation;

import Simulation.Configuration;
import VerilogCompiler.SyntacticTree.Base;
import java.math.BigInteger;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class Convert {
    public static Integer getInteger(ExpressionValue value) {
        return Integer.parseInt(value.getValueAsString(), Convert.baseToRadix(value.base));
    }
    
    public static Integer binaryToDecimal(Integer value) {
        return Integer.parseInt(Integer.toBinaryString(value), 10);
    }
    
    public static Integer voltageToLogicValue(double voltage) {
        if (voltage >= Configuration.LOGIC_1_VOLTAGE)
            return 1;
        return 0;
    }
    
    public static BigInteger decimalToBinary(Integer value) {
        String binaryString = Integer.toBinaryString(value);
        
        if (binaryString.length() < 32 || value > 0) {
            return new BigInteger(binaryString);
            //return Integer.parseInt(binaryString);
        } else {
            //Integer abs = Math.abs(value);
            String newBinary = Integer.toBinaryString(value);
            
            return new BigInteger(newBinary);
        }
    }
    
    public static Boolean getBoolean(ExpressionValue value) {
        return Long.parseLong(value.getValueAsString()) == 1;
    }
    
    public static String arrayToString(Object[] array) {
        String result = "[";
        for (int i = 0; i < array.length; i++) {
            Object object = array[i];
            result += "," + object;
        }
        result += "]";
        return result.replaceFirst(",", "");
    }
    
    public static int baseToRadix(Base base) {
        switch (base) {
            case BINARY: return 2;
            case HEXADECIMAL: return 16;
            case OCTAL: return 8;
            default: return 10;
        }
    }
}
