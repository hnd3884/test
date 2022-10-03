package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.TypedXmlWriter;

public interface FixedOrDefault extends TypedXmlWriter
{
    @XmlAttribute("default")
    FixedOrDefault _default(final String p0);
    
    @XmlAttribute
    FixedOrDefault fixed(final String p0);
}
