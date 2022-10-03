package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.ws.wsdl.writer.document.soap.SOAPOperation;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.TypedXmlWriter;

public interface BindingOperationType extends TypedXmlWriter, StartWithExtensionsType
{
    @XmlAttribute
    BindingOperationType name(final String p0);
    
    @XmlElement(value = "operation", ns = "http://schemas.xmlsoap.org/wsdl/soap/")
    SOAPOperation soapOperation();
    
    @XmlElement(value = "operation", ns = "http://schemas.xmlsoap.org/wsdl/soap12/")
    com.sun.xml.internal.ws.wsdl.writer.document.soap12.SOAPOperation soap12Operation();
    
    @XmlElement
    Fault fault();
    
    @XmlElement
    StartWithExtensionsType output();
    
    @XmlElement
    StartWithExtensionsType input();
}
