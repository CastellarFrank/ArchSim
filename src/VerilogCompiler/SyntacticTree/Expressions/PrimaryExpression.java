/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Expressions;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public abstract class PrimaryExpression extends Expression {

    public PrimaryExpression(int line, int column) {
        super(line, column);
    }
    
}
