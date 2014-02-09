/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.ModuleItems;

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
public class AlwaysBlock extends ModuleItem {
    SensitiveList sensitiveList;
    Statement statement;

    public AlwaysBlock(SensitiveList sensitiveList, Statement statement, int line, int column) {
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
        return String.format("always @(%s) \n%s", sensitiveList, statement);
    }

    @Override
    public ExpressionType validateSemantics() {
        SemanticCheck.getInstance().setInsideProceduralBlock(true);
        sensitiveList.validateSemantics();
        statement.validateSemantics();
        SemanticCheck.getInstance().setInsideProceduralBlock(false);
        return null;
    }
    
    public void execute(SimulationScope simulationScope, String moduleName) {
        ExpressionValue value = sensitiveList.evaluate(simulationScope, moduleName);
        Integer condition = Integer.parseInt(value.value.toString());
        
        if (condition == 1) {
            statement.execute(simulationScope, moduleName);
        }
    }

    @Override
    public void executeModuleItem(SimulationScope simulationScope, String moduleInstanceId) {
        super.executeModuleItem(simulationScope, moduleInstanceId);
        /*TODO*/
        execute(simulationScope, moduleInstanceId);
    }

    @Override
    public VNode getCopy() {
        return new AlwaysBlock((SensitiveList)sensitiveList.getCopy(), 
                (Statement)statement.getCopy(), line, column);
    }
    
}
