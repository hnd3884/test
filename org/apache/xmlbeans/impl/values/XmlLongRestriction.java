package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlLong;

public class XmlLongRestriction extends JavaLongHolderEx implements XmlLong
{
    public XmlLongRestriction(final SchemaType type, final boolean complex) {
        super(type, complex);
    }
}
