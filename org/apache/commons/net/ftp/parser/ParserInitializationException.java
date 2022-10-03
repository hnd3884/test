package org.apache.commons.net.ftp.parser;

public class ParserInitializationException extends RuntimeException
{
    private static final long serialVersionUID = 5563335279583210658L;
    
    public ParserInitializationException(final String message) {
        super(message);
    }
    
    public ParserInitializationException(final String message, final Throwable rootCause) {
        super(message, rootCause);
    }
    
    @Deprecated
    public Throwable getRootCause() {
        return super.getCause();
    }
}
