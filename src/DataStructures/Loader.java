/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataStructures;

import Simulation.Configuration;
import VerilogCompiler.SyntacticTree.PortDirection;
import java.io.File;
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
    }
    
    public void loadModules() {
        //Iterará un directorio y cargará la info de los modulos en ModuleRepository
        File directory = new File(Configuration.MODULES_DIRECTORY_PATH);
        List<File> modules = (List<File>)FileUtils.listFiles(directory, null, true);
        for (File file : modules) {
            System.out.println(file.getAbsolutePath());
            ModuleInfo moduleInfo = getModuleInfo(file.getName());
            if (moduleInfo != null) {
                ModuleRepository.getInstance().registerModule(moduleInfo.getModuleName(), moduleInfo);
            }
        }
    }
    
    public ModuleInfo getModuleInfo(String fileNameXml) {
        File moduleInfoFile = new File(Configuration.MODULE_METADATA_DIRECTORY_PATH +
                "/" + fileNameXml);
        
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
                    
                    PortDirection portDirection = PortDirection.valueOf(type);
                    portInfos.add(new PortInfo(portDirection, portName));
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
