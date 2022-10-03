package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.TypedXmlWriter;

@XmlElement("any")
public interface Any extends Occurs, Wildcard, TypedXmlWriter
{
}
