/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Others;

import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SemanticCheck.SemanticCheck;
import VerilogCompiler.SyntacticTree.Expressions.Expression;
import VerilogCompiler.SyntacticTree.VNode;
import VerilogCompiler.Utils.StringUtils;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class ModuleInstance extends VNode {
    String identifier;
    ArrayList<Expression> moduleConnectionList;

    public ModuleInstance(String identifier, ArrayList<Expression> moduleConnectionList, int line, int column) {
        super(line, column);
        this.identifier = identifier;
        this.moduleConnectionList = moduleConnectionList;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public ArrayList<Expression> getModuleConnectionList() {
        return moduleConnectionList;
    }

    public void setModuleConnectionList(ArrayList<Expression> moduleConnectionList) {
        this.moduleConnectionList = moduleConnectionList;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", identifier, 
                StringUtils.getInstance().ListToString(moduleConnectionList, ","));
    }

    @Override
    public ExpressionType validateSemantics() {
        if (SemanticCheck.getInstance().variableIsRegistered(identifier)) {
            ErrorHandler.getInstance().handleError(line, column, 
                    identifier + " is already defined");
            return ExpressionType.ERROR;
        }
        for (Expression expression : moduleConnectionList) {
            expression.validateSemantics();
        }
        return null;
    }

    @Override
    public VNode getCopy() {
        ArrayList<Expression> exps = new ArrayList<Expression>();
        for (Expression expression : moduleConnectionList) {
            exps.add((Expression)expression.getCopy());
        }
        return new ModuleInstance(identifier, exps, line, column);
    }
    
}
