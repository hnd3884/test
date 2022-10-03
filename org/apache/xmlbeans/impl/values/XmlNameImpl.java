package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlName;

public class XmlNameImpl extends JavaStringHolderEx implements XmlName
{
    public XmlNameImpl() {
        super(XmlName.type, false);
    }
    
    public XmlNameImpl(final SchemaType type, final boolean complex) {
        super(type, complex);
    }
}
