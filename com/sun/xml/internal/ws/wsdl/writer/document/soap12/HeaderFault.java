package com.sun.xml.internal.ws.wsdl.writer.document.soap12;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import javax.xml.namespace.QName;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.TypedXmlWriter;

@XmlElement("headerFault")
public interface HeaderFault extends TypedXmlWriter, BodyType
{
    @XmlAttribute
    HeaderFault message(final QName p0);
}
