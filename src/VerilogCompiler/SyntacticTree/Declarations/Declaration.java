/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Declarations;

import VerilogCompiler.SyntacticTree.VNode;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public abstract class Declaration extends VNode {

    public Declaration(int line, int column) {
        super(line, column);
    }
    
    @Override
    public abstract String toString();
}
