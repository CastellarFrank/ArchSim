/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.NestedWatcher;

import GUI.Watcher.WatchModelEntry;
import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.VariableInfo;
import java.util.ArrayList;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class CustomTreeModel extends AbstractTreeTableModel {

    private String[] titles = {"Variable", "Value"};
    private ArrayList<WatchModelEntry> variables;
    private SimulationScope scope;

    public CustomTreeModel(DefaultMutableTreeNode root) {
        super(root);        
    }
    
    public CustomTreeModel(SimulationScope scope, ArrayList<WatchModelEntry> variables) {
        this.variables = variables;
        this.scope = scope;
        root = generateRoot();
    }
    
    public final DefaultMutableTreeNode generateRoot() {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new TableRowData(null, null, null), true);
        for (WatchModelEntry watchModelEntry : variables) {
            String moduleId = watchModelEntry.moduleInstanceId;
            String variableName = watchModelEntry.variableName;
            VariableInfo info = scope.getVariableInfo(moduleId, variableName);
            if (info.isArray) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(new TableRowData(null, variableName, moduleId));
                Object[] values = (Object[]) scope.getVariableValue(moduleId, variableName).value;
                for (int i = 0; i < values.length; i++) {
                    node.add(new DefaultMutableTreeNode(new TableRowData(scope, variableName, moduleId, i), false));
                }
                rootNode.add(node);
            } else {
                rootNode.add(new DefaultMutableTreeNode(new TableRowData(scope, variableName, moduleId)));
            }
        }
        return rootNode;
    }
    
    public void refresh() {
        root = generateRoot();
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
                switch (arg1) {
                    case 0:
                        return data.getVariableName();
                    case 1:
                        return data.getValue();
                }
            }

        }

        if (arg0 instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) arg0;
            TableRowData data = (TableRowData) dataNode.getUserObject();
            if (data != null) {
                switch (arg1) {
                    case 0:
                        return data.getVariableName();
                    case 1:
                        return data.getValue();
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
    
    
}
