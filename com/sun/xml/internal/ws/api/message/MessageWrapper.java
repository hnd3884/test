package com.sun.xml.internal.ws.api.message;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.bind.api.Bridge;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.SOAPException;
import com.sun.xml.internal.ws.message.saaj.SAAJMessage;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import org.xml.sax.SAXException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import com.sun.xml.internal.ws.message.stream.StreamMessage;

class MessageWrapper extends StreamMessage
{
    Packet packet;
    Message delegate;
    StreamMessage streamDelegate;
    
    @Override
    public void writePayloadTo(final ContentHandler contentHandler, final ErrorHandler errorHandler, final boolean fragment) throws SAXException {
        this.streamDelegate.writePayloadTo(contentHandler, errorHandler, fragment);
    }
    
    @Override
    public String getBodyPrologue() {
        return this.streamDelegate.getBodyPrologue();
    }
    
    @Override
    public String getBodyEpilogue() {
        return this.streamDelegate.getBodyEpilogue();
    }
    
    MessageWrapper(final Packet p, final Message m) {
        super(m.getSOAPVersion());
        this.packet = p;
        this.delegate = m;
        this.streamDelegate = ((m instanceof StreamMessage) ? ((StreamMessage)m) : null);
        this.setMessageMedadata(p);
    }
    
    @Override
    public int hashCode() {
        return this.delegate.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this.delegate.equals(obj);
    }
    
    @Override
    public boolean hasHeaders() {
        return this.delegate.hasHeaders();
    }
    
    @Override
    public AttachmentSet getAttachments() {
        return this.delegate.getAttachments();
    }
    
    @Override
    public String toString() {
        return this.delegate.toString();
    }
    
    @Override
    public boolean isOneWay(final WSDLPort port) {
        return this.delegate.isOneWay(port);
    }
    
    @Override
    public String getPayloadLocalPart() {
        return this.delegate.getPayloadLocalPart();
    }
    
    @Override
    public String getPayloadNamespaceURI() {
        return this.delegate.getPayloadNamespaceURI();
    }
    
    @Override
    public boolean hasPayload() {
        return this.delegate.hasPayload();
    }
    
    @Override
    public boolean isFault() {
        return this.delegate.isFault();
    }
    
    @Override
    public QName getFirstDetailEntryName() {
        return this.delegate.getFirstDetailEntryName();
    }
    
    @Override
    public Source readEnvelopeAsSource() {
        return this.delegate.readEnvelopeAsSource();
    }
    
    @Override
    public Source readPayloadAsSource() {
        return this.delegate.readPayloadAsSource();
    }
    
    @Override
    public SOAPMessage readAsSOAPMessage() throws SOAPException {
        if (!(this.delegate instanceof SAAJMessage)) {
            this.delegate = this.toSAAJ(this.packet, null);
        }
        return this.delegate.readAsSOAPMessage();
    }
    
    @Override
    public SOAPMessage readAsSOAPMessage(final Packet p, final boolean inbound) throws SOAPException {
        if (!(this.delegate instanceof SAAJMessage)) {
            this.delegate = this.toSAAJ(p, inbound);
        }
        return this.delegate.readAsSOAPMessage();
    }
    
    @Override
    public Object readPayloadAsJAXB(final Unmarshaller unmarshaller) throws JAXBException {
        return this.delegate.readPayloadAsJAXB(unmarshaller);
    }
    
    @Override
    public <T> T readPayloadAsJAXB(final Bridge<T> bridge) throws JAXBException {
        return this.delegate.readPayloadAsJAXB(bridge);
    }
    
    @Override
    public <T> T readPayloadAsJAXB(final XMLBridge<T> bridge) throws JAXBException {
        return this.delegate.readPayloadAsJAXB(bridge);
    }
    
    @Override
    public XMLStreamReader readPayload() {
        try {
            return this.delegate.readPayload();
        }
        catch (final XMLStreamException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public void consume() {
        this.delegate.consume();
    }
    
    @Override
    public void writePayloadTo(final XMLStreamWriter sw) throws XMLStreamException {
        this.delegate.writePayloadTo(sw);
    }
    
    @Override
    public void writeTo(final XMLStreamWriter sw) throws XMLStreamException {
        this.delegate.writeTo(sw);
    }
    
    @Override
    public void writeTo(final ContentHandler contentHandler, final ErrorHandler errorHandler) throws SAXException {
        this.delegate.writeTo(contentHandler, errorHandler);
    }
    
    @Override
    public Message copy() {
        return this.delegate.copy();
    }
    
    @Override
    public String getID(final WSBinding binding) {
        return this.delegate.getID(binding);
    }
    
    @Override
    public String getID(final AddressingVersion av, final SOAPVersion sv) {
        return this.delegate.getID(av, sv);
    }
    
    @Override
    public SOAPVersion getSOAPVersion() {
        return this.delegate.getSOAPVersion();
    }
    
    @NotNull
    @Override
    public MessageHeaders getHeaders() {
        return this.delegate.getHeaders();
    }
    
    @Override
    public void setMessageMedadata(final MessageMetadata metadata) {
        super.setMessageMedadata(metadata);
        this.delegate.setMessageMedadata(metadata);
    }
}
