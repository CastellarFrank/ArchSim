/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.NestedWatcher;

import GUI.Watcher.WatchModelEntry;
import GUI.Watcher.WatchModelEntryDataLog;
import Simulation.Elements.ModuleChip;
import VerilogCompiler.Interpretation.ExpressionValue;
import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.VariableInfo;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class CustomTreeModel extends AbstractTreeTableModel {

    private String[] titles = {"Variable", "Binary Value", "Decimal Value", "Hex Value"};
    private ArrayList<WatchModelEntry> variables;
    private List<WatchModelEntryDataLog> variableLogs;
    private SimulationScope scope;
    private boolean useLogs = false;
    private boolean logsLoaded = false;

    public CustomTreeModel(DefaultMutableTreeNode root) {
        super(root);        
    }
    
    public CustomTreeModel(SimulationScope scope, ArrayList<WatchModelEntry> variables) {
        this.variables = variables;
        this.scope = scope;
        root = generateRoot();
    }
    
    public final DefaultMutableTreeNode generateRoot() {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new TableRowData(), true);
        ArrayList<WatchModelEntry> toDelete = new ArrayList<WatchModelEntry>();

        HashMap<String, DefaultMutableTreeNode> categories = new HashMap<String, DefaultMutableTreeNode>();
        
        for (WatchModelEntry watchModelEntry : variables) {
            ModuleChip chip = watchModelEntry.chip;
            if (chip != null) {
                if (categories.containsKey(chip.getUserReference())) continue;
                String userReference = chip.getUserReference();
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(new TableRowData(userReference));
                categories.put(chip.getUserReference(), node);
            }
        }
        
        for (WatchModelEntry watchModelEntry : variables) {
            String moduleId = watchModelEntry.moduleInstanceId;
            String variableName = watchModelEntry.variableName;
            ModuleChip chip = watchModelEntry.chip;
            
            String userReference = chip.getUserReference();
            DefaultMutableTreeNode parent = categories.get(userReference);
            
            VariableInfo info = scope.getVariableInfo(moduleId, variableName);
            if (info == null)
                toDelete.add(watchModelEntry);
            else{
                String parentNameIdentifier = userReference + "_" + variableName;
                if (info.isArray) {
                    TableRowData rowData = new TableRowData(variableName, true);
                    rowData.setNodeIdentifier(parentNameIdentifier);
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(rowData);
                    Object[] values = (Object[]) scope.getVariableValue(moduleId, variableName).value;
                    for (int i = 0; i < values.length; i++) {
                        Object val = values[i];
                        if (val != null && !val.toString().matches("[xXzZ]"))
                            val = scope.padWithZeros(val, info.signalSize);
                        String stringValue = val == null ? "z" : val.toString();
                        TableRowData childRowData = new TableRowData(variableName, stringValue, info.isArray, i);
                        childRowData.setNodeIdentifier(parentNameIdentifier + "_" + variableName);
                        node.add(new DefaultMutableTreeNode(childRowData, false));
                    }
                    parent.add(node);
                } else {
                    ExpressionValue infoValue = scope.getVariableValue(moduleId, variableName);
                    Object val = infoValue == null ? null : infoValue.getValueAsString();
                    String stringValue = val == null ? "z" : scope.getFormattedValue(moduleId, variableName);
                    TableRowData rowData = new TableRowData(variableName, stringValue, info.isArray);
                    rowData.setNodeIdentifier(parentNameIdentifier);
                    parent.add(new DefaultMutableTreeNode(rowData));
                }
            }
        }
        
        for (DefaultMutableTreeNode node : categories.values()) {
            rootNode.add(node);
        }
        
        for (WatchModelEntry watchModelEntry : toDelete) {
            variables.remove(watchModelEntry);
        }
        if (!toDelete.isEmpty())
            return generateRoot();
        return rootNode;
    }
    
    public void refresh() {
        if(!this.useLogs){
            this.root = generateRoot();
            return;
        }        
        
        if(this.logsLoaded)
            return;
        
        this.logsLoaded = true;
        this.root = generateLogsRoot();
    }
    
    
    public void clear() {
        variables.clear();
        refresh();
    }

    @Override
    public int getColumnCount() {
        return titles.length;
    }

    @Override
    public String getColumnName(int column) {
        if (column < titles.length) {
            return (String) titles[column];
        } else {
            return "";
        }
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return String.class;
    }

    @Override
    public Object getValueAt(Object arg0, int arg1) {
        if (arg0 instanceof TableRowData) {
            TableRowData data = (TableRowData) arg0;
            if (data != null) {
                if (!data.isCategory()) {
                    switch (arg1) {
                        case 0:
                            return data.getVariableName();
                        case 1:
                            return data.getValue();
                        case 2:
                            try {
                                BigInteger d = new BigInteger(data.getValue(), 2);
                                return d;
                            } catch (Exception e) {
                                return "";
                            }
                        case 3:
                            try {
                                BigInteger d = new BigInteger(data.getValue(), 2);
                                return d.toString(16);
                            } catch (Exception e) {
                                return "";
                            }
                    }
                } else {
                    if (arg1 == 0)
                        return data.getLabel();
                }
            }

        }

        if (arg0 instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) arg0;
            TableRowData data = (TableRowData) dataNode.getUserObject();
            if (data != null) {
                if (!data.isCategory()) {
                    switch (arg1) {
                        case 0:
                            return data.getVariableName();
                        case 1:
                            return data.getValue();
                        case 2:
                            try {
                                BigInteger d = new BigInteger(data.getValue(), 2);
                                return d;
                            } catch (Exception e) {
                                return "";
                            }
                        case 3:
                            try {
                                BigInteger d = new BigInteger(data.getValue(), 2);
                                return d.toString(16);
                            } catch (Exception e) {
                                return "";
                            }
                    }
                } else {
                    if (arg1 == 0)
                        return data.getLabel();
                }
            }

        }
        return null;
    }

    @Override
    public Object getChild(Object arg0, int index) {
        if (arg0 instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode nodes = (DefaultMutableTreeNode) arg0;
            return nodes.getChildAt(index);
        }
        return null;
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode nodes = (DefaultMutableTreeNode) parent;
            return nodes.getChildCount();
        }
        return 0;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return 0;
    }

    @Override
    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }

    void changeCurrentLogEntries(List<WatchModelEntryDataLog> entries, boolean useLogs) {
        this.variableLogs = entries;
        this.useLogs = useLogs;
        this.logsLoaded = false;
    }

    private DefaultMutableTreeNode generateLogsRoot() {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new TableRowData(), true);
        
        HashMap<String, DefaultMutableTreeNode> categories = new HashMap<String, DefaultMutableTreeNode>();
        if(this.variableLogs == null)
            return rootNode;
        for (WatchModelEntryDataLog watchModelEntry : this.variableLogs) {
            if (categories.containsKey(watchModelEntry.userNameReference)) continue;
            String userReference = watchModelEntry.userNameReference;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(new TableRowData(userReference));
            categories.put(userReference, node);
        }
        
        for (WatchModelEntryDataLog watchModelEntry : this.variableLogs) {
            String variableName = watchModelEntry.variableName;
            String userReference = watchModelEntry.userNameReference;
            DefaultMutableTreeNode parent = categories.get(userReference);
            String parentNameIdentifier = userReference + "_" + variableName;
            if (watchModelEntry.isArray) {
                TableRowData rowData = new TableRowData(variableName, true);
                rowData.setNodeIdentifier(parentNameIdentifier);
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(rowData);
                List<String> values  = watchModelEntry.variableValues;
                for (int i = 0; i < values.size(); i++) {
                    String stringValue = values.get(i);
                    TableRowData childRowData = new TableRowData(variableName, stringValue, watchModelEntry.isArray, i);
                    childRowData.setNodeIdentifier(parentNameIdentifier + "_" + variableName);
                    node.add(new DefaultMutableTreeNode(childRowData, false));
                }
                parent.add(node);
            } else {
                String stringValue = watchModelEntry.value;
                TableRowData rowData = new TableRowData(variableName, stringValue, watchModelEntry.isArray);
                rowData.setNodeIdentifier(parentNameIdentifier);
                parent.add(new DefaultMutableTreeNode(rowData));
            }
        }
        
        for (DefaultMutableTreeNode node : categories.values()) {
            rootNode.add(node);
        }
        return rootNode;
    }
    
    public boolean needsRefresh(){
        return !this.useLogs || !this.logsLoaded;
    }

    void stopUsingLogs() {
        this.useLogs = false;
    }

    void startUsingLogs() {
        this.useLogs = true;
    }
}
