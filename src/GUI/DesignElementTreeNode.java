/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Franklin
 */
public class DesignElementTreeNode extends DefaultMutableTreeNode {
    private final String classElement;
    private final String extraParam;
    
    public DesignElementTreeNode(String text, String classElement, String extraParam) {
        setUserObject(text);
        this.classElement = classElement;
        this.extraParam = extraParam;
    }

    public String getTransferResponse(){
        StringBuilder response = new StringBuilder(this.classElement);
        if(this.extraParam != null && !this.extraParam.isEmpty())
            response.append(",").append(this.extraParam);
        return response.toString();
    }
}
