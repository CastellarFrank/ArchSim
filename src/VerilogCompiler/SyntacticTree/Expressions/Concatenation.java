/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Expressions;

import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.Utils.StringUtils;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class Concatenation extends LValue {
    ArrayList<Expression> expressionList;

    public Concatenation(ArrayList<Expression> expressionList, int line, int column) {
        super(line, column);
        this.expressionList = expressionList;
    }

    public ArrayList<Expression> getExpressionList() {
        return expressionList;
    }

    public void setExpressionList(ArrayList<Expression> expressionList) {
        this.expressionList = expressionList;
    }

    @Override
    public String toString() {
        return String.format("{%s}", 
                StringUtils.getInstance().ListToString(expressionList, ", "));
    }

    @Override
    public ExpressionType validateSemantics() {
        for (Expression expression : expressionList) {
            if (expression.validateSemantics() == ExpressionType.ERROR) {
                ErrorHandler.getInstance().handleError(line, column, 
                        "concatenation elements must be array or numeric");
            }
        }
        return ExpressionType.ARRAY;
    }
    
}
