/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Watcher.Selector;

import GUI.Watcher.WatchesTableModel;
import java.util.ArrayList;
import java.util.Set;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class WatchesSelectorTableModel implements TableModel {

    ArrayList<WatchesSelectorModelEntry> data;

    public WatchesSelectorTableModel() {
        data = new ArrayList<WatchesSelectorModelEntry>();
    }
    
    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columnIndex == 0)
            return "";
        else
            return "Variable Name";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0)
            return Boolean.class;
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == 0)
            return true;
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0)
            return data.get(rowIndex).beingWatched;
        return data.get(rowIndex).variableName;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            data.get(rowIndex).beingWatched = (Boolean) aValue;
        }
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        
    }
    
    public static WatchesSelectorTableModel fromVariableSet(WatchesTableModel watches, 
            Set<String> variableNames, String moduleInstanceId) {
        WatchesSelectorTableModel model = new WatchesSelectorTableModel();
        for (String variableName : variableNames) {
            if (watches.contains(variableName, moduleInstanceId))
                model.data.add(new WatchesSelectorModelEntry(Boolean.TRUE, variableName));
            else
                model.data.add(new WatchesSelectorModelEntry(Boolean.FALSE, variableName));
        }
        return model;
    }
}
