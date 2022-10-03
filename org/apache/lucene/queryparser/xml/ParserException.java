package org.apache.lucene.queryparser.xml;

public class ParserException extends Exception
{
    public ParserException() {
    }
    
    public ParserException(final String message) {
        super(message);
    }
    
    public ParserException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public ParserException(final Throwable cause) {
        super(cause);
    }
}
