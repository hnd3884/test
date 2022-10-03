package com.sun.xml.internal.ws.message.source;

import com.sun.xml.internal.ws.api.message.MessageHeaders;
import org.xml.sax.SAXException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.bind.api.Bridge;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import com.sun.xml.internal.ws.api.message.Packet;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.api.pipe.Codecs;
import com.sun.xml.internal.ws.streaming.SourceReaderFactory;
import com.sun.xml.internal.ws.api.SOAPVersion;
import javax.xml.transform.Source;
import com.sun.xml.internal.ws.api.message.Message;

public class ProtocolSourceMessage extends Message
{
    private final Message sm;
    
    public ProtocolSourceMessage(final Source source, final SOAPVersion soapVersion) {
        final XMLStreamReader reader = SourceReaderFactory.createSourceReader(source, true);
        final StreamSOAPCodec codec = Codecs.createSOAPEnvelopeXmlCodec(soapVersion);
        this.sm = codec.decode(reader);
    }
    
    @Override
    public boolean hasHeaders() {
        return this.sm.hasHeaders();
    }
    
    @Override
    public String getPayloadLocalPart() {
        return this.sm.getPayloadLocalPart();
    }
    
    @Override
    public String getPayloadNamespaceURI() {
        return this.sm.getPayloadNamespaceURI();
    }
    
    @Override
    public boolean hasPayload() {
        return this.sm.hasPayload();
    }
    
    @Override
    public Source readPayloadAsSource() {
        return this.sm.readPayloadAsSource();
    }
    
    @Override
    public XMLStreamReader readPayload() throws XMLStreamException {
        return this.sm.readPayload();
    }
    
    @Override
    public void writePayloadTo(final XMLStreamWriter sw) throws XMLStreamException {
        this.sm.writePayloadTo(sw);
    }
    
    @Override
    public void writeTo(final XMLStreamWriter sw) throws XMLStreamException {
        this.sm.writeTo(sw);
    }
    
    @Override
    public Message copy() {
        return this.sm.copy();
    }
    
    @Override
    public Source readEnvelopeAsSource() {
        return this.sm.readEnvelopeAsSource();
    }
    
    @Override
    public SOAPMessage readAsSOAPMessage() throws SOAPException {
        return this.sm.readAsSOAPMessage();
    }
    
    @Override
    public SOAPMessage readAsSOAPMessage(final Packet packet, final boolean inbound) throws SOAPException {
        return this.sm.readAsSOAPMessage(packet, inbound);
    }
    
    @Override
    public <T> T readPayloadAsJAXB(final Unmarshaller unmarshaller) throws JAXBException {
        return this.sm.readPayloadAsJAXB(unmarshaller);
    }
    
    @Override
    @Deprecated
    public <T> T readPayloadAsJAXB(final Bridge<T> bridge) throws JAXBException {
        return this.sm.readPayloadAsJAXB(bridge);
    }
    
    @Override
    public <T> T readPayloadAsJAXB(final XMLBridge<T> bridge) throws JAXBException {
        return this.sm.readPayloadAsJAXB(bridge);
    }
    
    @Override
    public void writeTo(final ContentHandler contentHandler, final ErrorHandler errorHandler) throws SAXException {
        this.sm.writeTo(contentHandler, errorHandler);
    }
    
    @Override
    public SOAPVersion getSOAPVersion() {
        return this.sm.getSOAPVersion();
    }
    
    @Override
    public MessageHeaders getHeaders() {
        return this.sm.getHeaders();
    }
}
