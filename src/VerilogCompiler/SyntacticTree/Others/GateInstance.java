/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Others;

import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SyntacticTree.Expressions.Expression;
import VerilogCompiler.SyntacticTree.VNode;
import VerilogCompiler.Utils.StringUtils;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class GateInstance extends VNode {
    GateInstanceName gateInstanceName;
    ArrayList<Expression> terminalList;

    public GateInstance(GateInstanceName gateInstanceName, ArrayList<Expression> terminalList, int line, int column) {
        super(line, column);
        this.gateInstanceName = gateInstanceName;
        this.terminalList = terminalList;
    }

    public GateInstanceName getGateInstanceName() {
        return gateInstanceName;
    }

    public void setGateInstanceName(GateInstanceName gateInstanceName) {
        this.gateInstanceName = gateInstanceName;
    }

    public ArrayList<Expression> getTerminalList() {
        return terminalList;
    }

    public void setTerminalList(ArrayList<Expression> terminalList) {
        this.terminalList = terminalList;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", 
                gateInstanceName==null?"":gateInstanceName.toString(), 
                StringUtils.getInstance().ListToString(terminalList, ","));
    }

    @Override
    public ExpressionType validateSemantics() {
        if (gateInstanceName != null)
            gateInstanceName.validateSemantics();
        for (Expression expression : terminalList) {
            expression.validateSemantics();
        }
        return null;
    }

    @Override
    public VNode getCopy() {
        GateInstanceName name = null;
        if (gateInstanceName != null)
            name = (GateInstanceName)gateInstanceName.getCopy();
        ArrayList<Expression> terminals = new ArrayList<Expression>();
        for (Expression expression : terminalList) {
            terminals.add((Expression)expression.getCopy());
        }
        
        return new GateInstance(name, terminals, line, column);
    }
    
}