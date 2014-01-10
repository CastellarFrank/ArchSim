/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Expressions;

import VerilogCompiler.Interpretation.ExpressionValue;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SyntacticTree.VNode;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public abstract class Expression extends VNode {

    ExpressionType type = ExpressionType.ERROR;
    
    public Expression(int line, int column) {
        super(line, column);
    }

    public ExpressionType getType() {
        return type;
    }

    public void setType(ExpressionType type) {
        this.type = type;
    }
    
    public abstract ExpressionValue evaluate(VerilogCompiler.Interpretation.SimulationScope simulationScope, String moduleName);
}
