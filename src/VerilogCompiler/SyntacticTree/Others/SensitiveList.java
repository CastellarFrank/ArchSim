/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Others;

import VerilogCompiler.Interpretation.ExpressionValue;
import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SyntacticTree.Expressions.EventExpression;
import VerilogCompiler.SyntacticTree.Expressions.Expression;
import VerilogCompiler.SyntacticTree.VNode;
import VerilogCompiler.Utils.StringUtils;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class SensitiveList extends Expression {

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

    @Override
    public ExpressionValue evaluate(SimulationScope simulationScope, String moduleName) {
        if (acceptAll) {
            return new ExpressionValue(1, 1);
        }
        int result = 1;
        for (EventExpression eventExpression : items) {
            ExpressionValue val = eventExpression.evaluate(simulationScope, moduleName);
            if (val.xValue || val.zValue) {
                result = 0;
            } else {
                Integer intValue = Integer.parseInt(val.value.toString());
                if (intValue != 1) {
                    result = 0;
                    break;
                }
            }
        }
        return new ExpressionValue(result, 1);
    }

    @Override
    public VNode getCopy() {
        ArrayList<EventExpression> events = new ArrayList<EventExpression>();
        for (EventExpression eventExpression : items) {
            events.add((EventExpression)eventExpression.getCopy());
        }
        return new SensitiveList(acceptAll, events, line, column);
    }
    
}
