package com.sun.xml.internal.ws.wsdl.writer.document.soap12;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import javax.xml.namespace.QName;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.TypedXmlWriter;

@XmlElement("header")
public interface Header extends TypedXmlWriter, BodyType
{
    @XmlAttribute
    Header message(final QName p0);
    
    @XmlElement
    HeaderFault headerFault();
    
    @XmlAttribute
    BodyType part(final String p0);
}
