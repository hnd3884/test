package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.TypedXmlWriter;

@XmlElement("complexContent")
public interface ComplexContent extends Annotated, TypedXmlWriter
{
    @XmlElement
    ComplexExtension extension();
    
    @XmlElement
    ComplexRestriction restriction();
    
    @XmlAttribute
    ComplexContent mixed(final boolean p0);
}
