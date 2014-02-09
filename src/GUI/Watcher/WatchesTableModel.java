/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Watcher;

import VerilogCompiler.Interpretation.ExpressionValue;
import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.VariableInfo;
import java.util.ArrayList;
import javax.swing.JToggleButton;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * Class used to feed a JTable that shows variable watches.
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class WatchesTableModel implements TableModel {

    SimulationScope scope;
    
    public WatchesTableModel(SimulationScope simulationScope) {
        scope = simulationScope;
    }
    
    ArrayList<WatchModelEntry> modelData = new ArrayList<WatchModelEntry>();
    
    /**
     * Erases everything from this table.
     */
    public void clear() {
        modelData.clear();
    }
    
    /**
     * It registers a variable to be watched. 
     * @param variableName name of the variable to be watched.
     * @param moduleInstanceId module instance that contains the <code>variable</code> variable.
     */
    public void addWatch(String variableName, String moduleInstanceId) {
        if (contains(variableName, moduleInstanceId))
            return;
        
        modelData.add(new WatchModelEntry(variableName, moduleInstanceId));
        updateValues();        
    }
    
    public void removeWatch(String variableName, String moduleInstanceId) {
        for (int i = 0; i < modelData.size(); i++) {
            WatchModelEntry entry = modelData.get(i);
            if (entry.moduleInstanceId.equals(moduleInstanceId) &&
                    entry.variableName.equals(variableName)) {
                modelData.remove(i);
                break;
            }
        }
    }
    
    /**
     * It says if there is a <code>WatchModelEntry</code> being watch for the given parameters.
     * @param variableName name of the variable to be watched.
     * @param moduleInstanceId module instance that contains the <code>variable</code> variable.
     * @return
     */
    public boolean contains(String variableName, String moduleInstanceId) {
        for (WatchModelEntry watchModelEntry : modelData) {
            if (watchModelEntry.moduleInstanceId.equals(moduleInstanceId) &&
                    watchModelEntry.variableName.equals(variableName))
                return true;
        }
        return false;
    }
    
    /**
     * Refreshes every variable value from a <code>SimulationScope</code>
     */
    public void updateValues() {
        for (int i = 0; i < modelData.size(); i++) {
            WatchModelEntry watchModelEntry = modelData.get(i);
            
            ExpressionValue value = scope.getVariableValue(watchModelEntry.moduleInstanceId, watchModelEntry.variableName);
            if (value == null) {
                watchModelEntry.value = "<unavailable>";
                continue;
            }
            if (value.xValue)
                watchModelEntry.value = "x";
            else if (value.zValue)
                watchModelEntry.value = "z";
            else
                watchModelEntry.value = value.value.toString();
        }
    }
    
    /**
     * Given a <code>rowIndex</code> it returns a <code>ExpressionValue</code>
     * @param rowIndex number of the row.
     * @return <code>ExpressionValue</code> taken from a <code>SimulationScope</code>
     */
    public ExpressionValue getExpressionValue(int rowIndex) {
        WatchModelEntry entry = modelData.get(rowIndex);
        return scope.getVariableValue(entry.moduleInstanceId, entry.variableName);
    }
    
    /**
     * Given a <code>rowIndex</code> it returns a <code>VariableInfo</code>
     * @param rowIndex number of the row.
     * @return <code>VariableInfo</code> taken from a <code>SimulationScope</code>
     */
    public VariableInfo getVariableInfo(int rowIndex) {
        WatchModelEntry entry = modelData.get(rowIndex);
        return scope.getVariableInfo(entry.moduleInstanceId, entry.variableName);
    }
    
    @Override
    public int getRowCount() {
        return modelData.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columnIndex == 1)
            return "Variable Name";
        else if (columnIndex == 2)
            return "Value";
        else return "";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 1 || columnIndex == 2)
            return String.class;
        return JToggleButton.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == 0)
            return true;
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 1) {
            return modelData.get(rowIndex).variableName;
        } else if (columnIndex == 2){
            return modelData.get(rowIndex).value;
        } else {
            VariableInfo info = getVariableInfo(rowIndex);
            if (info != null && info.isArray)
                return Boolean.TRUE;
            return Boolean.FALSE;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        
    }
    
}
