/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.CaseItems;

import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SyntacticTree.Expressions.Expression;
import VerilogCompiler.SyntacticTree.Statements.Statement;
import VerilogCompiler.Utils.StringUtils;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class NumCaseItem extends CaseItem {
    ArrayList<Expression> expressionList;
    Statement statement;

    public NumCaseItem(ArrayList<Expression> expressionList, Statement statement, int line, int column) {
        super(line, column);
        this.expressionList = expressionList;
        this.statement = statement;
    }

    public ArrayList<Expression> getExpressionList() {
        return expressionList;
    }

    public void setExpressionList(ArrayList<Expression> expressionList) {
        this.expressionList = expressionList;
    }

    public Statement getStatement() {
        return statement;
    }

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", StringUtils.getInstance().ListToString(expressionList, ","), statement);
    }

    @Override
    public ExpressionType validateSemantics() {
        for (Expression expression : expressionList) {
            ExpressionType ret = expression.validateSemantics();
            if (ret == ExpressionType.ERROR || ret == ExpressionType.INTEGER)
                continue;
        }
        statement.validateSemantics();
        return null;
    }

    @Override
    public void execute(SimulationScope simulationScope, String moduleName) {
        statement.execute(simulationScope, moduleName);
    }

    @Override
    public ArrayList<Integer> getValue(SimulationScope scope, String moduleName) {
        ArrayList<Integer> values = new ArrayList<Integer>();
        for (Expression expression : expressionList) {
            values.add(Integer.parseInt(expression.evaluate(scope, moduleName).value.toString()));
        }
        return values;
    }
    
}
