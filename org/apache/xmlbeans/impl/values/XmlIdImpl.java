package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlID;

public class XmlIdImpl extends JavaStringHolderEx implements XmlID
{
    public XmlIdImpl() {
        super(XmlID.type, false);
    }
    
    public XmlIdImpl(final SchemaType type, final boolean complex) {
        super(type, complex);
    }
}
