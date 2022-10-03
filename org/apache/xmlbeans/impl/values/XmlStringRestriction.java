package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlString;

public class XmlStringRestriction extends JavaStringHolderEx implements XmlString
{
    public XmlStringRestriction(final SchemaType type, final boolean complex) {
        super(type, complex);
    }
}
