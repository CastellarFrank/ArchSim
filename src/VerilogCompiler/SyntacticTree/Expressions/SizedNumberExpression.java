/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Expressions;

import VerilogCompiler.Interpretation.ExpressionValue;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SyntacticTree.Base;
import VerilogCompiler.SyntacticTree.VNode;
import VerilogCompiler.Utils.StringUtils;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class SizedNumberExpression extends NumberExpression {
    long size;
    Base base;
    String value;

    public SizedNumberExpression(long size, Base base, String value, int line, int column) {
        super(line, column);
        this.size = size;
        this.base = base;
        this.value = value;
    }
    
    public SizedNumberExpression(String size, Base base, String value, int line, int column) {
        super(line, column);
        this.size = new Long(size);
        this.base = base;
        this.value = value;
    }

    public long getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Base getBase() {
        return base;
    }

    public void setBase(Base base) {
        this.base = base;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%s%s%s", size, 
                StringUtils.getInstance().BaseToString(base), value);
    }

    @Override
    public ExpressionType validateSemantics() {
        return ExpressionType.INTEGER;
    }

    @Override
    public ExpressionValue evaluate(VerilogCompiler.Interpretation.SimulationScope simulationScope, 
    String moduleName) {
        ExpressionValue val = new ExpressionValue(value, size);
        return val;
    }

    @Override
    public VNode getCopy() {
        return new SizedNumberExpression(size, base, value, line, column);
    }
    
}
