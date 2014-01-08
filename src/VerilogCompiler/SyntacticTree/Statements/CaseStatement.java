/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Statements;

import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SemanticCheck.SemanticCheck;
import VerilogCompiler.SyntacticTree.CaseItems.CaseItem;
import VerilogCompiler.SyntacticTree.Expressions.Expression;
import VerilogCompiler.Utils.StringUtils;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
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
    
}
