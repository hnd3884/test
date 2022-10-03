package com.sun.xml.internal.ws.wsdl.writer.document.soap;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.TypedXmlWriter;

@XmlElement("operation")
public interface SOAPOperation extends TypedXmlWriter
{
    @XmlAttribute
    SOAPOperation soapAction(final String p0);
    
    @XmlAttribute
    SOAPOperation style(final String p0);
}
