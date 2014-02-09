/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Statements;

import VerilogCompiler.SyntacticTree.VNode;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public abstract class Statement extends VNode {

    public Statement(int line, int column) {
        super(line, column);
    }
    
    public abstract void execute(VerilogCompiler.Interpretation.SimulationScope simulationScope, String moduleName);
}
