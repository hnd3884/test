package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlString;

public class XmlStringEnumeration extends JavaStringEnumerationHolderEx implements XmlString
{
    public XmlStringEnumeration(final SchemaType type, final boolean complex) {
        super(type, complex);
    }
}
