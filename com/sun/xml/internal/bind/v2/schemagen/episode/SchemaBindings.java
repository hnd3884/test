package com.sun.xml.internal.bind.v2.schemagen.episode;

import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.TypedXmlWriter;

public interface SchemaBindings extends TypedXmlWriter
{
    @XmlAttribute
    void map(final boolean p0);
    
    @XmlElement("package")
    Package _package();
}
