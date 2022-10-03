package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.TypedXmlWriter;

public interface Documented extends TypedXmlWriter
{
    @XmlElement
    Documented documentation(final String p0);
}
