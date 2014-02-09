/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Watcher.Selector;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class WatchesSelectorModelEntry {
    public Boolean beingWatched;
    public String variableName;

    public WatchesSelectorModelEntry(Boolean beingWatched, String variableName) {
        this.beingWatched = beingWatched;
        this.variableName = variableName;
    }
    
}
