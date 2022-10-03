package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.TypedXmlWriter;

@XmlElement("simpleType")
public interface SimpleType extends Annotated, SimpleDerivation, TypedXmlWriter
{
    @XmlAttribute("final")
    SimpleType _final(final String p0);
    
    @XmlAttribute("final")
    SimpleType _final(final String[] p0);
    
    @XmlAttribute
    SimpleType name(final String p0);
}
