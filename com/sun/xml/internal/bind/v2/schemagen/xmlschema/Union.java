package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import javax.xml.namespace.QName;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.TypedXmlWriter;

@XmlElement("union")
public interface Union extends Annotated, SimpleTypeHost, TypedXmlWriter
{
    @XmlAttribute
    Union memberTypes(final QName[] p0);
}
