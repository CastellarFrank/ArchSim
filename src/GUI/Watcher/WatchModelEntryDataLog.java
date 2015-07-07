/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Watcher;

/**
 *
 * @author Franklin
 */
public class WatchModelEntryDataLog {
    public String variableName;
    public Object value;
    public String userNameReference;
    public boolean isArray;

    public WatchModelEntryDataLog(String variableName, Object value, String userNameReference, boolean isArray) {
        this.variableName = variableName;
        this.value = value;
        this.userNameReference = userNameReference;
        this.isArray = isArray;
    }
}
