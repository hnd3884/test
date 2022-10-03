package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.TypedXmlWriter;

public interface ContentModelContainer extends TypedXmlWriter
{
    @XmlElement
    LocalElement element();
    
    @XmlElement
    Any any();
    
    @XmlElement
    ExplicitGroup all();
    
    @XmlElement
    ExplicitGroup sequence();
    
    @XmlElement
    ExplicitGroup choice();
}
