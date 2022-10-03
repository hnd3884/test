package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.TypedXmlWriter;

@XmlElement("complexType")
public interface ComplexType extends Annotated, ComplexTypeModel, TypedXmlWriter
{
    @XmlAttribute("final")
    ComplexType _final(final String[] p0);
    
    @XmlAttribute("final")
    ComplexType _final(final String p0);
    
    @XmlAttribute
    ComplexType block(final String[] p0);
    
    @XmlAttribute
    ComplexType block(final String p0);
    
    @XmlAttribute("abstract")
    ComplexType _abstract(final boolean p0);
    
    @XmlAttribute
    ComplexType name(final String p0);
}
