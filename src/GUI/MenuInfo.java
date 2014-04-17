/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.util.ArrayList;

/**
 *
 * @author Nestor Bermudez
 */
public class MenuInfo {
    public boolean isMenuItem;
    public String label;
    public ArrayList<MenuInfo> children;
    
    public MenuInfo(String label, boolean isMenuItem) {
        this.label = label;
        this.isMenuItem = isMenuItem;
        children = new ArrayList<MenuInfo>();
    }
    
    public void addChild(MenuInfo child) {
        children.add(child);
    }
}
