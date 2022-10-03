package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;

public class SOAPVersionMismatchException extends SOAPExceptionImpl
{
    public SOAPVersionMismatchException() {
    }
    
    public SOAPVersionMismatchException(final String reason) {
        super(reason);
    }
    
    public SOAPVersionMismatchException(final String reason, final Throwable cause) {
        super(reason, cause);
    }
    
    public SOAPVersionMismatchException(final Throwable cause) {
        super(cause);
    }
}
