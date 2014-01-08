/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.ModuleItems;

import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SemanticCheck.SemanticCheck;
import VerilogCompiler.SyntacticTree.Others.SensitiveList;
import VerilogCompiler.SyntacticTree.Statements.Statement;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class InitialBlock extends ModuleItem {
    SensitiveList sensitiveList;
    Statement statement;

    public InitialBlock(SensitiveList sensitiveList, Statement statement, int line, int column) {
        super(line, column);
        this.sensitiveList = sensitiveList;
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
        return String.format("initial @(%s) \n%s", sensitiveList, statement);
    }

    @Override
    public ExpressionType validateSemantics() {
        SemanticCheck.getInstance().setInsideProceduralBlock(true);
        sensitiveList.validateSemantics();
        statement.validateSemantics();
        SemanticCheck.getInstance().setInsideProceduralBlock(false);
        return null;
    }
    
}
