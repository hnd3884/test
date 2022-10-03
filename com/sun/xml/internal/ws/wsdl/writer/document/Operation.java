package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.TypedXmlWriter;

@XmlElement("operation")
public interface Operation extends TypedXmlWriter, Documented
{
    @XmlElement
    ParamType input();
    
    @XmlElement
    ParamType output();
    
    @XmlElement
    FaultType fault();
    
    @XmlAttribute
    Operation name(final String p0);
    
    @XmlAttribute
    Operation parameterOrder(final String p0);
}
