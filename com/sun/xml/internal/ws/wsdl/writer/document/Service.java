package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.TypedXmlWriter;

@XmlElement("service")
public interface Service extends TypedXmlWriter, Documented
{
    @XmlAttribute
    Service name(final String p0);
    
    @XmlElement
    Port port();
}
