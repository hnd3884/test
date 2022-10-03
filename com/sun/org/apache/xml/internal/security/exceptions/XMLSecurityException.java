package com.sun.org.apache.xml.internal.security.exceptions;

import java.text.MessageFormat;
import com.sun.org.apache.xml.internal.security.utils.I18n;

public class XMLSecurityException extends Exception
{
    private static final long serialVersionUID = 1L;
    protected String msgID;
    
    public XMLSecurityException() {
        super("Missing message string");
        this.msgID = null;
    }
    
    public XMLSecurityException(final String msgID) {
        super(I18n.getExceptionMessage(msgID));
        this.msgID = msgID;
    }
    
    public XMLSecurityException(final String msgID, final Object[] array) {
        super(MessageFormat.format(I18n.getExceptionMessage(msgID), array));
        this.msgID = msgID;
    }
    
    public XMLSecurityException(final Exception ex) {
        super(ex.getMessage(), ex);
    }
    
    public XMLSecurityException(final Exception ex, final String msgID) {
        super(I18n.getExceptionMessage(msgID, ex), ex);
        this.msgID = msgID;
    }
    
    @Deprecated
    public XMLSecurityException(final String s, final Exception ex) {
        this(ex, s);
    }
    
    public XMLSecurityException(final Exception ex, final String msgID, final Object[] array) {
        super(MessageFormat.format(I18n.getExceptionMessage(msgID), array), ex);
        this.msgID = msgID;
    }
    
    @Deprecated
    public XMLSecurityException(final String s, final Object[] array, final Exception ex) {
        this(ex, s, array);
    }
    
    public String getMsgID() {
        if (this.msgID == null) {
            return "Missing message ID";
        }
        return this.msgID;
    }
    
    @Override
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
        if (super.getCause() != null) {
            s = s + "\nOriginal Exception was " + super.getCause().toString();
        }
        return s;
    }
    
    @Override
    public void printStackTrace() {
        synchronized (System.err) {
            super.printStackTrace(System.err);
        }
    }
    
    public Exception getOriginalException() {
        if (this.getCause() instanceof Exception) {
            return (Exception)this.getCause();
        }
        return null;
    }
}
