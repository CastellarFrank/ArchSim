/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.ModuleItems;

import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SyntacticTree.GateType;
import VerilogCompiler.SyntacticTree.Others.GateInstance;
import VerilogCompiler.Utils.StringUtils;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class GateDecl extends ModuleItem {
    GateType gateType;
    ArrayList<GateInstance> gateInstanceList;

    public GateDecl(GateType gateType, ArrayList<GateInstance> gateInstanceList, int line, int column) {
        super(line, column);
        this.gateType = gateType;
        this.gateInstanceList = gateInstanceList;
    }

    public GateType getGateType() {
        return gateType;
    }

    public void setGateType(GateType gateType) {
        this.gateType = gateType;
    }

    public ArrayList<GateInstance> getGateInstanceList() {
        return gateInstanceList;
    }

    public void setGateInstanceList(ArrayList<GateInstance> gateInstanceList) {
        this.gateInstanceList = gateInstanceList;
    }

    @Override
    public String toString() {
        return String.format("%s %s;", 
                StringUtils.getInstance().GateTypeToString(gateType),
                StringUtils.getInstance().ListToString(gateInstanceList, ","));
    }

    @Override
    public ExpressionType validateSemantics() {
        for (GateInstance gateInstance : gateInstanceList) {
            gateInstance.validateSemantics();
        }
        return null;
    }
    
}
