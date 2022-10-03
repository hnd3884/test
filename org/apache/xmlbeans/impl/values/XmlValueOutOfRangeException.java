package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.XmlError;

public class XmlValueOutOfRangeException extends IllegalArgumentException
{
    public XmlValueOutOfRangeException() {
    }
    
    public XmlValueOutOfRangeException(final String message) {
        super(message);
    }
    
    public XmlValueOutOfRangeException(final String code, final Object[] args) {
        super(XmlError.formattedMessage(code, args));
    }
}
