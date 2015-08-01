/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.NestedWatcher;

import GUI.Simulation.SimulationCanvas;
import GUI.Watcher.WatchModelEntry;
import GUI.Watcher.WatchModelEntryDataLog;
import VerilogCompiler.Interpretation.SimulationScope;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.StringValue;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class Debugger extends javax.swing.JInternalFrame {

    SimulationCanvas simulationCanvas;
    SimulationScope simulationScope;
    JXTreeTable treeTable;
    CustomClocksChart customChart;
    CustomTreeModel currentDataModel;
    List<String> expansionStateIdentifier;

    /**
     * Creates new form Debugger
     * @param simWin
     */
    public Debugger(SimulationCanvas simWin) {
        initComponents();
        simulationCanvas = simWin;
        simulationScope = simWin.simulationScope;
        currentDataModel = simWin.debuggerModel;
        
        treeTable = new JXTreeTable(simWin.debuggerModel);
        this.customChart = new CustomClocksChart(this);
        simWin.clockEventManagement.setCustomClocksChart(this.customChart);
        Highlighter highligher = HighlighterFactory.createSimpleStriping(HighlighterFactory.LEDGER);
        treeTable.setHighlighters(highligher);
        treeTable.setAutoResizeMode(JXTreeTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        treeTable.setShowGrid(true);
        treeTable.setShowsRootHandles(true);
        configureCommonTableProperties(treeTable);
        treeTable.setTreeCellRenderer(new TreeTableCellRenderer());

        treeTable.setVisible(true);
        scroll.getViewport().add(treeTable);
        scroll1.getViewport().add(customChart.getCustomClocksChart());
        setVisible(true);
    }

    public void refresh() {
        simulationCanvas.parent.debuggerRefresh = false;
        if (treeTable != null) {
            if(currentDataModel != null){
                boolean needsRefresh = currentDataModel.needsRefresh();
                this.saveExpansionState();
                currentDataModel.refresh();
                if(needsRefresh){
                    treeTable.updateUI();
                    this.loadExpansionState();
                }
            }
        }
    }
    
    public JScrollPane getCustomChartScrollPane(){
        return this.scroll1;
    }

    private void configureCommonTableProperties(JXTable table) {
        table.setColumnControlVisible(true);
        StringValue toString = new StringValue() {
            @Override
            public String getString(Object value) {
                if (value instanceof Point) {
                    Point p = (Point) value;
                    return createString(p.x, p.y);
                } else if (value instanceof Dimension) {
                    Dimension dim = (Dimension) value;
                    return createString(dim.width, dim.height);
                }
                return "";
            }

            private String createString(int width, int height) {
                return "(" + width + ", " + height + ")";
            }
        };
        TableCellRenderer renderer = new DefaultTableRenderer(toString);
        table.setDefaultRenderer(Point.class, renderer);
        table.setDefaultRenderer(Dimension.class, renderer);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        deleteAll = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        scroll = new javax.swing.JScrollPane();
        scroll1 = new javax.swing.JScrollPane();
        ckbRealTime = new javax.swing.JCheckBox();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setMaximizable(true);
        setResizable(true);
        setTitle("Variable Watcher");

        deleteAll.setText("Delete All Variables");
        deleteAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteAllActionPerformed(evt);
            }
        });

        jSplitPane1.setDividerLocation(370);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setLeftComponent(scroll);
        jSplitPane1.setRightComponent(scroll1);

        ckbRealTime.setSelected(true);
        ckbRealTime.setText("Real Time Variables");
        ckbRealTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ckbRealTimeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(deleteAll)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 244, Short.MAX_VALUE)
                .addComponent(ckbRealTime)
                .addContainerGap())
            .addComponent(jSplitPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deleteAll)
                    .addComponent(ckbRealTime))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 606, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void deleteAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAllActionPerformed
        if(this.expansionStateIdentifier != null)
            this.expansionStateIdentifier.clear();
        ((CustomTreeModel) (treeTable.getTreeTableModel())).clear();
        treeTable.updateUI();
    }//GEN-LAST:event_deleteAllActionPerformed

    private void ckbRealTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ckbRealTimeActionPerformed
        if(this.ckbRealTime.isSelected())
        {
            this.currentDataModel.stopUsingLogs();
        }else{
            this.currentDataModel.startUsingLogs();
        }
        this.refresh();
    }//GEN-LAST:event_ckbRealTimeActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox ckbRealTime;
    private javax.swing.JButton deleteAll;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JScrollPane scroll1;
    // End of variables declaration//GEN-END:variables

    public void saveVariablesIfNeeded(ArrayList<WatchModelEntry> modelData) {
        this.customChart.saveVariablesIfNeeded(modelData);
    }

    void changeLogWatchEntries(List<WatchModelEntryDataLog> entries) {
        if(this.currentDataModel == null)
            return;
        this.ckbRealTime.setSelected(false);
        this.currentDataModel.changeCurrentLogEntries(entries, true);
        this.refresh();
    }
    
    public void saveExpansionState() {
        if(this.treeTable == null)
            return;
        this.expansionStateIdentifier = new ArrayList<String>();
        Enumeration<TreePath> elements = (Enumeration<TreePath>)this.treeTable.getExpandedDescendants(new TreePath(this.currentDataModel.getRoot()));
        if(elements == null)
            return;
        while(elements.hasMoreElements()){
            TreePath path = elements.nextElement();
            String nodeIdentifier = ((TableRowData)((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject()).getNodeIdentifier();
            if(!expansionStateIdentifier.contains(nodeIdentifier))
                expansionStateIdentifier.add(nodeIdentifier); 
        }
    }
    
     public void loadExpansionState() {
        if(this.expansionStateIdentifier == null || this.expansionStateIdentifier.isEmpty())
            return;
        
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)simulationCanvas.debuggerModel.getRoot();
        DefaultMutableTreeNode currentNode = root.getNextNode();
        if (currentNode != null) {
            do {
                String realIdentifier = ((TableRowData)currentNode.getUserObject()).getNodeIdentifier();
                if(this.expansionStateIdentifier.contains(realIdentifier))
                    treeTable.expandPath(new TreePath(currentNode.getPath()));
                
                currentNode = currentNode.getNextNode();
            } while (currentNode != null);
        }        
    }
}
