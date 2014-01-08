/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation;

import DataStructures.Setting;
import java.util.HashMap;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class Configuration {
    public static String MODULES_DIRECTORY_PATH = "modules/";
    public static String MODULE_METADATA_DIRECTORY_PATH = "modulesMetadata/";
    public static boolean COMPILE_ON_SAVE = true;
    
    public static double LOGIC_1_VOLTAGE = 2.5;
    public static double LOGIC_0_VOLTAGE = 0;
    public static boolean SMALL_GRID = true;
    public static int REPAINT_PAUSE = 10;
    public static boolean DEBUG_MODE = true;
    public static boolean LOGIC_VALUES_AS_NUMBER = false;
    
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
}
