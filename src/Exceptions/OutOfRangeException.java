/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Exceptions;

/**
 * Exception thrown when tried to access a value out of range in a 
 * custom list.
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 * @see ArchException
 */
public class OutOfRangeException extends ArchException {

    /**
     * Constructor
     * @param message error message
     */
    public OutOfRangeException(String message) {
        super(message);
    }
    
}
