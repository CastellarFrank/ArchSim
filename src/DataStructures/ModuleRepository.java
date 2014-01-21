/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataStructures;

import Simulation.Configuration;
import java.io.File;
import java.util.HashMap;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class ModuleRepository {

    private HashMap<String, ModuleInfo> modulesRepository;
    private HashMap<String, Element> designPrototypes;

    private ModuleRepository() {
        modulesRepository = new HashMap<String, ModuleInfo>();
        designPrototypes = new HashMap<String, Element>();
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
    
    public Element getDesignPrototype(String moduleName) {
        if (designPrototypes.containsKey(moduleName))
            return designPrototypes.get(moduleName);
        
        File target = new File(Configuration.MODULES_DIRECTORY_PATH + "/" + moduleName + ".xml");
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(target);
            
            doc.normalize();
            NodeList elements = doc.getElementsByTagName("element");
            Element first = (Element) (elements.item(0));
            designPrototypes.put(moduleName, first);
            
            return first;
        } catch (Exception ex) {
            return null;
        }
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
