package com.sun.xml.internal.ws.message.stream;

import com.sun.xml.internal.ws.api.message.Message;
import org.xml.sax.SAXException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.message.AttachmentSetImpl;
import com.sun.xml.internal.ws.api.SOAPVersion;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.message.AbstractMessageImpl;

public class PayloadStreamReaderMessage extends AbstractMessageImpl
{
    private final StreamMessage message;
    
    public PayloadStreamReaderMessage(final XMLStreamReader reader, final SOAPVersion soapVer) {
        this(null, reader, new AttachmentSetImpl(), soapVer);
    }
    
    public PayloadStreamReaderMessage(@Nullable final MessageHeaders headers, @NotNull final XMLStreamReader reader, @NotNull final AttachmentSet attSet, @NotNull final SOAPVersion soapVersion) {
        super(soapVersion);
        this.message = new StreamMessage(headers, attSet, reader, soapVersion);
    }
    
    @Override
    public boolean hasHeaders() {
        return this.message.hasHeaders();
    }
    
    @Override
    public AttachmentSet getAttachments() {
        return this.message.getAttachments();
    }
    
    @Override
    public String getPayloadLocalPart() {
        return this.message.getPayloadLocalPart();
    }
    
    @Override
    public String getPayloadNamespaceURI() {
        return this.message.getPayloadNamespaceURI();
    }
    
    @Override
    public boolean hasPayload() {
        return true;
    }
    
    @Override
    public Source readPayloadAsSource() {
        return this.message.readPayloadAsSource();
    }
    
    @Override
    public XMLStreamReader readPayload() throws XMLStreamException {
        return this.message.readPayload();
    }
    
    @Override
    public void writePayloadTo(final XMLStreamWriter sw) throws XMLStreamException {
        this.message.writePayloadTo(sw);
    }
    
    @Override
    public <T> T readPayloadAsJAXB(final Unmarshaller unmarshaller) throws JAXBException {
        return (T)this.message.readPayloadAsJAXB(unmarshaller);
    }
    
    @Override
    public void writeTo(final ContentHandler contentHandler, final ErrorHandler errorHandler) throws SAXException {
        this.message.writeTo(contentHandler, errorHandler);
    }
    
    @Override
    protected void writePayloadTo(final ContentHandler contentHandler, final ErrorHandler errorHandler, final boolean fragment) throws SAXException {
        this.message.writePayloadTo(contentHandler, errorHandler, fragment);
    }
    
    @Override
    public Message copy() {
        return this.message.copy();
    }
    
    @NotNull
    @Override
    public MessageHeaders getHeaders() {
        return this.message.getHeaders();
    }
}
