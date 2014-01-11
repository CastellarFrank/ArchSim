/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Others;

import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SemanticCheck.SemanticCheck;
import VerilogCompiler.SyntacticTree.Range;
import VerilogCompiler.SyntacticTree.VNode;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
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
    
}
