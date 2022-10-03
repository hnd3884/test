package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlInteger;

public class XmlIntegerRestriction extends JavaIntegerHolderEx implements XmlInteger
{
    public XmlIntegerRestriction(final SchemaType type, final boolean complex) {
        super(type, complex);
    }
}
