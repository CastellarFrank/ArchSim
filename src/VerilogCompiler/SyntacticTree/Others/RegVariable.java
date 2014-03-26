/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Others;

import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SemanticCheck.SemanticCheck;
import VerilogCompiler.SyntacticTree.Expressions.SimpleNumberExpression;
import VerilogCompiler.SyntacticTree.Range;
import VerilogCompiler.SyntacticTree.VNode;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class RegVariable extends VNode {
    String identifier;
    Range range;

    public RegVariable(String identifier, Range range, int line, int column) {
        super(line, column);
        this.identifier = identifier;
        this.range = range;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    @Override
    public String toString() {
        return String.format("%s %s", identifier, 
                range==null?"":range.toString());
    }
    
    public boolean isArray(){
        return range != null;
    }
    
    public int getSize() {
        int size = 0;
        if (range != null) {
            range.validateSemantics();
            size = (int)((SimpleNumberExpression)range.getMinValue()).getUnsignedNumber();
            size -= (int)((SimpleNumberExpression)range.getMaxValue()).getUnsignedNumber();
            size = Math.abs(size) + 1;
        }
        return size;
    }
    
    @Override
    public ExpressionType validateSemantics() {
        if (range != null) {
            range.validateSemantics();
            SemanticCheck.getInstance().setVariableIsArray(identifier, true);
        }
        if (SemanticCheck.getInstance().variableIsArrayOrVector(identifier))
            return ExpressionType.ARRAY;
        if (SemanticCheck.getInstance().variableIsNumeric(identifier))
            return ExpressionType.INTEGER;
        return ExpressionType.ERROR;
    }

    @Override
    public VNode getCopy() {
        return new RegVariable(identifier, range != null ? (Range)range.getCopy(): null,
                line, column);
    }
    
}
