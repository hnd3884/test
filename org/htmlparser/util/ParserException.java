package org.htmlparser.util;

public class ParserException extends ChainedException
{
    public ParserException() {
    }
    
    public ParserException(final String message) {
        super(message);
    }
    
    public ParserException(final Throwable throwable) {
        super(throwable);
    }
    
    public ParserException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
