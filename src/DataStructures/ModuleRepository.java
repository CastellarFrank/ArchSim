/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataStructures;

import Simulation.Configuration;
import VerilogCompiler.SyntacticTree.Declarations.ModuleDecl;
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
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class ModuleRepository {

    private HashMap<String, ModuleInfo> moduleInfos;
    private HashMap<String, ModuleDecl> modulesLogic;
    private HashMap<String, Element> designPrototypes;

    private ModuleRepository() {
        moduleInfos = new HashMap<String, ModuleInfo>();
        designPrototypes = new HashMap<String, Element>();
        modulesLogic = new HashMap<String, ModuleDecl>();
    }
    
    public void registerModuleLogic(String moduleName, ModuleDecl moduleDecl) {
        if (!modulesLogic.containsKey(moduleName))
            modulesLogic.put(moduleName, moduleDecl);
    }

    public void registerModule(String moduleName, ModuleInfo info) {
        moduleInfos.put(moduleName, info);
    }

    public boolean moduleIsRegistered(String moduleName) {
        return moduleInfos.containsKey(moduleName);
    }

    public ModuleInfo getModuleInfo(String moduleName) {
        return moduleInfos.get(moduleName);
    }
    
    public ModuleDecl getModuleLogic(String moduleName) {
        if (modulesLogic.containsKey(moduleName))
            return (ModuleDecl)modulesLogic.get(moduleName).getCopy();
        return null;
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
        return moduleInfos.keySet();
    }
    
    public static ModuleRepository getInstance() {
        return ModuleRepositoryHolder.INSTANCE;
    }

    private static class ModuleRepositoryHolder {

        private static final ModuleRepository INSTANCE = new ModuleRepository();
    }
}
