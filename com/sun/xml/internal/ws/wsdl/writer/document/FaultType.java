package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import javax.xml.namespace.QName;
import com.sun.xml.internal.txw2.TypedXmlWriter;

public interface FaultType extends TypedXmlWriter, Documented
{
    @XmlAttribute
    FaultType message(final QName p0);
    
    @XmlAttribute
    FaultType name(final String p0);
}
