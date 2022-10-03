package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlUnsignedShort;

public class XmlUnsignedShortImpl extends JavaIntHolderEx implements XmlUnsignedShort
{
    public XmlUnsignedShortImpl() {
        super(XmlUnsignedShort.type, false);
    }
    
    public XmlUnsignedShortImpl(final SchemaType type, final boolean complex) {
        super(type, complex);
    }
}
