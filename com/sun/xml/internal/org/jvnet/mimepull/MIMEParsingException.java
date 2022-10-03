package com.sun.xml.internal.org.jvnet.mimepull;

public class MIMEParsingException extends RuntimeException
{
    public MIMEParsingException() {
    }
    
    public MIMEParsingException(final String message) {
        super(message);
    }
    
    public MIMEParsingException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public MIMEParsingException(final Throwable cause) {
        super(cause);
    }
}
