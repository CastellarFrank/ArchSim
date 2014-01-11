/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Statements;

import Exceptions.UnsuportedFeature;
import VerilogCompiler.Interpretation.ExpressionValue;
import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SemanticCheck.SemanticCheck;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class IdentifierEventControl extends EventControlStatement {
    String identifier;

    public IdentifierEventControl(String identifier, int line, int column) {
        super(line, column);
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return "@" + identifier;
    }

    @Override
    public ExpressionType validateSemantics() {
        if (!SemanticCheck.getInstance().variableIsRegistered(identifier)) {
            ErrorHandler.getInstance().handleError(line, column, 
                    identifier + " is not defined");
        }
        return null;
    }

    @Override
    public void execute(SimulationScope simulationScope, String moduleName) {
        ExpressionValue value = simulationScope.getVariableValue(moduleName, identifier);
        Integer intValue = Integer.parseInt(value.value.toString());
        /*TODO*/
        throw new UnsuportedFeature("identifier event control statement not supported");
    }
    
}
