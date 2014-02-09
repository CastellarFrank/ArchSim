/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Expressions;

import VerilogCompiler.Interpretation.ExpressionValue;
import VerilogCompiler.Interpretation.InstanceModuleScope;
import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SyntacticTree.VNode;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public abstract class LValue extends VNode {

    public LValue(int line, int column) {
        super(line, column);
    }
    
    public abstract void setValue(SimulationScope simulationScope, 
            String moduleName, Object value);
    
    public abstract void setValue(InstanceModuleScope scope, ExpressionValue value);
}
