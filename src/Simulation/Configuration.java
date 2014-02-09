/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation;

import DataStructures.Setting;
import GUI.Design.DesignWindow;
import java.awt.Color;
import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class Configuration {
    public static String MODULES_DIRECTORY_PATH = "modules/";
    public static String MODULE_METADATA_DIRECTORY_PATH = "modulesMetadata/";
    public static final String CONFIGURATION_FILE_DIRECTORY_PATH = "configs/";
    public static final String CONFIG_FILE_NAME = "globalSettings.xml";
    public static String THEME = "themes/default-alt.xml";
    public static Boolean COMPILE_ON_SAVE = true;
    
    public static Double LOGIC_1_VOLTAGE = 2.5;
    public static Double LOGIC_0_VOLTAGE = 0.0;
    public static Boolean SMALL_GRID = true;
    public static Integer REPAINT_PAUSE = 10;
    public static Boolean DEBUG_MODE = true;
    public static Boolean LOGIC_VALUES_AS_NUMBER = false;
    public static Boolean KEEP_CONNECTED_ON_DRAG = true;
    
    public static Color BACKGROUND_COLOR = Color.WHITE;
    
    public static HashMap<String, Setting> otherSettings = new HashMap<String, Setting>();
    
    public static void addSetting(String name, Setting setting) {
        otherSettings.put(name, setting);
    }
    
    public static void addSetting(String name, String type, Object value) {
        otherSettings.put(name, new Setting(type, name, value));
    }
    
    public static Object getSetting(String name) {
        if (name.equals("MODULES_DIRECTORY_PATH"))
            return MODULES_DIRECTORY_PATH;
        if (name.equals("LOGIC_1_VOLTAGE"))
            return LOGIC_1_VOLTAGE;
        
        return otherSettings.get(name).value;
    }
    
    public static void saveToFile(String fileName) {
        File settings = new File(CONFIGURATION_FILE_DIRECTORY_PATH + fileName);
        
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document document = docBuilder.newDocument();

            Element root = document.createElement("configs");
            document.appendChild(root);
            
            //<editor-fold defaultstate="collapsed" desc="Configs">
            root.appendChild(createElement(document, "MODULES_DIRECTORY_PATH", 
                    MODULES_DIRECTORY_PATH.toString(), 
                    MODULES_DIRECTORY_PATH.getClass().getSimpleName()));
            
            root.appendChild(createElement(document, "MODULE_METADATA_DIRECTORY_PATH", 
                    MODULE_METADATA_DIRECTORY_PATH.toString(), 
                    MODULE_METADATA_DIRECTORY_PATH.getClass().getSimpleName()));
            
            root.appendChild(createElement(document, "COMPILE_ON_SAVE", 
                    COMPILE_ON_SAVE.toString(), 
                    COMPILE_ON_SAVE.getClass().getSimpleName()));
            
            root.appendChild(createElement(document, "LOGIC_1_VOLTAGE", 
                    LOGIC_1_VOLTAGE.toString(), 
                    LOGIC_1_VOLTAGE.getClass().getSimpleName()));
            
            root.appendChild(createElement(document, "LOGIC_0_VOLTAGE", 
                    LOGIC_0_VOLTAGE.toString(), 
                    LOGIC_0_VOLTAGE.getClass().getSimpleName()));
            
            root.appendChild(createElement(document, "SMALL_GRID", 
                    SMALL_GRID.toString(), 
                    SMALL_GRID.getClass().getSimpleName()));
            
            root.appendChild(createElement(document, "REPAINT_PAUSE", 
                    REPAINT_PAUSE.toString(), 
                    REPAINT_PAUSE.getClass().getSimpleName()));
            
            root.appendChild(createElement(document, "DEBUG_MODE", 
                    DEBUG_MODE.toString(), 
                    DEBUG_MODE.getClass().getSimpleName()));
            
            root.appendChild(createElement(document, "LOGIC_VALUES_AS_NUMBER", 
                    LOGIC_VALUES_AS_NUMBER.toString(), 
                    LOGIC_VALUES_AS_NUMBER.getClass().getSimpleName()));
            
            root.appendChild(createElement(document, "KEEP_CONNECTED_ON_DRAG", 
                    KEEP_CONNECTED_ON_DRAG.toString(), 
                    KEEP_CONNECTED_ON_DRAG.getClass().getSimpleName()));
            //</editor-fold>            

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult result = new StreamResult(settings);

            transformer.transform(domSource, result);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(DesignWindow.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }
    
    public static Element createElement(Document doc, String name, String value, String type) {
        Element element = doc.createElement("config");
        element.setAttribute("name", name);
        element.setAttribute("type", type);
        element.setAttribute("value", value);
        
        return element;
    }
}
