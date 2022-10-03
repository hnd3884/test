package com.sun.xml.internal.org.jvnet.fastinfoset;

public class EncodingAlgorithmException extends FastInfosetException
{
    public EncodingAlgorithmException(final String message) {
        super(message);
    }
    
    public EncodingAlgorithmException(final String message, final Exception e) {
        super(message, e);
    }
    
    public EncodingAlgorithmException(final Exception e) {
        super(e);
    }
}
