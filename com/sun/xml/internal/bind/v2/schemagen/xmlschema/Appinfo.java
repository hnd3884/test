package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.TypedXmlWriter;

@XmlElement("appinfo")
public interface Appinfo extends TypedXmlWriter
{
    @XmlAttribute
    Appinfo source(final String p0);
}
