package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlTime;

public class XmlTimeImpl extends JavaGDateHolderEx implements XmlTime
{
    public XmlTimeImpl() {
        super(XmlTime.type, false);
    }
    
    public XmlTimeImpl(final SchemaType type, final boolean complex) {
        super(type, complex);
    }
}
