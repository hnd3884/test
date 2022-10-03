package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import javax.xml.namespace.QName;
import com.sun.xml.internal.txw2.TypedXmlWriter;

public interface SimpleRestrictionModel extends SimpleTypeHost, TypedXmlWriter
{
    @XmlAttribute
    SimpleRestrictionModel base(final QName p0);
    
    @XmlElement
    NoFixedFacet enumeration();
}
