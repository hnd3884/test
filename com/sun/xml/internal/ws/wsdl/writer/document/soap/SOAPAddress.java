package com.sun.xml.internal.ws.wsdl.writer.document.soap;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.TypedXmlWriter;

@XmlElement("address")
public interface SOAPAddress extends TypedXmlWriter
{
    @XmlAttribute
    SOAPAddress location(final String p0);
}
