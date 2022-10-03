package com.adventnet.nms.util;

public class XMLDataException extends RuntimeException
{
    public XMLDataException() {
    }
    
    public XMLDataException(final String s) {
        super(s);
    }
    
    public XMLDataException(final String s, final String s2) {
        super("Element " + s + ": " + s2);
    }
}
