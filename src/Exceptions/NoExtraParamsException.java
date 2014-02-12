/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Exceptions;

/**
 * Exception thrown when a <code>BaseElement</code> was expecting extra parameters
 * but they were not specify.
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 * @see ArchException
 */
public class NoExtraParamsException extends ArchException {

    /**
     * Constructor
     * @param message error message
     */
    public NoExtraParamsException(String message) {
        super(message);
    }
    
}
