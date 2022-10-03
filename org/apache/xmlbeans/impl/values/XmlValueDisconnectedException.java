package org.apache.xmlbeans.impl.values;

public class XmlValueDisconnectedException extends RuntimeException
{
    XmlValueDisconnectedException() {
    }
    
    XmlValueDisconnectedException(final String message) {
        super(message);
    }
    
    XmlValueDisconnectedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
