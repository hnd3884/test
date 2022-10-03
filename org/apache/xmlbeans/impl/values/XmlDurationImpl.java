package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlDuration;

public class XmlDurationImpl extends JavaGDurationHolderEx implements XmlDuration
{
    public XmlDurationImpl() {
        super(XmlDuration.type, false);
    }
    
    public XmlDurationImpl(final SchemaType type, final boolean complex) {
        super(type, complex);
    }
}
