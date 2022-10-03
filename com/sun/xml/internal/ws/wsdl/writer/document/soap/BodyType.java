package com.sun.xml.internal.ws.wsdl.writer.document.soap;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.TypedXmlWriter;

public interface BodyType extends TypedXmlWriter
{
    @XmlAttribute
    BodyType encodingStyle(final String p0);
    
    @XmlAttribute
    BodyType namespace(final String p0);
    
    @XmlAttribute
    BodyType use(final String p0);
    
    @XmlAttribute
    BodyType parts(final String p0);
}
