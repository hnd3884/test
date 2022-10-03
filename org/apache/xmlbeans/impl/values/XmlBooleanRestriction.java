package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;

public class XmlBooleanRestriction extends JavaBooleanHolderEx implements XmlBoolean
{
    public XmlBooleanRestriction(final SchemaType type, final boolean complex) {
        super(type, complex);
    }
}
