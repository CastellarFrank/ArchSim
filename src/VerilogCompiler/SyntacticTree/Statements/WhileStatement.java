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
public class WhileStatement extends Statement {
    Expression condition;
    Statement body;

    public WhileStatement(Expression condition, Statement body, int line, int column) {
        super(line, column);
        this.condition = condition;
        this.body = body;
    }

    public Expression getCondition() {
        return condition;
    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    public Statement getBody() {
        return body;
    }

    public void setBody(Statement body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return String.format("while (%s) \n %s", condition, body);
    }

    @Override
    public ExpressionType validateSemantics() {
        condition.validateSemantics();
        body.validateSemantics();
        
        return null;
    }
    
}
