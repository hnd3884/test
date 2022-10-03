package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.TypedXmlWriter;

public interface ComplexTypeHost extends TypeHost, TypedXmlWriter
{
    @XmlElement
    ComplexType complexType();
}
