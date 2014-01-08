/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Expressions;

import VerilogCompiler.SemanticCheck.ExpressionType;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class SimpleNumberExpression extends NumberExpression {
    int unsignedNumber;

    public SimpleNumberExpression(int unsignedNumber, int line, int column) {
        super(line, column);
        this.unsignedNumber = unsignedNumber;
    }

    public int getUnsignedNumber() {
        return unsignedNumber;
    }

    public void setUnsignedNumber(int unsignedNumber) {
        this.unsignedNumber = unsignedNumber;
    }

    @Override
    public String toString() {
        return Integer.toString(unsignedNumber);
    }

    @Override
    public ExpressionType validateSemantics() {
        return ExpressionType.INTEGER;
    }
    
}
