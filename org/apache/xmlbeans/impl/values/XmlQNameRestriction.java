package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlQName;

public class XmlQNameRestriction extends JavaQNameHolderEx implements XmlQName
{
    public XmlQNameRestriction(final SchemaType type, final boolean complex) {
        super(type, complex);
    }
}
