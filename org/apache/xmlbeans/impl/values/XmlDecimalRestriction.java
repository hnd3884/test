package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlDecimal;

public class XmlDecimalRestriction extends JavaDecimalHolderEx implements XmlDecimal
{
    public XmlDecimalRestriction(final SchemaType type, final boolean complex) {
        super(type, complex);
    }
}
