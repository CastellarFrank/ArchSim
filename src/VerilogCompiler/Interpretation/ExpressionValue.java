/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.Interpretation;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class ExpressionValue {
    public Object value;
    public int bits;

    public ExpressionValue() {
    }

    public ExpressionValue(Object value, int bits) {
        this.value = value;
        this.bits = bits;
    }
    
}
