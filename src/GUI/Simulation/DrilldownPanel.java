/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Simulation;

import DataStructures.ModuleInfo;
import DataStructures.ModuleRepository;
import GUI.ContainerPanel;
import Simulation.Elements.ModuleChip;
import VerilogCompiler.SyntacticTree.Declarations.ModuleDecl;
import VerilogCompiler.SyntacticTree.ModuleItems.ModuleInstantiation;
import VerilogCompiler.SyntacticTree.Others.ModuleInstance;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class DrilldownPanel extends ContainerPanel {
    ModuleInfo moduleInfo;

    public DrilldownPanel(ModuleInfo moduleInfo) {
        this.moduleInfo = moduleInfo;
        init();
        
        String moduleName = moduleInfo.getModuleName();
        ModuleDecl decl = ModuleRepository.getInstance().getModuleLogic(moduleName);
        
        ArrayList<ModuleInstantiation> insts = decl.getModuleInstances();
        for (ModuleInstantiation modInst : insts) {
            for (ModuleInstance instance : modInst.getModuleInstanceList()) {
                String inner = modInst.getIdentifier();
                ModuleInfo innerModuleInfo = ModuleRepository.getInstance().getModuleInfo(inner);
                ModuleChip chip = new ModuleChip(56, 40,
                        121, 73, new String[] { inner });
                addElement(chip);
            }
        }
    }    
    
}
