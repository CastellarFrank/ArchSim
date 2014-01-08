/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Expressions;

import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SemanticCheck.SemanticCheck;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class IdentifierExpression extends PrimaryExpression {
    String identifier;

    public IdentifierExpression(String identifier, int line, int column) {
        super(line, column);
        this.identifier = identifier;
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
        if (SemanticCheck.getInstance().variableIsRegistered(identifier)) {
            if (SemanticCheck.getInstance().variableIsArray(identifier))
                return ExpressionType.ARRAY;
            else
                return ExpressionType.INTEGER;
        } else {
            ErrorHandler.getInstance().handleError(line, column, 
                    identifier + " is not declared");
        }
        return null;
    }
    
}
