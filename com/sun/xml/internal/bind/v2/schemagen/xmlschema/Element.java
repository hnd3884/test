package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import javax.xml.namespace.QName;
import com.sun.xml.internal.txw2.TypedXmlWriter;

public interface Element extends Annotated, ComplexTypeHost, FixedOrDefault, SimpleTypeHost, TypedXmlWriter
{
    @XmlAttribute
    Element type(final QName p0);
    
    @XmlAttribute
    Element block(final String[] p0);
    
    @XmlAttribute
    Element block(final String p0);
    
    @XmlAttribute
    Element nillable(final boolean p0);
}
