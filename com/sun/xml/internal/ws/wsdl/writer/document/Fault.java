package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.TypedXmlWriter;

@XmlElement("fault")
public interface Fault extends TypedXmlWriter, StartWithExtensionsType
{
    @XmlAttribute
    Fault name(final String p0);
}
