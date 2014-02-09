/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree;

import VerilogCompiler.SemanticCheck.ExpressionType;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public abstract class VNode {
    public int line, column;

    public VNode(int line, int column) {
        this.line = line;
        this.column = column;
    }    
    
    @Override
    public abstract String toString();
    
    public abstract ExpressionType validateSemantics();
    
    public abstract VNode getCopy();
}
