/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataStructures;

import Simulation.Configuration;
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
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class Loader {

    private Loader() {
    }

    public void setup() {
        loadSettings();
        loadModules();
    }

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

    public void loadModules() {
        //Iterará un directorio y cargará la info de los modulos en ModuleRepository
        File directory = new File(Configuration.MODULES_DIRECTORY_PATH);
        List<File> modules = (List<File>) FileUtils.listFiles(directory, null, true);
        for (File file : modules) {
            if (Configuration.DEBUG_MODE) {
                System.out.println("Loading module... " + file.getAbsolutePath());
            }
            ModuleInfo moduleInfo = getModuleInfo(file.getName());
            if (moduleInfo != null) {
                ModuleRepository.getInstance().registerModule(moduleInfo.getModuleName(), moduleInfo);
            }
        }
    }

    public ModuleInfo getModuleInfo(String fileNameXml) {
        File moduleInfoFile = new File(Configuration.MODULE_METADATA_DIRECTORY_PATH
                + "/" + fileNameXml);

        if (!moduleInfoFile.exists()) {
            return null;
        }

        String fileName = fileNameXml.replaceFirst(".xml", "");
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

    public static Loader getInstance() {
        return LoaderHolder.INSTANCE;
    }

    private static class LoaderHolder {

        private static final Loader INSTANCE = new Loader();
    }
}
