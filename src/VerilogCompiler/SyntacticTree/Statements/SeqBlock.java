/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Statements;

import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SyntacticTree.VNode;
import VerilogCompiler.Utils.StringUtils;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class SeqBlock extends  Statement {
    ArrayList<Statement> statementList;

    public SeqBlock(int line, int column) {
        super(line, column);
        statementList = new ArrayList<Statement>();
    }

    public SeqBlock(ArrayList<Statement> statementList, int line, int column) {
        super(line, column);
        this.statementList = statementList;
    }

    public ArrayList<Statement> getStatementList() {
        return statementList;
    }

    public void setStatementList(ArrayList<Statement> statementList) {
        this.statementList = statementList;
    }

    @Override
    public String toString() {
        return String.format("begin\n%s\nend", 
                StringUtils.getInstance().ListToString(statementList, ";\n"));
    }

    @Override
    public ExpressionType validateSemantics() {
        for (Statement statement : statementList) {
            statement.validateSemantics();
        }
        return null;
    }

    @Override
    public void execute(SimulationScope simulationScope, String moduleName) {
        for (Statement statement : statementList) {
            statement.execute(simulationScope, moduleName);
        }
    }

    @Override
    public VNode getCopy() {
        ArrayList<Statement> stmts = new ArrayList<Statement>();
        for (Statement statement : statementList) {
            stmts.add((Statement)statement.getCopy());
        }
        return new SeqBlock(stmts, line, column);
    }
    
}
