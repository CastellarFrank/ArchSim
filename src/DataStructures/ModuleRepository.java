/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataStructures;

import Simulation.Elements.PortPosition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class ModuleRepository {

    private HashMap<String, ModuleInfo> modulesRepository;

    private ModuleRepository() {
        modulesRepository = new HashMap<String, ModuleInfo>();
    }

    public void registerModule(String moduleName, ModuleInfo info) {
        modulesRepository.put(moduleName, info);
    }

    public boolean moduleIsRegistered(String moduleName) {
        return modulesRepository.containsKey(moduleName);
    }

    public ModuleInfo getModuleInfo(String moduleName) {
        return modulesRepository.get(moduleName);
    }
    
    public Set<String> getModuleNames() {
        return modulesRepository.keySet();
    }
    
    public static ModuleRepository getInstance() {
        return ModuleRepositoryHolder.INSTANCE;
    }

    private static class ModuleRepositoryHolder {

        private static final ModuleRepository INSTANCE = new ModuleRepository();
    }
}
