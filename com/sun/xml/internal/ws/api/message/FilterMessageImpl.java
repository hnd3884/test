package com.sun.xml.internal.ws.api.message;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import org.xml.sax.SAXException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.bind.api.Bridge;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.namespace.QName;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.istack.internal.NotNull;

public class FilterMessageImpl extends Message
{
    private final Message delegate;
    
    protected FilterMessageImpl(final Message delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public boolean hasHeaders() {
        return this.delegate.hasHeaders();
    }
    
    @NotNull
    @Override
    public MessageHeaders getHeaders() {
        return this.delegate.getHeaders();
    }
    
    @NotNull
    @Override
    public AttachmentSet getAttachments() {
        return this.delegate.getAttachments();
    }
    
    @Override
    protected boolean hasAttachments() {
        return this.delegate.hasAttachments();
    }
    
    @Override
    public boolean isOneWay(@NotNull final WSDLPort port) {
        return this.delegate.isOneWay(port);
    }
    
    @Nullable
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
    
    @Nullable
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
        return this.delegate.readAsSOAPMessage();
    }
    
    @Override
    public SOAPMessage readAsSOAPMessage(final Packet packet, final boolean inbound) throws SOAPException {
        return this.delegate.readAsSOAPMessage(packet, inbound);
    }
    
    @Override
    public <T> T readPayloadAsJAXB(final Unmarshaller unmarshaller) throws JAXBException {
        return this.delegate.readPayloadAsJAXB(unmarshaller);
    }
    
    @Override
    @Deprecated
    public <T> T readPayloadAsJAXB(final Bridge<T> bridge) throws JAXBException {
        return this.delegate.readPayloadAsJAXB(bridge);
    }
    
    @Override
    public <T> T readPayloadAsJAXB(final XMLBridge<T> bridge) throws JAXBException {
        return this.delegate.readPayloadAsJAXB(bridge);
    }
    
    @Override
    public XMLStreamReader readPayload() throws XMLStreamException {
        return this.delegate.readPayload();
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
    
    @NotNull
    @Override
    public String getID(@NotNull final WSBinding binding) {
        return this.delegate.getID(binding);
    }
    
    @NotNull
    @Override
    public String getID(final AddressingVersion av, final SOAPVersion sv) {
        return this.delegate.getID(av, sv);
    }
    
    @Override
    public SOAPVersion getSOAPVersion() {
        return this.delegate.getSOAPVersion();
    }
}
