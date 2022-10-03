package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import javax.xml.namespace.QName;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.TypedXmlWriter;

@XmlElement("part")
public interface Part extends TypedXmlWriter, OpenAtts
{
    @XmlAttribute
    Part element(final QName p0);
    
    @XmlAttribute
    Part type(final QName p0);
    
    @XmlAttribute
    Part name(final String p0);
}
