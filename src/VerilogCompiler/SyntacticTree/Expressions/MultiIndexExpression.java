/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Expressions;

import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SemanticCheck.SemanticCheck;
import VerilogCompiler.SyntacticTree.Range;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class MultiIndexExpression extends PrimaryExpression {
    String identifier;
    ArrayList<Expression> indexes;
    Range range;

    public MultiIndexExpression(String identifier, ArrayList<Expression> indexes, Range range, int line, int column) {
        super(line, column);
        this.identifier = identifier;
        this.indexes = indexes;
        this.range = range;
    }

    @Override
    public String toString() {
        String stringIndexes = "";
        for (Expression expression : indexes) {
            stringIndexes += "[" + expression + "]";
        }
        return String.format("%s %s %s", identifier, stringIndexes, range == null?"":range);
    }

    @Override
    public ExpressionType validateSemantics() {
        if (indexes.size() > 1)
            return ExpressionType.ERROR;
        if (!SemanticCheck.getInstance().variableIsRegistered(identifier)) {
            ErrorHandler.getInstance().handleError(line, column, 
                    identifier + " is not declared");
            return ExpressionType.ERROR;
        } else if (!SemanticCheck.getInstance().variableIsVector(identifier) && range != null) {
            ErrorHandler.getInstance().handleError(line, column, 
                    identifier + " is not a vector");
            return ExpressionType.ERROR;
        }
        if (indexes.size() == 1 && !SemanticCheck.getInstance().variableIsArrayOrVector(identifier)) {
             ErrorHandler.getInstance().handleError(line, column, 
                    identifier + " is not a vector/array");
            return ExpressionType.ERROR;
        }
        if (indexes.size() == 1 && range != null && 
                SemanticCheck.getInstance().variableIsArray(identifier) &&
                SemanticCheck.getInstance().variableIsVector(identifier))
            return ExpressionType.INTEGER;
        return ExpressionType.VECTOR;
    }
    
}
