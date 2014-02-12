/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Watcher;

import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class ExpandCellRenderer extends JToggleButton implements TableCellRenderer {

    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof Boolean) {
            Boolean realValue = (Boolean) value;
            if (realValue) {
                this.setText("+");
                this.setFont(new Font("SanSerif", Font.PLAIN, 8));
                return this;
            }
        }
        return this;
        
    }
    
}
