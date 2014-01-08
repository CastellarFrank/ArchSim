/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Statements;

import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SyntacticTree.Expressions.Expression;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class WaitStatement extends Statement {
    Expression condition;
    Statement statement;

    public WaitStatement(Expression condition, Statement statement, int line, int column) {
        super(line, column);
        this.condition = condition;
        this.statement = statement;
    }

    public Expression getCondition() {
        return condition;
    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    public Statement getStatement() {
        return statement;
    }

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    @Override
    public String toString() {
        return String.format("wait (%s) %s", condition, 
                statement==null?"":statement);
    }

    @Override
    public ExpressionType validateSemantics() {
        condition.validateSemantics();
        if (statement != null)
            statement.validateSemantics();
        
        return null;
    }
    
}
