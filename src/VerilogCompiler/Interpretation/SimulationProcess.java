/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.Interpretation;

import VerilogCompiler.SyntacticTree.ModuleItems.AlwaysBlock;
import VerilogCompiler.SyntacticTree.ModuleItems.InitialBlock;
import java.util.Vector;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class SimulationProcess {
    Vector<InitialBlock> initialBlocks = new Vector<InitialBlock>();
    Vector<AlwaysBlock> alwaysBlocks = new Vector<AlwaysBlock>();
    
    public void init() {
        for (InitialBlock initialBlock : initialBlocks) {
            initialBlock.execute();
        }
    }
    
    public void runStep() {
        for (AlwaysBlock alwaysBlock : alwaysBlocks) {
            alwaysBlock.execute();
        }
    }
}
