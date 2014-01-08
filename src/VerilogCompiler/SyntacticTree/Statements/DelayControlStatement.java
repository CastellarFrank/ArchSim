/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Statements;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public abstract class DelayControlStatement extends Statement {

    public DelayControlStatement(int line, int column) {
        super(line, column);
    }
    
}
