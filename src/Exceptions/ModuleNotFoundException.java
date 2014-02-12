/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Exceptions;

/**
 * Exception thrown when a module metadata is supposed to be registered in
 * a repository but it isn't.
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 * @see ArchException
 */
public class ModuleNotFoundException extends ArchException {

    /**
     * Constructor
     * @param moduleName module name that caused the exception.
     */
    public ModuleNotFoundException(String moduleName) {
        super(moduleName + " is not registered in local repository");
    }
    
}
