package com.sun.xml.internal.ws.wsdl.writer.document.xsd;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.ws.wsdl.writer.document.Documented;
import com.sun.xml.internal.txw2.TypedXmlWriter;

@XmlElement("schema")
public interface Schema extends TypedXmlWriter, Documented
{
    @XmlElement("import")
    Import _import();
    
    @XmlAttribute
    Schema targetNamespace(final String p0);
}
