/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Watcher;

/**
 * Entry used for a <code>WatchesTableModel</code> to represent its data.
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class WatchModelEntry {
    public String variableName;
    public String value;
    public String moduleInstanceId;

    /**
     * Creates a new Model Entry that contains information used for a <code>WatchesTableModel</code>
     * @param variableName used to get the value from the <code>SimulationScope</code>
     * @param moduleInstanceId which identifies the module instance from any other.
     */
    public WatchModelEntry(String variableName, String moduleInstanceId) {
        this.variableName = variableName;
        this.moduleInstanceId = moduleInstanceId;
    }
}
