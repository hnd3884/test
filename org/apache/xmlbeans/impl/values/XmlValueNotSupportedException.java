package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.XmlError;

public class XmlValueNotSupportedException extends XmlValueOutOfRangeException
{
    public XmlValueNotSupportedException() {
    }
    
    public XmlValueNotSupportedException(final String message) {
        super(message);
    }
    
    public XmlValueNotSupportedException(final String code, final Object[] args) {
        super(XmlError.formattedMessage(code, args));
    }
}
