/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Exceptions;

/**
 * Exception thrown when a module graphic design was not found in 
 * its repository.
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 * @see ArchException
 */
public class ModuleDesignNotFoundException extends ArchException {

    /**
     * Constructor
     * @param message error message
     */
    public ModuleDesignNotFoundException(String message) {
        super(message);
    }
    
}
