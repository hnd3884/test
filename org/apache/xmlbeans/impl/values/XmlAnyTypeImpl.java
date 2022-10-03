package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public class XmlAnyTypeImpl extends XmlComplexContentImpl implements XmlObject
{
    public XmlAnyTypeImpl() {
        super(XmlAnyTypeImpl.type);
    }
    
    public XmlAnyTypeImpl(final SchemaType type) {
        super(type);
    }
}
