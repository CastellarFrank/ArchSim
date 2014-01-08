/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Expressions;

import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SemanticCheck.SemanticCheck;
import VerilogCompiler.SyntacticTree.Range;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class RangeLValue extends LValue {
    String identifier;
    Range range;

    public RangeLValue(String identifier, Range range, int line, int column) {
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
        return String.format("%s %s", identifier, range);
    }

    @Override
    public ExpressionType validateSemantics() {
        if (!SemanticCheck.getInstance().variableIsRegistered(identifier)) {
            ErrorHandler.getInstance().handleError(line, column, 
                    identifier + " is not declared");
            return ExpressionType.ERROR;
        } else if (!SemanticCheck.getInstance().variableIsVector(identifier)) {
            ErrorHandler.getInstance().handleError(line, column, 
                    identifier + " is not a vector");
            return ExpressionType.ERROR;
        }
        range.validateSemantics();
        if (SemanticCheck.getInstance().variableIsVector(identifier))
            return ExpressionType.VECTOR;
        return ExpressionType.INTEGER; /*En realidad un vector de tamaño 1*/
    }
    
}
