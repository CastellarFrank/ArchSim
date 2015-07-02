/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataStructures;

import GUI.Design.DesignWindow;
import GUI.MenuInfo;
import Simulation.Configuration;
import Utils.TextUtils;
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
 * Singleton Class used to load settings and modules on application starting
 * process.
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class Loader {
    private final List<String> loadingModuleLogsList;
    private final List<ModuleLoadingError> moduleLoadingErrorList;

    private Loader() {
        this.loadingModuleLogsList = new ArrayList<String>();
        this.moduleLoadingErrorList = new ArrayList<ModuleLoadingError>();
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
     * Loads the settings configuration file into
     * <code>Configuration</code> class. So after this call, every configuration
     * is available through
     * <code>Configuration</code> class.
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
                    if (type.equals(String.class.getSimpleName())) {
                        fieldValue = value;
                    } else if (type.equals(Integer.class.getSimpleName())) {
                        fieldValue = Integer.parseInt(value);
                    } else if (type.equals(Boolean.class.getSimpleName())) {
                        fieldValue = Boolean.parseBoolean(value);
                    } else if (type.equals(Double.class.getSimpleName())) {
                        fieldValue = Double.parseDouble(value);
                    }

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
     * Loads every module definition file found on
     * <code>Configuration.MODULES_DIRECTORY_PATH</code> into the
     * <code>ModuleRepository</code> singleton class.
     */
    public void loadModules() {
        this.moduleLoadingErrorList.clear();
        ModuleRepository.getInstance().clear();
        //Iterará un directorio y cargará la info de los modulos en ModuleRepository
        File directory = new File(Configuration.MODULES_DIRECTORY_PATH);
        
        if (!directory.exists()) {
            Configuration.MODULES_DIRECTORY_PATH = "modules/";
            directory = new File(Configuration.MODULES_DIRECTORY_PATH);
            if (!directory.exists())
                directory.mkdir();
        }
        
        //List<File> modules = (List<File>) FileUtils.listFiles(directory, null, false);

        File[] modules = directory.listFiles();
        ArrayList<MenuInfo> menus = new ArrayList<MenuInfo>();

        for (File file : modules) {
            if (file.isFile()) {
                this.loadingModuleLogsList.clear();
                MenuInfo info = checkPossibleModule(file);
                if (info == null) {
                    this.moduleLoadingErrorList.add(new ModuleLoadingError(file.getName(), loadingModuleLogsList));
                }else{
                    menus.add(info);
                }
            } else {
                String folder = file.getName();
                MenuInfo dir = new MenuInfo(folder, false);

                List<File> project = (List<File>) FileUtils.listFiles(file, null, true);

                for (File projectModule : project) {
                    if(!projectModule.isFile())
                        continue;
                    
                    this.loadingModuleLogsList.clear();
                    MenuInfo info = checkPossibleModule(projectModule);
                    if (info == null) {
                        this.moduleLoadingErrorList.add(new ModuleLoadingError(projectModule.getName(), loadingModuleLogsList));
                    }else{
                        dir.addChild(info);
                    }
                }
                
                menus.add(dir);
            }
        }
        
        ModuleRepository.getInstance().setMenuData(menus);
        ModuleRepository.getInstance().notifySimulationWindows();
    }
    
    public List<ModuleLoadingError> getLoadingErrors(){
        return this.moduleLoadingErrorList;
    }

    private MenuInfo checkPossibleModule(File file) {
        if (Configuration.DEBUG_MODE)
            System.out.println("Loading module... " + file.getAbsolutePath());
        
        this.loadingModuleLogsList.add("Getting source code from file: [" + file.getAbsolutePath() +"].");
        String source = getSourceCode(file);
        this.loadingModuleLogsList.add("Parsing module Source code.");
        ModuleDecl parsed = getModuleLogic(source);
        if (parsed != null) {
            
            //Before checking if MetaDataFile exists, the module would be re-compiled.
            if(!DesignWindow.saveModuleMetadata(parsed)){
                this.loadingModuleLogsList.add("Couldn't create a MetadataFile for module: [" + file.getName() +"].");
                return null;
            }
            
            String moduleName = getModuleName(file);
            ModuleInfo moduleInfo = getModuleInfo(moduleName);
            if (moduleInfo == null) {
                return null;
            }
            moduleInfo.setSource(source);

            if (!parsed.hasModuleInstances()) {
                moduleInfo.setIsLeaf(true);
            }
            
            ModuleRepository.registeringModuleLock.writeLock().lock();
            try{
                ModuleRepository.getInstance().unregisterModule(moduleInfo.getModuleName());
                ModuleRepository.getInstance().registerModuleLogic(moduleInfo.getModuleName(), parsed);
                ModuleRepository.getInstance().registerModule(moduleInfo.getModuleName(), moduleInfo);
                ModuleRepository.getInstance().addDesignPrototype(moduleInfo.getModuleName(), file);
            }finally{
                ModuleRepository.registeringModuleLock.writeLock().unlock();
            }
            
            ModuleRepository.getInstance().updateModuleChips(moduleInfo.getModuleName());
           
            MenuInfo info = new MenuInfo(moduleInfo.getModuleName(), true);
            this.loadingModuleLogsList.add("The module: [" + moduleInfo.getModuleName() + "] was succesfully loaded.");
            return info;
        } else {
            String msg = "ModuleDecl [" + file.getName() + "] is null. Possibly parse error.";
            System.out.println(msg);
            this.loadingModuleLogsList.add(msg);
            return null;
        }
    }

    /**
     * Given a file name of a module definition file it returns its source code.
     *
     * @param xmlFileName name (extension included) of the XML file of a module
     * definition.
     * @return Verilog source used at definition time.
     */
    public String getSourceCode(File moduleFile) {
        if (!moduleFile.exists()) {
            this.loadingModuleLogsList.add("The module file: [" + moduleFile.getName() + "], doesn't exist.");
            return null;
        }
        
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(moduleFile);

            doc.normalize();

            NodeList program = doc.getElementsByTagName("behaviour");
            if (program.getLength() != 1) {
                String msg = "The file: [" + moduleFile.getName() +"] is invalid, doesn't have a behavior section.";
                this.loadingModuleLogsList.add(msg);
                return null;
            }
            String source = ((Element) program.item(0)).getTextContent();
            return source;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            String msg = "An error ocurred at getting SourceCode from the file: [" + moduleFile.getName() + "].";
            this.loadingModuleLogsList.add(msg);
            return null;
        }
    }

    /**
     * It parses and returns a memory representation of a Verilog source code.
     *
     * @param source Verilog source code
     * @return a <code>ModuleDecl</code> instance result of parsing the source
     * code.
     */
    public ModuleDecl getModuleLogic(String source) {
        try {
            return CompilationHelper.parseWithSemantics(source);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            this.loadingModuleLogsList.add("Couldn't parse the source code, error: [" + ex.getMessage() + "].");
            return null;
        }        
    }

    /**
     * It parses a XML file named
     * <code>xmlFileName</code> its metadata.
     *
     * @param xmlFileName name (extension included) of the XML file of a module
     * definition.
     * @param moduleStringName name of the module at the XML file at modules folder
     * @return <code>ModuleInfo</code> instance
     */
    public ModuleInfo getModuleInfo(String moduleStringName) {
        File dir = new File(Configuration.MODULE_METADATA_DIRECTORY_PATH);
        if (!dir.exists()) {
            Configuration.MODULE_METADATA_DIRECTORY_PATH = "modulesMetadata/";
            dir = new File("modulesMetadata/");
            if (!dir.exists())
                dir.mkdir();
        }
        
        File moduleInfoFile = new File(Configuration.MODULE_METADATA_DIRECTORY_PATH
                + "/" + TextUtils.AddMetadataTypeFileExtension(moduleStringName));
        this.loadingModuleLogsList.add("Checking for the Metadata File: [" + moduleInfoFile.getAbsolutePath() +"].");
        
        if(!moduleInfoFile.exists()){
            moduleInfoFile = new File(Configuration.MODULE_METADATA_DIRECTORY_PATH
                    + "/" + moduleStringName);
            this.loadingModuleLogsList.add("Metadata File not found.");
            this.loadingModuleLogsList.add("Checking for the Metadata File: [" + moduleInfoFile.getAbsolutePath() +"]");
            if (!moduleInfoFile.exists()) {
                System.out.println("Couldn't load the module: [ " + moduleStringName + " ] The metadata file doesn't exist.");
                this.loadingModuleLogsList.add("There isn't any Metadata File for the module: [" + moduleStringName + "].");
                return null;
            }
        }

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(moduleInfoFile);

            doc.normalize();

            NodeList names = doc.getElementsByTagName("name");
            if (names.getLength() == 0) {
                String msg = "Invlaid Metadata File: [ " + moduleInfoFile.getName() + " ] The file doesn't contain the needed 'name' element.";
                System.out.println(msg);
                this.loadingModuleLogsList.add(msg);
                return null;
            }
            Element name = (Element) names.item(0);
            if (moduleStringName == null || !name.getTextContent().equals(moduleStringName)) {
                String msg = "Couldn't load the Metadata Module File: [ " + moduleInfoFile.getName() + " ] The module name: ["+ name.getTextContent() +"] doesn't math with the Metadata module name: [" + moduleStringName + "].";
                System.out.println(msg);
                this.loadingModuleLogsList.add(msg);
            } else {
                ModuleInfo info = new ModuleInfo();
                info.setModuleName(moduleStringName);

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
            System.out.println(ex.getMessage());
            this.loadingModuleLogsList.add("The following error ocurred at getting information from Metadata File: [" + ex.getMessage() + "].");
            /*Parseo del XML*/
        /*si el parseo sale bien retornar el objecto sino null*/
        return null;
        }
    }

    /**
     * Static method to get the instance of a
     * <code>Loader</code> singleton
     *
     * @return the <code>Loader</code> singleton instance.
     */
    public static Loader getInstance() {
        return LoaderHolder.INSTANCE;
    }

    private String getModuleName(File moduleFile) {
       if (!moduleFile.exists()) {
            return null;
        }
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(moduleFile);

            doc.normalize();

            NodeList elements = doc.getElementsByTagName("element");
            if (elements.getLength() == 0) {
                /*ERROR*/
                return null;
            }
            return ((Element) elements.item(0)).getAttribute("moduleName");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    private static class LoaderHolder {

        private static final Loader INSTANCE = new Loader();
    }
}
