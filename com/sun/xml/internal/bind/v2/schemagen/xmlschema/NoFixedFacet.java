package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.TypedXmlWriter;

public interface NoFixedFacet extends Annotated, TypedXmlWriter
{
    @XmlAttribute
    NoFixedFacet value(final String p0);
}
