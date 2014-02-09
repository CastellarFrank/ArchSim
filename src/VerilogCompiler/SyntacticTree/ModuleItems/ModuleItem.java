/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.ModuleItems;

import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SyntacticTree.VNode;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public abstract class ModuleItem extends VNode {

    public ModuleItem(int line, int column) {
        super(line, column);
    }
    
    public void executeModuleItem(SimulationScope simulationScope, String moduleInstanceId) {
        System.out.println("executing module item: " + this.getClass().getSimpleName());
    }
}
