package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.TypedXmlWriter;

@XmlElement("import")
public interface Import extends Annotated, TypedXmlWriter
{
    @XmlAttribute
    Import namespace(final String p0);
    
    @XmlAttribute
    Import schemaLocation(final String p0);
}
