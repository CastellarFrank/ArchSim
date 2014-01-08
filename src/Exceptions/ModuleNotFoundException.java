/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Exceptions;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class ModuleNotFoundException extends ArchException {

    public ModuleNotFoundException(String moduleName) {
        super(moduleName + " is not registered in local repository");
    }
    
}
