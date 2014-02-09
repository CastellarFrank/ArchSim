/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.CaseItems;

import VerilogCompiler.Interpretation.ExpressionValue;
import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SyntacticTree.VNode;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public abstract class CaseItem extends VNode {

    public CaseItem(int line, int column) {
        super(line, column);
    }
    
    @Override
    public abstract String toString();
    
    public abstract ArrayList<ExpressionValue> getValue(SimulationScope simulationScope, String moduleName);
    
    public abstract void execute(SimulationScope simulationScope, String moduleName);
    
    @Override
    public abstract VNode getCopy();
}
