package org.apache.xml.security.exceptions;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import org.apache.xml.security.utils.I18n;

public class XMLSecurityRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    protected Exception originalException;
    protected String msgID;
    
    public XMLSecurityRuntimeException() {
        super("Missing message string");
        this.originalException = null;
        this.msgID = null;
        this.originalException = null;
    }
    
    public XMLSecurityRuntimeException(final String msgID) {
        super(I18n.getExceptionMessage(msgID));
        this.originalException = null;
        this.msgID = msgID;
        this.originalException = null;
    }
    
    public XMLSecurityRuntimeException(final String msgID, final Object[] array) {
        super(MessageFormat.format(I18n.getExceptionMessage(msgID), array));
        this.originalException = null;
        this.msgID = msgID;
        this.originalException = null;
    }
    
    public XMLSecurityRuntimeException(final Exception originalException) {
        super("Missing message ID to locate message string in resource bundle \"org/apache/xml/security/resource/xmlsecurity\". Original Exception was a " + originalException.getClass().getName() + " and message " + originalException.getMessage());
        this.originalException = null;
        this.originalException = originalException;
    }
    
    public XMLSecurityRuntimeException(final String msgID, final Exception originalException) {
        super(I18n.getExceptionMessage(msgID, originalException));
        this.originalException = null;
        this.msgID = msgID;
        this.originalException = originalException;
    }
    
    public XMLSecurityRuntimeException(final String msgID, final Object[] array, final Exception originalException) {
        super(MessageFormat.format(I18n.getExceptionMessage(msgID), array));
        this.originalException = null;
        this.msgID = msgID;
        this.originalException = originalException;
    }
    
    public String getMsgID() {
        if (this.msgID == null) {
            return "Missing message ID";
        }
        return this.msgID;
    }
    
    public String toString() {
        final String name = this.getClass().getName();
        final String localizedMessage = super.getLocalizedMessage();
        String s;
        if (localizedMessage != null) {
            s = name + ": " + localizedMessage;
        }
        else {
            s = name;
        }
        if (this.originalException != null) {
            s = s + "\nOriginal Exception was " + this.originalException.toString();
        }
        return s;
    }
    
    public void printStackTrace() {
        synchronized (System.err) {
            super.printStackTrace(System.err);
            if (this.originalException != null) {
                this.originalException.printStackTrace(System.err);
            }
        }
    }
    
    public void printStackTrace(final PrintWriter printWriter) {
        super.printStackTrace(printWriter);
        if (this.originalException != null) {
            this.originalException.printStackTrace(printWriter);
        }
    }
    
    public void printStackTrace(final PrintStream printStream) {
        super.printStackTrace(printStream);
        if (this.originalException != null) {
            this.originalException.printStackTrace(printStream);
        }
    }
    
    public Exception getOriginalException() {
        return this.originalException;
    }
}
