/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Franklin
 */
public class FileUtils {
    public static void DeleteFile(File file){
        if(file.exists())
            file.delete();
    }
    
    public static void DeleteFile(String filePath){
        DeleteFile(new File(filePath));
    }
    
    public static boolean CheckIfPathIsChild(File parentDirectory, File possibleChild){
        
        File parent;
        try {
            parent = parentDirectory.getCanonicalFile();
        } catch (IOException ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        if(!parent.exists() || !parent.isDirectory())
            return false;
        
        File tempElement;
        try {
            tempElement = possibleChild.getCanonicalFile();
        } catch (IOException ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        while(tempElement != null){
            if(tempElement.equals(parent))
                return true;
            tempElement = tempElement.getParentFile();
        }
        
        return false;
    }
}
