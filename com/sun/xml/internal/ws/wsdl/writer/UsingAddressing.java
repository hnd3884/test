package com.sun.xml.internal.ws.wsdl.writer;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.ws.wsdl.writer.document.StartWithExtensionsType;
import com.sun.xml.internal.txw2.TypedXmlWriter;

@XmlElement(value = "http://www.w3.org/2006/05/addressing/wsdl", ns = "UsingAddressing")
public interface UsingAddressing extends TypedXmlWriter, StartWithExtensionsType
{
    @XmlAttribute(value = "required", ns = "http://schemas.xmlsoap.org/wsdl/")
    void required(final boolean p0);
}
