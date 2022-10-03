package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.TypedXmlWriter;

public interface Occurs extends TypedXmlWriter
{
    @XmlAttribute
    Occurs minOccurs(final int p0);
    
    @XmlAttribute
    Occurs maxOccurs(final String p0);
    
    @XmlAttribute
    Occurs maxOccurs(final int p0);
}
