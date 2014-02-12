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
 * A very important class that contains information about every module registered.
 * It contains:
 * <ul>
 *  <li>module metadata (<code>ModuleInfo</code> instances)</li>
 *  <li>module logic used for interpretation (<code>ModuleDecl</code> instances)</li>
 *  <li>module graphic prototype (<code>org.w3c.dom.Element</code> instances)</li>
 * </ul>
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
    
    /**
     * Registers a new <code>ModuleDecl</code> under the specified name if it doesn't already exists
     * @param moduleName name of the module
     * @param moduleDecl <code>ModuleDecl</code> instance result of parsing process
     */
    public void registerModuleLogic(String moduleName, ModuleDecl moduleDecl) {
        if (!modulesLogic.containsKey(moduleName))
            modulesLogic.put(moduleName, moduleDecl);
    }

    /**
     * Registers a new <code>ModuleInfo</code> under the specified name if it doesn't already exists
     * @param moduleName name of the module
     * @param info <code>ModuleInfo</code> instance (metadata)
     */
    public void registerModule(String moduleName, ModuleInfo info) {
        moduleInfos.put(moduleName, info);
    }

    /**
     * Tells if a given module is registered.
     * @param moduleName name of the module
     * @return <code>true</code> if <code>moduleName</code> is registered in this repository,
     * <code>false</code> otherwise.
     */
    public boolean moduleIsRegistered(String moduleName) {
        return moduleInfos.containsKey(moduleName);
    }

    /**
     * Returns a <code>ModuleInfo</code> given a module name.
     * @param moduleName name of the module
     * @return <code>ModuleInfo</code> under the name specified if 
     * <code>moduleName</code> is registered, <code>null</code> otherwise
     */
    public ModuleInfo getModuleInfo(String moduleName) {
        return moduleInfos.get(moduleName);
    }
    
    /**
     * Returns a <code>ModuleDecl</code> (module logic) given a module name.
     * @param moduleName name of the module
     * @return <code>ModuleDecl</code> under the name specified if 
     * <code>moduleName</code> is registered, <code>null</code> otherwise
     */
    public ModuleDecl getModuleLogic(String moduleName) {
        if (modulesLogic.containsKey(moduleName))
            return (ModuleDecl)modulesLogic.get(moduleName).getCopy();
        return null;
    }
    
    /**
     * Returns a module design prototype given a module name.
     * @param moduleName name of the module
     * @return <code>Element</code> under the name specified if 
     * <code>moduleName</code> is registered, <code>null</code> otherwise
     */
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
    
    /**
     * Returns a set that contains all names registered in this <code>ModuleRepository</code>
     * @return a set of names
     */
    public Set<String> getModuleNames() {
        return moduleInfos.keySet();
    }
    
    /**
     * Static method to get the instance of a <code>ModuleRepository</code> singleton
     * @return the <code>ModuleRepository</code> singleton instance.
     */
    public static ModuleRepository getInstance() {
        return ModuleRepositoryHolder.INSTANCE;
    }

    private static class ModuleRepositoryHolder {

        private static final ModuleRepository INSTANCE = new ModuleRepository();
    }
}
