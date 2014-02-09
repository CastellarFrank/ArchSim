/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.ModuleItems;

import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SemanticCheck.SemanticCheck;
import VerilogCompiler.SyntacticTree.Range;
import VerilogCompiler.SyntacticTree.VNode;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class Variable extends VNode {
    String identifier;
    Range range;

    public Variable(String identifier, int line, int column) {
        super(line, column);
        this.identifier = identifier;
    }

    public Variable(String identifier, Range range, int line, int column) {
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

    @Override
    public String toString() {
        return identifier;
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
        return new Variable(identifier, range != null? (Range)range.getCopy(): null, 
                line, column);
    }
    
}
