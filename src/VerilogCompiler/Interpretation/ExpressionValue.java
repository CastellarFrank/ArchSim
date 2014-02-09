/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.Interpretation;

import VerilogCompiler.SyntacticTree.Base;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class ExpressionValue {
    public Object value;
    public long bits;
    public boolean zValue, xValue;
    public Base base = Base.BINARY;

    public ExpressionValue(boolean zValue, boolean xValue) {
        this.zValue = zValue;
        this.xValue = xValue;
    }
    
    public ExpressionValue() {
        this.zValue = true;
        this.xValue = false;
    }

    public ExpressionValue(Object value, long bits) {
        this.value = value;
        this.bits = bits;
        this.zValue = false;
        this.xValue = false;
    }
    
    public ExpressionValue(Object value, long bits, Base base) {
        this.value = value;
        this.bits = bits;
        this.zValue = false;
        this.xValue = false;
        this.base = base;
    }
    
    public void setValue(Object value) {
        this.value = value;
        if (value != null) {
            this.xValue = this.zValue = false;
        }
    }
    
    public ExpressionValue getCopy() {
        ExpressionValue copy = new ExpressionValue();
        
        Object newValue = null;
        if (value instanceof Object[]) {
            newValue = new Object[((Object[])value).length];
        }
        copy.value = newValue;
        return copy;
    }

    @Override
    public String toString() {
        return "ExpressionValue{" + "value=" + value + ", bits=" + bits + ", zValue=" + zValue + ", xValue=" + xValue + ", base=" + base + '}';
    }

}
