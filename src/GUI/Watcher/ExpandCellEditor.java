/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Watcher;

import java.awt.Component;
import java.awt.Font;
import java.util.EventObject;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class ExpandCellEditor extends JToggleButton implements TableCellEditor {

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (value instanceof Boolean) {
            if (this.isSelected())
                this.setText("+");
            else
                this.setText("-");
            this.addChangeListener(new MyChangeListener());
            this.setFont(new Font("SanSerif", Font.PLAIN, 8));
            return this;
        } 
        return null;
    }

    @Override
    public Object getCellEditorValue() {
        return this.isSelected();
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return true;
    }

    @Override
    public boolean stopCellEditing() {
        return true;
    }

    @Override
    public void cancelCellEditing() {
        throw new UnsupportedOperationException("Not supported yet. 6");
    }

    @Override
    public void addCellEditorListener(CellEditorListener l) {
        
    }

    @Override
    public void removeCellEditorListener(CellEditorListener l) {
        
    }
    
    class MyChangeListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            JToggleButton button = (JToggleButton) e.getSource();
            if (button.isSelected())
                button.setText("-");
            else 
                button.setText("+");
        }
    
    }
    
}
