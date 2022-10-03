package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlIDREF;

public class XmlIdRefImpl extends JavaStringHolderEx implements XmlIDREF
{
    public XmlIdRefImpl() {
        super(XmlIDREF.type, false);
    }
    
    public XmlIdRefImpl(final SchemaType type, final boolean complex) {
        super(type, complex);
    }
}
