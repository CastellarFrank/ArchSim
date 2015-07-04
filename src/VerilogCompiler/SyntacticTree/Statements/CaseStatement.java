/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Statements;

import VerilogCompiler.Interpretation.Convert;
import VerilogCompiler.Interpretation.ExpressionValue;
import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SemanticCheck.SemanticCheck;
import VerilogCompiler.SyntacticTree.CaseItems.CaseItem;
import VerilogCompiler.SyntacticTree.Expressions.Expression;
import VerilogCompiler.SyntacticTree.VNode;
import VerilogCompiler.Utils.StringUtils;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class CaseStatement extends Statement {
    Expression expression;
    ArrayList<CaseItem> caseItemList;

    public CaseStatement(Expression expression, ArrayList<CaseItem> caseItemList, int line, int column) {
        super(line, column);
        this.expression = expression;
        this.caseItemList = caseItemList;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public ArrayList<CaseItem> getCaseItemList() {
        return caseItemList;
    }

    public void setCaseItemList(ArrayList<CaseItem> caseItemList) {
        this.caseItemList = caseItemList;
    }

    @Override
    public String toString() {
        return String.format("case(%s) \n %s", expression, 
                StringUtils.getInstance().ListToString(caseItemList, "\n"));
    }

    @Override
    public ExpressionType validateSemantics() {
        SemanticCheck.getInstance().saveDefaultCaseItemFound();
        expression.validateSemantics();
        for (CaseItem caseItem : caseItemList) {
            caseItem.validateSemantics();
        }
        SemanticCheck.getInstance().popDefaultcaseItemFound();
        return null;
    }

    @Override
    public void execute(SimulationScope simulationScope, String moduleName) {
        ExpressionValue value = expression.evaluate(simulationScope, moduleName);
        for (CaseItem caseItem : caseItemList) {
            ArrayList<ExpressionValue> result = caseItem.getValue(simulationScope, moduleName);
            if (result == null) { //default
                caseItem.execute(simulationScope, moduleName);
                break;
            } else {
                for (ExpressionValue expressionValue : result) {
                    if (value == null) {
                        if (expressionValue != null &&
                                (expressionValue.xValue == value.xValue || 
                                expressionValue.zValue == value.zValue)) {
                            caseItem.execute(simulationScope, moduleName);
                            return;
                        }
                    } else {
                        if (expressionValue != null && 
                                (expressionValue.xValue && value.xValue ||
                                expressionValue.zValue && value.zValue ||
                                value.value != null && 
                                Convert.getInteger(expressionValue).compareTo(Convert.getInteger(value)) == 0)) {
                            caseItem.execute(simulationScope, moduleName);
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public VNode getCopy() {
        ArrayList<CaseItem> cases = new ArrayList<CaseItem>();
        for (CaseItem caseItem : caseItemList) {
            cases.add((CaseItem)caseItem.getCopy());
        }
        return new CaseStatement((Expression)expression.getCopy(), cases, line, column);
    }
    
}
