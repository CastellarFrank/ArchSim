/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.NestedWatcher;

import VerilogCompiler.Interpretation.ExpressionValue;
import VerilogCompiler.Interpretation.SimulationScope;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class TableRowData {

    private String variableName;
    private String value;
    private int index = -1;
    private String moduleInstanceId;
    private boolean isRoot;

    public TableRowData(SimulationScope scope, String variableName, String moduleInstanceId) {
        this.variableName = variableName;
        this.moduleInstanceId = moduleInstanceId;
        if (scope == null) {
            this.isRoot = true;
            this.value = "[...]";
        } else {
            this.isRoot = scope.getVariableInfo(moduleInstanceId, variableName).isArray;
            ExpressionValue info = scope.getVariableValue(moduleInstanceId, variableName);
            Object val = info == null ? null : info.getValueAsString();
            this.value = val == null ? "z" : scope.getFormattedValue(moduleInstanceId, variableName);
            try {
                Integer vale = Integer.parseInt(value, 2);
                //this.value = this.value + " (" + vale + ")";
            } catch (Exception e) {
            }
        }
    }

    public TableRowData(SimulationScope scope, String variableName, String moduleInstanceId, int index) {
        this.variableName = variableName;
        this.moduleInstanceId = moduleInstanceId;
        this.index = index;
        if (scope == null) {
            this.isRoot = true;
            this.value = "[...]";
        } else {
            this.isRoot = scope.getVariableInfo(moduleInstanceId, variableName).isArray;
            if (isRoot) {
                Object val = ((Object[]) scope.getVariableValue(moduleInstanceId, variableName).value)[index];
                this.value = val == null ? "z" : val.toString();
            } else {
                this.value = scope.getFormattedValue(moduleInstanceId, variableName);
            }
            try {
                Integer val = Integer.parseInt(value, 2);
                //this.value = this.value + " (" + val + ")";
            } catch (Exception e) {
            }
        }
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getModuleInstanceId() {
        return moduleInstanceId;
    }

    public void setModuleInstanceId(String moduleInstanceId) {
        this.moduleInstanceId = moduleInstanceId;
    }

    public String getVariableName() {
        if (index == -1) {
            return variableName;
        } else {
            return "[" + index + "]";
        }
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setIsRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }

    public void update(SimulationScope scope) {
        value = scope.getFormattedValue(moduleInstanceId, variableName);
        try {
            Integer val = Integer.parseInt(value, 2);
            //this.value = this.value + " (" + val + ")";
        } catch (Exception e) {
        }
    }
}
