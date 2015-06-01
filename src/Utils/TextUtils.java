/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import Simulation.Configuration;

/**
 *
 * @author Franklin
 */
public class TextUtils {
    public static String GetFileExtensionWithDot(String fileName){
        int index = fileName.lastIndexOf('.');
        if(index < 0){
            return "";
        }
        
        return fileName.substring(index);
    }
    
    public static String GetFileExtensionWithoutDot(String fileName){
        return GetFileExtensionWithDot(fileName).replaceFirst(".", "");
    }
    
    public static String GetFileNameWithoutExtension(String fileName){
        String fileExtension = GetFileExtensionWithDot(fileName);
        return fileName.replaceFirst(fileExtension, "");
    }
    
    public static String GetFileExtensionWithoutDotAndRemovitFromFileName(StringBuilder fileName){
        String fileExtension = GetFileExtensionWithDot(fileName.toString());
        fileName.replace(0, fileName.length(), fileExtension);
        return fileExtension.replaceFirst(".", "");
    }
    
    public static String AddDesignTypeFileExtension(String fileName){
        return AddExtensionToFileName(fileName, Configuration.DESIGN_FILE_EXTENSION);
    }
    
    public static String AddSimulationTypeFileExtension(String fileName){
        return AddExtensionToFileName(fileName, Configuration.SIMULATION_FILE_EXTENSION);
    }
    
    public static String AddMetadataTypeFileExtension(String fileName){
        return AddExtensionToFileName(fileName, Configuration.METADATA_FILE_EXTENSION);
    }
    
    public static String AddExtensionToFileName(String fileName, String extension){
        String fileExtension = GetFileExtensionWithDot(fileName);
        String newExtension = "." + extension;
        if(fileExtension.equals("")){
            return fileName + newExtension;
        }
        
        if(fileExtension.equals(newExtension)){
            return fileName;
        }
        
        return GetFileNameWithoutExtension(fileName) + newExtension;
    }
    
    public static String LeftEmptyPadding(String original, int quantity){
        return Padding(original, " ", quantity, true);
    }
    
    public static String Padding(String original, String text, int quantity, boolean isLeft){
        String leftPadding = "";
        String rightPadding = "";
        StringBuilder padding = new StringBuilder();
        
        int iterations = quantity - original.length();
        if(iterations <= 0)
            return original;
        
        for(int i = 0; i< iterations;i ++)
            padding.append(text);
        
        if(isLeft)
            leftPadding = padding.toString();
        else
            rightPadding = padding.toString();
        
        return leftPadding +  original + rightPadding;
    }
}
