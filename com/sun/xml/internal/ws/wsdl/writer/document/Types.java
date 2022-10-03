package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.ws.wsdl.writer.document.xsd.Schema;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.TypedXmlWriter;

@XmlElement("types")
public interface Types extends TypedXmlWriter, Documented
{
    @XmlElement(value = "schema", ns = "http://www.w3.org/2001/XMLSchema")
    Schema schema();
}
