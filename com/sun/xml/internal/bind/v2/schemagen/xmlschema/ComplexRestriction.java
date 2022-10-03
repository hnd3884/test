package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import javax.xml.namespace.QName;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.TypedXmlWriter;

@XmlElement("restriction")
public interface ComplexRestriction extends Annotated, AttrDecls, TypeDefParticle, TypedXmlWriter
{
    @XmlAttribute
    ComplexRestriction base(final QName p0);
}
