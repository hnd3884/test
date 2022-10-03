package com.sun.org.apache.xml.internal.security.exceptions;

import java.text.MessageFormat;
import com.sun.org.apache.xml.internal.security.utils.I18n;

public class XMLSecurityRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    protected String msgID;
    
    public XMLSecurityRuntimeException() {
        super("Missing message string");
        this.msgID = null;
    }
    
    public XMLSecurityRuntimeException(final String msgID) {
        super(I18n.getExceptionMessage(msgID));
        this.msgID = msgID;
    }
    
    public XMLSecurityRuntimeException(final String msgID, final Object[] array) {
        super(MessageFormat.format(I18n.getExceptionMessage(msgID), array));
        this.msgID = msgID;
    }
    
    public XMLSecurityRuntimeException(final Exception ex) {
        super("Missing message ID to locate message string in resource bundle \"com.sun.org.apache.xml.internal.security/resource/xmlsecurity\". Original Exception was a " + ex.getClass().getName() + " and message " + ex.getMessage(), ex);
    }
    
    public XMLSecurityRuntimeException(final String msgID, final Exception ex) {
        super(I18n.getExceptionMessage(msgID, ex), ex);
        this.msgID = msgID;
    }
    
    public XMLSecurityRuntimeException(final String msgID, final Object[] array, final Exception ex) {
        super(MessageFormat.format(I18n.getExceptionMessage(msgID), array), ex);
        this.msgID = msgID;
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
        if (this.getCause() != null) {
            s = s + "\nOriginal Exception was " + this.getCause().toString();
        }
        return s;
    }
    
    public Exception getOriginalException() {
        if (this.getCause() instanceof Exception) {
            return (Exception)this.getCause();
        }
        return null;
    }
}
