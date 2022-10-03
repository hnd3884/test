package com.sun.xml.internal.ws.wsdl.writer.document.soap12;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.TypedXmlWriter;

@XmlElement("fault")
public interface SOAPFault extends TypedXmlWriter, BodyType
{
    @XmlAttribute
    SOAPFault name(final String p0);
}
