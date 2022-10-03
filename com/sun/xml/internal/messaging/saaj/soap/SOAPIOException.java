package com.sun.xml.internal.messaging.saaj.soap;

import java.io.PrintWriter;
import java.io.PrintStream;
import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import java.io.IOException;

public class SOAPIOException extends IOException
{
    SOAPExceptionImpl soapException;
    
    public SOAPIOException() {
        (this.soapException = new SOAPExceptionImpl()).fillInStackTrace();
    }
    
    public SOAPIOException(final String s) {
        (this.soapException = new SOAPExceptionImpl(s)).fillInStackTrace();
    }
    
    public SOAPIOException(final String reason, final Throwable cause) {
        (this.soapException = new SOAPExceptionImpl(reason, cause)).fillInStackTrace();
    }
    
    public SOAPIOException(final Throwable cause) {
        super(cause.toString());
        (this.soapException = new SOAPExceptionImpl(cause)).fillInStackTrace();
    }
    
    @Override
    public Throwable fillInStackTrace() {
        if (this.soapException != null) {
            this.soapException.fillInStackTrace();
        }
        return this;
    }
    
    @Override
    public String getLocalizedMessage() {
        return this.soapException.getLocalizedMessage();
    }
    
    @Override
    public String getMessage() {
        return this.soapException.getMessage();
    }
    
    @Override
    public void printStackTrace() {
        this.soapException.printStackTrace();
    }
    
    @Override
    public void printStackTrace(final PrintStream s) {
        this.soapException.printStackTrace(s);
    }
    
    @Override
    public void printStackTrace(final PrintWriter s) {
        this.soapException.printStackTrace(s);
    }
    
    @Override
    public String toString() {
        return this.soapException.toString();
    }
}
