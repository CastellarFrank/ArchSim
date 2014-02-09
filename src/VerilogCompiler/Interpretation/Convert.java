/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.Interpretation;

import Simulation.Configuration;
import VerilogCompiler.SyntacticTree.Base;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class Convert {
    public static Integer getInteger(ExpressionValue value) {
        return Integer.parseInt(value.value.toString(), Convert.baseToRadix(value.base));
    }
    
    public static Integer binaryToDecimal(Integer value) {
        return Integer.parseInt(Integer.toBinaryString(value), 10);
    }
    
    public static Integer voltageToLogicValue(double voltage) {
        if (voltage >= Configuration.LOGIC_1_VOLTAGE)
            return 1;
        return 0;
    }
    
    public static Integer decimalToBinary(Integer value) {
        return Integer.parseInt(Integer.toBinaryString(value));
    }
    
    public static Boolean getBoolean(ExpressionValue value) {
        return Integer.parseInt(value.value.toString()) == 1;
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
