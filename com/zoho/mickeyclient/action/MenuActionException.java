package com.zoho.mickeyclient.action;

import java.text.MessageFormat;

public class MenuActionException extends RuntimeException
{
    private static final long serialVersionUID = -4060297913572088150L;
    
    public MenuActionException() {
    }
    
    public MenuActionException(final String message) {
        super(message);
    }
    
    public MenuActionException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public MenuActionException(final ActionErrors error, final Object... arguments) {
        this(MessageFormat.format(error.toString(), arguments));
    }
}
