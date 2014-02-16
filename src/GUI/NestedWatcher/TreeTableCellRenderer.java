/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.NestedWatcher;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class TreeTableCellRenderer extends DefaultTreeCellRenderer {

    /**
     *
     */
    private static final long serialVersionUID = 5593629042737938947L;

    public TreeTableCellRenderer() {

        setOpenIcon(new ImageIcon("icons/minus.gif"));
        setClosedIcon(new ImageIcon("icons/plus.gif"));
    }

    @Override
    public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {

        super.getTreeCellRendererComponent(
                tree, value, sel,
                expanded, leaf, row,
                hasFocus);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

        if (node != null && node.getUserObject() != null && (node.getUserObject() instanceof TableRowData)) {
            TableRowData item = (TableRowData) (node.getUserObject());
            setText(item.getVariableName());
            if (item.isRoot()) {
                setOpenIcon(new ImageIcon("icons/minus.gif"));
                setClosedIcon(new ImageIcon("icons/plus.gif"));
            } else {
                setIcon(null);
            }
        } else {
            setIcon(null);
        }
        return this;

    }
}
