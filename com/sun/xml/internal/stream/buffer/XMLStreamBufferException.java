package com.sun.xml.internal.stream.buffer;

public class XMLStreamBufferException extends Exception
{
    public XMLStreamBufferException(final String message) {
        super(message);
    }
    
    public XMLStreamBufferException(final String message, final Exception e) {
        super(message, e);
    }
    
    public XMLStreamBufferException(final Exception e) {
        super(e);
    }
}
