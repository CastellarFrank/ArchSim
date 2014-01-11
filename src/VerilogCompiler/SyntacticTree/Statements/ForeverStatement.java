/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Statements;

import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.ExpressionType;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class ForeverStatement extends Statement {
    Statement statement;

    public ForeverStatement(Statement statement, int line, int column) {
        super(line, column);
        this.statement = statement;
    }

    public Statement getStatement() {
        return statement;
    }

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    @Override
    public String toString() {
        return String.format("forever (%s)", statement);
    }

    @Override
    public ExpressionType validateSemantics() {
        statement.validateSemantics();
        return null;
    }

    @Override
    public void execute(SimulationScope simulationScope, String moduleName) {
        while (true) {
            statement.execute(simulationScope, moduleName);
        }
    }
    
}
