package com.sun.xml.internal.ws.addressing;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.namespace.QName;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;

public class WSEPRExtension extends WSEndpointReference.EPRExtension
{
    XMLStreamBuffer xsb;
    final QName qname;
    
    public WSEPRExtension(final XMLStreamBuffer xsb, final QName qname) {
        this.xsb = xsb;
        this.qname = qname;
    }
    
    @Override
    public XMLStreamReader readAsXMLStreamReader() throws XMLStreamException {
        return this.xsb.readAsXMLStreamReader();
    }
    
    @Override
    public QName getQName() {
        return this.qname;
    }
}
