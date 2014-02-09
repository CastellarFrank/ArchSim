/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Statements;

import VerilogCompiler.Interpretation.ExpressionValue;
import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SyntacticTree.Expressions.Expression;
import VerilogCompiler.SyntacticTree.VNode;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class IfStatement extends Statement {
    Expression condition;
    Statement trueBlock;
    Statement falseBlock;

    public IfStatement(Expression condition, Statement trueBlock, Statement falseBlock, int line, int column) {
        super(line, column);
        this.condition = condition;
        this.trueBlock = trueBlock;
        this.falseBlock = falseBlock;
    }

    public Expression getCondition() {
        return condition;
    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    public Statement getTrueBlock() {
        return trueBlock;
    }

    public void setTrueBlock(Statement trueBlock) {
        this.trueBlock = trueBlock;
    }

    public Statement getFalseBlock() {
        return falseBlock;
    }

    public void setFalseBlock(Statement falseBlock) {
        this.falseBlock = falseBlock;
    }

    @Override
    public String toString() {
        return String.format("if (%s) %s %s", condition, trueBlock, 
                falseBlock != null? " else " + falseBlock:"");
    }

    @Override
    public ExpressionType validateSemantics() {
        condition.validateSemantics();
        trueBlock.validateSemantics();
        if (falseBlock != null)
            falseBlock.validateSemantics();
        
        return null;
    }

    @Override
    public void execute(SimulationScope simulationScope, String moduleName) {
        ExpressionValue value = condition.evaluate(simulationScope, moduleName);
        Integer intValue = Integer.parseInt(value.value.toString());
        if (intValue == 1)
            trueBlock.execute(simulationScope, moduleName);
        else if (falseBlock != null)
            falseBlock.execute(simulationScope, moduleName);
    }

    @Override
    public VNode getCopy() {
        return new IfStatement((Expression)condition.getCopy(), (Statement)trueBlock.getCopy(), 
                falseBlock != null ? (Statement)falseBlock.getCopy(): null, line, column);
    }
    
}
