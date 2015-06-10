/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.ModuleItems;

import VerilogCompiler.Interpretation.Convert;
import VerilogCompiler.Interpretation.ExpressionValue;
import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SemanticCheck.SemanticCheck;
import VerilogCompiler.SyntacticTree.Others.SensitiveList;
import VerilogCompiler.SyntacticTree.Statements.Statement;
import VerilogCompiler.SyntacticTree.VNode;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
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
        if (sensitiveList != null)
            sensitiveList.validateSemantics();
        statement.validateSemantics();
        SemanticCheck.getInstance().setInsideProceduralBlock(false);
        return null;
    }

    @Override
    public void executeModuleItem(SimulationScope simulationScope, String moduleInstanceId) {
        /*TODO*/
        if (sensitiveList == null || Convert.getBoolean(sensitiveList.evaluate(simulationScope, moduleInstanceId))) {
            statement.execute(simulationScope, moduleInstanceId);
        } else {
            System.out.println("DEBUG: " + "initial block avoided.");
        }
    }

    @Override
    public void initModuleItem(SimulationScope simulationScope, String moduleInstanceId) {
        if(sensitiveList != null)
            sensitiveList.clearState();
        if (sensitiveList == null || Convert.getBoolean(sensitiveList.evaluate(simulationScope, moduleInstanceId))) {
            statement.execute(simulationScope, moduleInstanceId);
        } else {
            System.out.println("DEBUG: " + "initial block avoided.");
        }
    }
    

    @Override
    public VNode getCopy() {
        SensitiveList listCopy = null;
        if (sensitiveList != null)
            listCopy = (SensitiveList)sensitiveList.getCopy();
        return new InitialBlock(listCopy, 
                (Statement)statement.getCopy(), line, column);
    }
}
