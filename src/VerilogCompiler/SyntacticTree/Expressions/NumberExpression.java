/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Expressions;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public abstract class NumberExpression extends PrimaryExpression {

    public NumberExpression(int line, int column) {
        super(line, column);
    }
    
}
