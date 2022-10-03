package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlToken;

public class XmlTokenImpl extends JavaStringHolderEx implements XmlToken
{
    public XmlTokenImpl() {
        super(XmlToken.type, false);
    }
    
    public XmlTokenImpl(final SchemaType type, final boolean complex) {
        super(type, complex);
    }
}
