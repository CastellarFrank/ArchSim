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
public class GateInstanceName extends VNode {
    String identifier;
    Range range;

    public GateInstanceName(String identifier, Range range, int line, int column) {
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
        return String.format("%s%s", identifier, range==null?"":range.toString());
    }

    @Override
    public ExpressionType validateSemantics() {
        if (SemanticCheck.getInstance().variableIsRegistered(identifier)) {
            ErrorHandler.getInstance().handleError(line, column, 
                    identifier + " is already defined");
            return ExpressionType.ERROR;
        } else {
            
        }
        if (range != null)
            range.validateSemantics();
        
        return null;
    }

    @Override
    public VNode getCopy() {
        return new GateInstanceName(identifier, range != null?(Range)range.getCopy():null,
                line, column);
    }
    
}
