package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.TypedXmlWriter;

public interface Wildcard extends Annotated, TypedXmlWriter
{
    @XmlAttribute
    Wildcard processContents(final String p0);
    
    @XmlAttribute
    Wildcard namespace(final String[] p0);
    
    @XmlAttribute
    Wildcard namespace(final String p0);
}
