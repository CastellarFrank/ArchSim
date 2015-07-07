/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.NestedWatcher;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class TableRowData {

    private String variableName;
    private String stringValue;
    private int index = -1;
    private boolean isRoot;
    private boolean isCategory;
    
    public TableRowData(){
        this.isCategory = false;
        this.isRoot = true;
        this.stringValue = "[...]";
    }
    
    public TableRowData(String label) {
        this.variableName = label;
        this.isCategory = true;
    }
    
    public TableRowData(String variableName, boolean isArrayHeader){
         this.variableName = variableName;
        this.isCategory = false;
        this.isRoot = true;
        this.stringValue = "[...]";
    }

    public TableRowData(String variableName, String value, boolean isArray) {
        this.variableName = variableName;
        this.isCategory = false;
        this.isRoot = isArray;
        this.stringValue = value;
    }

    public TableRowData(String variableName, String value, boolean isArray, int index) {
        this(variableName, value, isArray);
        this.index = index;
    }

    public String getVariableName() {
        if (index == -1) {
            return /*(chip != null ? chip.getUserReference() + "." : "") + */variableName;
        } else {
            return "[" + index + "]";
        }
    }
    
    public String getValue() {
        return stringValue;
    }

    public void setValue(String value) {
        this.stringValue = value;
    }
    
    public boolean isCategory() {
        return isCategory;
    }
    
    public String getLabel() {
        return variableName;
    }

    public boolean isRoot() {
        return isRoot;
    }
}
