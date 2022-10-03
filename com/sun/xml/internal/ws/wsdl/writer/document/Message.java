package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.TypedXmlWriter;

@XmlElement("message")
public interface Message extends TypedXmlWriter, Documented
{
    @XmlAttribute
    Message name(final String p0);
    
    @XmlElement
    Part part();
}
