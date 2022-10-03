package org.apache.axiom.om.impl.common.serializer.push.sax;

import org.xml.sax.SAXException;
import javax.xml.stream.XMLStreamException;

final class SAXExceptionWrapper extends XMLStreamException
{
    private static final long serialVersionUID = -8523524667092495463L;
    
    SAXExceptionWrapper(final SAXException cause) {
        super(cause);
    }
}
