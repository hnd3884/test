package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import javax.xml.namespace.QName;
import com.sun.xml.internal.txw2.TypedXmlWriter;

public interface ExtensionType extends Annotated, TypedXmlWriter
{
    @XmlAttribute
    ExtensionType base(final QName p0);
}
