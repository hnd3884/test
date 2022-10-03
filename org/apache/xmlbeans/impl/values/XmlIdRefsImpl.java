package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlIDREFS;

public class XmlIdRefsImpl extends XmlListImpl implements XmlIDREFS
{
    public XmlIdRefsImpl() {
        super(XmlIDREFS.type, false);
    }
    
    public XmlIdRefsImpl(final SchemaType type, final boolean complex) {
        super(type, complex);
    }
}
