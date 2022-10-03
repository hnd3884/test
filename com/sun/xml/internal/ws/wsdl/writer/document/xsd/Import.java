package com.sun.xml.internal.ws.wsdl.writer.document.xsd;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.ws.wsdl.writer.document.Documented;
import com.sun.xml.internal.txw2.TypedXmlWriter;

@XmlElement("import")
public interface Import extends TypedXmlWriter, Documented
{
    @XmlAttribute
    Import schemaLocation(final String p0);
    
    @XmlAttribute
    Import namespace(final String p0);
}
