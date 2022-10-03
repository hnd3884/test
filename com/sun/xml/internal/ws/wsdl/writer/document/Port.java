package com.sun.xml.internal.ws.wsdl.writer.document;

import javax.xml.namespace.QName;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.TypedXmlWriter;

@XmlElement("port")
public interface Port extends TypedXmlWriter, Documented
{
    @XmlAttribute
    Port name(final String p0);
    
    @XmlAttribute
    Port arrayType(final String p0);
    
    @XmlAttribute
    Port binding(final QName p0);
}
