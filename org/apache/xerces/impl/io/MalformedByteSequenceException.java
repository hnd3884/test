package org.apache.xerces.impl.io;

import java.util.Locale;
import org.apache.xerces.util.MessageFormatter;
import java.io.CharConversionException;

public final class MalformedByteSequenceException extends CharConversionException
{
    static final long serialVersionUID = 8436382245048328739L;
    private MessageFormatter fFormatter;
    private Locale fLocale;
    private String fDomain;
    private String fKey;
    private Object[] fArguments;
    private String fMessage;
    
    public MalformedByteSequenceException(final MessageFormatter fFormatter, final Locale fLocale, final String fDomain, final String fKey, final Object[] fArguments) {
        this.fFormatter = fFormatter;
        this.fLocale = fLocale;
        this.fDomain = fDomain;
        this.fKey = fKey;
        this.fArguments = fArguments;
    }
    
    public String getDomain() {
        return this.fDomain;
    }
    
    public String getKey() {
        return this.fKey;
    }
    
    public Object[] getArguments() {
        return this.fArguments;
    }
    
    public synchronized String getMessage() {
        if (this.fMessage == null) {
            this.fMessage = this.fFormatter.formatMessage(this.fLocale, this.fKey, this.fArguments);
            this.fFormatter = null;
            this.fLocale = null;
        }
        return this.fMessage;
    }
}
