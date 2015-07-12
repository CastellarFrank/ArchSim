/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Watcher;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Franklin
 */
public class WatchModelEntryDataLog {
    public String variableName;
    public String value;
    public String userNameReference;
    public boolean isArray;
    public List<String> variableValues;

    public WatchModelEntryDataLog(String variableName, String value, String userNameReference) {
        this.variableName = variableName;
        this.value = value;
        this.variableValues = new ArrayList<String>();
        this.userNameReference = userNameReference;
        this.isArray = false;
    }
    
    public WatchModelEntryDataLog(String variableName, List<String> values, String userNameReference){
        this.variableName = variableName;
        this.variableValues = values;
        this.userNameReference = userNameReference;
        this.isArray = true;
    }
}
