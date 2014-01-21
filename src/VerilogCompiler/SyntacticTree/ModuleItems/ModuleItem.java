/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.ModuleItems;

import VerilogCompiler.SyntacticTree.VNode;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public abstract class ModuleItem extends VNode {

    public ModuleItem(int line, int column) {
        super(line, column);
    }
    
    public void executeModuleItem() {
        System.out.println("executing module item: " + this.getClass().getSimpleName());
    }
}
