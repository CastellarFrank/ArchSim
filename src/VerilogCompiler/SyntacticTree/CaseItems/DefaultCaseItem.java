/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.CaseItems;

import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SemanticCheck.SemanticCheck;
import VerilogCompiler.SyntacticTree.Statements.Statement;
import VerilogCompiler.SyntacticTree.VNode;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class DefaultCaseItem extends CaseItem {
    Statement statement;

    public DefaultCaseItem(Statement statement, int line, int column) {
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
        return String.format("default: %s", this.statement);
    }

    @Override
    public ExpressionType validateSemantics() {
        if (SemanticCheck.getInstance().isDefaultCaseItemFound()) {
            ErrorHandler.getInstance().handleError(line, column, 
                    "more than one default case item found");
        } else {
            SemanticCheck.getInstance().setDefaultCaseItemFound(true);
        }
        statement.validateSemantics();
        return null;
    }

    @Override
    public void execute(SimulationScope simulationScope, String moduleName) {
        statement.execute(simulationScope, moduleName);
    }

    @Override
    public ArrayList<Integer> getValue(SimulationScope simulationScope, String moduleName) {
        return null;
    }

    @Override
    public VNode getCopy() {
        return new DefaultCaseItem((Statement)statement.getCopy(), line, column);
    }
    
}
