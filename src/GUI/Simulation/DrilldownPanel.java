/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Simulation;

import DataStructures.ModuleInfo;
import GUI.ContainerPanel;
import javax.swing.JLabel;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class DrilldownPanel extends ContainerPanel {
    ModuleInfo moduleInfo;

    public DrilldownPanel(ModuleInfo moduleInfo) {
        this.moduleInfo = moduleInfo;
        
        add(new JLabel(moduleInfo.getModuleName()));
    }    
    
}
