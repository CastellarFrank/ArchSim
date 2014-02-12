/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Exceptions;

/**
 * Thrown when a runtime error happen inside this project code.
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public abstract class ArchException extends RuntimeException {

    /**
     * Constructor
     * @param message error message
     */
    public ArchException(String message) {
        super(message);
    }
    
}
