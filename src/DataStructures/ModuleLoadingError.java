/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataStructures;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Franklin
 */
public class ModuleLoadingError {
    String moduleName;
    String mainMessage;
    List<String> errorsDetailList;
    
    public ModuleLoadingError(String moduleName){
        this(moduleName, new ArrayList<String>());
    }
    
    public ModuleLoadingError(String moduleName, List<String> errorDetailsList){
        this.moduleName = moduleName;
        this.mainMessage = "The module file: [" + this.moduleName + "] couldn't be loaded.";
        this.errorsDetailList = new ArrayList<String>(errorDetailsList);
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getMainMessage() {
        return mainMessage;
    }

    public List<String> getErrorsDetailList() {
        return errorsDetailList;
    }

    public void setErrorsDetailList(List<String> errorsDetailList) {
        this.errorsDetailList = errorsDetailList;
    }
}
