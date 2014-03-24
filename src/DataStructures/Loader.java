/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataStructures;

import Simulation.Configuration;
import VerilogCompiler.CompilationHelper;
import VerilogCompiler.SyntacticTree.Declarations.ModuleDecl;
import VerilogCompiler.SyntacticTree.PortDirection;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Singleton Class used to load settings and modules on application starting process.
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class Loader {

    private Loader() {
    }

    /**
     * <code>Loader</code>'s main method. It's used to load settings and modules
     * in a single method call.
     */
    public void setup() {
        loadSettings();
        loadModules();
    }

    /**
     * Loads the settings configuration file into <code>Configuration</code> class.
     * So after this call, every configuration is available through <code>Configuration</code> class.
     */
    public void loadSettings() {
        //Cargará los settings de un xml y las pondrá en Configuration
        File generalSettings = new File(Configuration.CONFIGURATION_FILE_DIRECTORY_PATH
                + Configuration.CONFIG_FILE_NAME);
        if (!generalSettings.exists()) {
            return;
        }
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(generalSettings);

            doc.normalize();

            NodeList configs = doc.getElementsByTagName("config");

            for (int i = 0; i < configs.getLength(); i++) {
                Element config = (Element) configs.item(i);
                String value = config.getAttribute("value");
                String name = config.getAttribute("name");
                String type = config.getAttribute("type");
                try {
                    Field field = Configuration.class.getDeclaredField(name);
                    Object fieldValue = null;
                    if (type.equals(String.class.getSimpleName()))
                        fieldValue = value;
                    else if (type.equals(Integer.class.getSimpleName()))
                        fieldValue = Integer.parseInt(value);
                    else if (type.equals(Boolean.class.getSimpleName()))
                        fieldValue = Boolean.parseBoolean(value);
                    else if (type.equals(Double.class.getSimpleName()))
                        fieldValue = Double.parseDouble(value);
                    
                    field.set(null, fieldValue);
                } catch (NoSuchFieldException ex) {
                    System.err.println("No field called " + value + " in configuration (" 
                            + ex.getMessage() + ")");
                } catch (SecurityException ex) {
                    System.err.println(ex.getMessage());
                }

            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * Loads every module definition file found on <code>Configuration.MODULES_DIRECTORY_PATH</code>
     * into the <code>ModuleRepository</code> singleton class.
     */
    public void loadModules() {
        ModuleRepository.getInstance().clear();
        //Iterará un directorio y cargará la info de los modulos en ModuleRepository
        File directory = new File(Configuration.MODULES_DIRECTORY_PATH);
        List<File> modules = (List<File>) FileUtils.listFiles(directory, null, true);
        
        for (File file : modules) {
            ModuleInfo moduleInfo = new ModuleInfo();
            moduleInfo.valid = false;
            ModuleRepository.getInstance().registerModule(file.getName().replace(".xml", ""), moduleInfo);
        }
        
        for (File file : modules) {
            if (Configuration.DEBUG_MODE) {
                System.out.println("Loading module... " + file.getAbsolutePath());
            }            
            String source = getSourceCode(file.getName());
            ModuleDecl parsed = getModuleLogic(source);
            if (parsed != null) {
                ModuleInfo moduleInfo = getModuleInfo(file.getName());
                if (moduleInfo == null) continue;
                moduleInfo.setSource(source);
                
                if (!parsed.hasModuleInstances()) 
                    moduleInfo.setIsLeaf(true);
                
                if (moduleInfo != null) {
                    ModuleRepository.getInstance().unregisterModule(moduleInfo.getModuleName());
                    ModuleRepository.getInstance().registerModuleLogic(moduleInfo.getModuleName(), parsed);
                    ModuleRepository.getInstance().registerModule(moduleInfo.getModuleName(), moduleInfo);
                } else {
                    System.out.println("Module Info is null");
                }
            } else {
                System.out.println("ModuleDecl (" + file.getName() + ") is null. Possibly parse error");
            }
        }
    }
    
    /**
     * Given a file name of a module definition file it returns its source code.
     * @param xmlFileName name (extension included) of the XML file of a module definition.
     * @return Verilog source used at definition time. 
     */
    public String getSourceCode(String xmlFileName) {
        File moduleFile = new File(Configuration.MODULES_DIRECTORY_PATH + 
                "/" + xmlFileName);
        if (!moduleFile.exists())
            return null;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(moduleFile);

            doc.normalize();
            
            NodeList program = doc.getElementsByTagName("behaviour");
            if (program.getLength() != 1) {
                /*ERROR*/
                return null;
            }
            String source = ((Element)program.item(0)).getTextContent();
            return source;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
    
    /**
     * It parses and returns a memory representation of a Verilog source code.
     * @param source Verilog source code
     * @return a <code>ModuleDecl</code> instance result of parsing the source code.
     */
    public ModuleDecl getModuleLogic(String source) {
        try {
            return CompilationHelper.parseWithSemantics(source);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    /**
     * It parses a XML file named <code>xmlFileName</code> its metadata.
     * @param xmlFileName name (extension included) of the XML file of a module definition.
     * @return <code>ModuleInfo</code> instance
     */
    public ModuleInfo getModuleInfo(String xmlFileName) {
        File moduleInfoFile = new File(Configuration.MODULE_METADATA_DIRECTORY_PATH
                + "/" + xmlFileName);

        if (!moduleInfoFile.exists()) {
            return null;
        }

        String fileName = xmlFileName.replaceFirst(".xml", "");
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(moduleInfoFile);

            doc.normalize();

            NodeList names = doc.getElementsByTagName("name");
            if (names.getLength() == 0) {
                /*ERROR*/
                return null;
            }
            Element name = (Element) names.item(0);
            if (!name.getTextContent().equals(fileName)) {
                /*ERROR!*/
            } else {
                ModuleInfo info = new ModuleInfo();
                info.setModuleName(fileName);

                ArrayList<PortInfo> portInfos = new ArrayList<PortInfo>();
                NodeList ports = doc.getElementsByTagName("port");
                for (int portIndex = 0; portIndex < ports.getLength(); portIndex++) {
                    Element port = (Element) ports.item(portIndex);
                    String type = port.getAttribute("type");
                    String portName = port.getAttribute("name");
                    //int sideIndex = Integer.parseInt(port.getAttribute("index"));

                    PortDirection portDirection = PortDirection.valueOf(type);
                    portInfos.add(new PortInfo(portDirection, portName, 0));
                }
                info.setPortsInfo(portInfos);

                return info;
            }
            return null;
        } catch (Exception ex) {
        }
        /*Parseo del XML*/
        /*si el parseo sale bien retornar el objecto sino null*/
        return null;
    }

    /**
     * Static method to get the instance of a <code>Loader</code> singleton
     * @return the <code>Loader</code> singleton instance.
     */
    public static Loader getInstance() {
        return LoaderHolder.INSTANCE;
    }

    private static class LoaderHolder {

        private static final Loader INSTANCE = new Loader();
    }
}
