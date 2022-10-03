package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.TypedXmlWriter;

@XmlElement("attribute")
public interface TopLevelAttribute extends Annotated, AttributeType, FixedOrDefault, TypedXmlWriter
{
    @XmlAttribute
    TopLevelAttribute name(final String p0);
}
