/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Others;

import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SyntacticTree.Expressions.EventExpression;
import VerilogCompiler.SyntacticTree.VNode;
import VerilogCompiler.Utils.StringUtils;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class SensitiveList extends VNode {

    boolean acceptAll;
    ArrayList<EventExpression> items;

    public SensitiveList(int line, int column) {
        super(line, column);
        items = new ArrayList<EventExpression>();
    }
    
    public SensitiveList(ArrayList<EventExpression> items, int line, int column) {
        super(line, column);
        this.items = items;
    }

    public SensitiveList(boolean acceptAll, ArrayList<EventExpression> items, int line, int column) {
        super(line, column);
        this.acceptAll = acceptAll;
        this.items = items;
    }    
    
    public void addSensitiveItem(EventExpression item) {
        items.add(item);
    }

    public boolean isAcceptAll() {
        return acceptAll;
    }

    public void setAcceptAll(boolean acceptAll) {
        this.acceptAll = acceptAll;
    }

    public ArrayList<EventExpression> getItems() {
        return items;
    }

    public void setItems(ArrayList<EventExpression> items) {
        this.items = items;
    }    
    
    @Override
    public String toString() {
        if (acceptAll)
            return "*";
        else {
            return StringUtils.getInstance().ListToString(items, ",");
        }
    }

    @Override
    public ExpressionType validateSemantics() {
        for (EventExpression eventExpression : items) {
            eventExpression.validateSemantics();
        }
        return null;
    }
    
}
