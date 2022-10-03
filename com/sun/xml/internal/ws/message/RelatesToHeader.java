package com.sun.xml.internal.ws.message;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.namespace.QName;

public final class RelatesToHeader extends StringHeader
{
    protected String type;
    private final QName typeAttributeName;
    
    public RelatesToHeader(final QName name, final String messageId, final String type) {
        super(name, messageId);
        this.type = type;
        this.typeAttributeName = new QName(name.getNamespaceURI(), "type");
    }
    
    public RelatesToHeader(final QName name, final String mid) {
        super(name, mid);
        this.typeAttributeName = new QName(name.getNamespaceURI(), "type");
    }
    
    public String getType() {
        return this.type;
    }
    
    @Override
    public void writeTo(final XMLStreamWriter w) throws XMLStreamException {
        w.writeStartElement("", this.name.getLocalPart(), this.name.getNamespaceURI());
        w.writeDefaultNamespace(this.name.getNamespaceURI());
        if (this.type != null) {
            w.writeAttribute("type", this.type);
        }
        w.writeCharacters(this.value);
        w.writeEndElement();
    }
    
    @Override
    public void writeTo(final SOAPMessage saaj) throws SOAPException {
        SOAPHeader header = saaj.getSOAPHeader();
        if (header == null) {
            header = saaj.getSOAPPart().getEnvelope().addHeader();
        }
        final SOAPHeaderElement she = header.addHeaderElement(this.name);
        if (this.type != null) {
            she.addAttribute(this.typeAttributeName, this.type);
        }
        she.addTextNode(this.value);
    }
}
