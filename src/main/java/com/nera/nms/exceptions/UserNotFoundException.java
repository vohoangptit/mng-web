/**
 * 
 */
package com.nera.nms.exceptions;

public class UserNotFoundException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;
    public UserNotFoundException(String message) {
        super(message);
    }
    
}
