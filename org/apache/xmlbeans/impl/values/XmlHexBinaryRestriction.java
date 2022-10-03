package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlHexBinary;

public class XmlHexBinaryRestriction extends JavaHexBinaryHolderEx implements XmlHexBinary
{
    public XmlHexBinaryRestriction(final SchemaType type, final boolean complex) {
        super(type, complex);
    }
}
