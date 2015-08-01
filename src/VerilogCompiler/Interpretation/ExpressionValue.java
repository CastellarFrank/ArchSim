/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.Interpretation;

import VerilogCompiler.SyntacticTree.Base;
import java.math.BigInteger;

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
        if(xValue)
            this.setToXValue();
        else
            this.setToZValue();
    }
    
    public ExpressionValue() {
        this.zValue = true;
        this.xValue = false;
    }
    
    public String getValueAsString() {
        //return value != null ? value.toString() : null;
        
        if (value instanceof Integer) {
          
            String val = ((Integer) value).toString();
            
            try {
                String adjustedValue = String.format("%0" + bits + "d", val);
                return adjustedValue;
            }catch (Exception e) {
                String format = "%0" + bits + "d";
                return value.toString();
            }
            
        } else if (value instanceof BigInteger) {
            String val = ((BigInteger)value).toString();
            
            try {
                String adjustedValue = String.format("%0" + bits + "d", val);
                return adjustedValue;
            }catch (Exception e) {
                String format = "%0" + bits + "d";
                return value.toString();
            }
        } else if (value instanceof Object[]) {
            return "z";
        }
        return value != null ? value.toString() : null;
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
    
    public final void setToZValue(){
        if(this.value instanceof Object[]){
            Object[] values = (Object[]) value;
            for(int i = 0; i<values.length; i++)
                values [i] = "z";
        }else{
            this.value = "z";
        }
        this.bits = 1;
        this.base = Base.BINARY;
        this.xValue = false;
        this.zValue = true;
    }
    
    public final void setToXValue(){
        if(this.value instanceof Object[]){
            Object[] values = (Object[]) value;
            for(int i = 0; i<values.length; i++)
                values [i] = "x";
        }else{
            this.value = "x";
        }
        this.bits = 1;
        this.base = Base.BINARY;
        this.xValue = true;
        this.zValue = false;
    }
    
    public ExpressionValue getCopy() {
        ExpressionValue copy = new ExpressionValue();
        copy.bits = this.bits;
        
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
