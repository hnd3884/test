package com.sun.xml.internal.org.jvnet.fastinfoset;

public class FastInfosetException extends Exception
{
    public FastInfosetException(final String message) {
        super(message);
    }
    
    public FastInfosetException(final String message, final Exception e) {
        super(message, e);
    }
    
    public FastInfosetException(final Exception e) {
        super(e);
    }
}
