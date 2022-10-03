package com.sun.xml.internal.ws.wsdl.writer.document.soap;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.TypedXmlWriter;

@XmlElement("binding")
public interface SOAPBinding extends TypedXmlWriter
{
    @XmlAttribute
    SOAPBinding transport(final String p0);
    
    @XmlAttribute
    SOAPBinding style(final String p0);
}
