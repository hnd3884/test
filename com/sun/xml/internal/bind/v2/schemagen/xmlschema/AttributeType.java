package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import javax.xml.namespace.QName;
import com.sun.xml.internal.txw2.TypedXmlWriter;

public interface AttributeType extends SimpleTypeHost, TypedXmlWriter
{
    @XmlAttribute
    AttributeType type(final QName p0);
}
