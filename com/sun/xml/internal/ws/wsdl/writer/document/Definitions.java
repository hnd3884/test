package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.TypedXmlWriter;

@XmlElement("definitions")
public interface Definitions extends TypedXmlWriter, Documented
{
    @XmlAttribute
    Definitions name(final String p0);
    
    @XmlAttribute
    Definitions targetNamespace(final String p0);
    
    @XmlElement
    Service service();
    
    @XmlElement
    Binding binding();
    
    @XmlElement
    PortType portType();
    
    @XmlElement
    Message message();
    
    @XmlElement
    Types types();
    
    @XmlElement("import")
    Import _import();
}
