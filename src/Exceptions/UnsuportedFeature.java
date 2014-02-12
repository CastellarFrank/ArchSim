/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Exceptions;

/**
 * Exception thrown when a not yet implemented feature is tried to be launched.
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 * @see ArchException
 */
public class UnsuportedFeature extends ArchException{

    /**
     * Constructor
     * @param message error message
     */
    public UnsuportedFeature(String message) {
        super(message);
    }
    
}
