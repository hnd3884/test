package com.sun.xml.internal.messaging.saaj;

import java.io.PrintWriter;
import java.io.PrintStream;
import javax.xml.soap.SOAPException;

public class SOAPExceptionImpl extends SOAPException
{
    private Throwable cause;
    
    public SOAPExceptionImpl() {
        this.cause = null;
    }
    
    public SOAPExceptionImpl(final String reason) {
        super(reason);
        this.cause = null;
    }
    
    public SOAPExceptionImpl(final String reason, final Throwable cause) {
        super(reason);
        this.initCause(cause);
    }
    
    public SOAPExceptionImpl(final Throwable cause) {
        super(cause.toString());
        this.initCause(cause);
    }
    
    @Override
    public String getMessage() {
        final String message = super.getMessage();
        if (message == null && this.cause != null) {
            return this.cause.getMessage();
        }
        return message;
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
    
    @Override
    public synchronized Throwable initCause(final Throwable cause) {
        if (this.cause != null) {
            throw new IllegalStateException("Can't override cause");
        }
        if (cause == this) {
            throw new IllegalArgumentException("Self-causation not permitted");
        }
        this.cause = cause;
        return this;
    }
    
    @Override
    public void printStackTrace() {
        super.printStackTrace();
        if (this.cause != null) {
            System.err.println("\nCAUSE:\n");
            this.cause.printStackTrace();
        }
    }
    
    @Override
    public void printStackTrace(final PrintStream s) {
        super.printStackTrace(s);
        if (this.cause != null) {
            s.println("\nCAUSE:\n");
            this.cause.printStackTrace(s);
        }
    }
    
    @Override
    public void printStackTrace(final PrintWriter s) {
        super.printStackTrace(s);
        if (this.cause != null) {
            s.println("\nCAUSE:\n");
            this.cause.printStackTrace(s);
        }
    }
}
