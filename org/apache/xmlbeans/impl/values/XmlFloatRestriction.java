package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlFloat;

public class XmlFloatRestriction extends JavaFloatHolderEx implements XmlFloat
{
    public XmlFloatRestriction(final SchemaType type, final boolean complex) {
        super(type, complex);
    }
}
