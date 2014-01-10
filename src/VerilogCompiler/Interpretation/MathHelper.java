/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.Interpretation;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class MathHelper {

    public static int unaryAnd(int value) {
        int bitVal = getBit(value, 0);
        for (int i = 1; i < 31; i++) {
            bitVal = bitVal & getBit(value, i);
        }
        
        return bitVal;
    }
    
    public static int unaryOr(int value) {
        int bitVal = getBit(value, 0);
        for (int i = 1; i < 31; i++) {
            bitVal = bitVal | getBit(value, i);
        }
        
        return bitVal;
    }
    
    public static int unaryXor(int value) {
        int bitVal = getBit(value, 0);
        for (int i = 1; i < 31; i++) {
            bitVal = bitVal ^ getBit(value, i);
        }
        
        return bitVal;
    }

    public static int getBit(int value, int position) {
        return (value >> position) & 1;
    }
}
