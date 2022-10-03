package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlDate;

public class XmlDateImpl extends JavaGDateHolderEx implements XmlDate
{
    public XmlDateImpl() {
        super(XmlDate.type, false);
    }
    
    public XmlDateImpl(final SchemaType type, final boolean complex) {
        super(type, complex);
    }
}
