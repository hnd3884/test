package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import javax.xml.namespace.QName;
import com.sun.xml.internal.txw2.TypedXmlWriter;

public interface ParamType extends TypedXmlWriter, Documented
{
    @XmlAttribute
    ParamType message(final QName p0);
    
    @XmlAttribute
    ParamType name(final String p0);
}
