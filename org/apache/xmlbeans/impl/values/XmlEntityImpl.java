package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlENTITY;

public class XmlEntityImpl extends JavaStringHolderEx implements XmlENTITY
{
    public XmlEntityImpl() {
        super(XmlENTITY.type, false);
    }
    
    public XmlEntityImpl(final SchemaType type, final boolean complex) {
        super(type, complex);
    }
}
