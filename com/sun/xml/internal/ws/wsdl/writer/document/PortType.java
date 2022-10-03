package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.TypedXmlWriter;

@XmlElement("portType")
public interface PortType extends TypedXmlWriter, Documented
{
    @XmlAttribute
    PortType name(final String p0);
    
    @XmlElement
    Operation operation();
}
