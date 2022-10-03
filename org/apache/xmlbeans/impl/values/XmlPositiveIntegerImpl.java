package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlPositiveInteger;

public class XmlPositiveIntegerImpl extends JavaIntegerHolderEx implements XmlPositiveInteger
{
    public XmlPositiveIntegerImpl() {
        super(XmlPositiveInteger.type, false);
    }
    
    public XmlPositiveIntegerImpl(final SchemaType type, final boolean complex) {
        super(type, complex);
    }
}
