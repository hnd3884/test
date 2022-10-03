package com.sun.xml.internal.ws.message;

import com.sun.xml.internal.ws.api.message.Message;
import org.xml.sax.SAXException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.HeaderList;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.MessageHeaders;

public class EmptyMessageImpl extends AbstractMessageImpl
{
    private final MessageHeaders headers;
    private final AttachmentSet attachmentSet;
    
    public EmptyMessageImpl(final SOAPVersion version) {
        super(version);
        this.headers = new HeaderList(version);
        this.attachmentSet = new AttachmentSetImpl();
    }
    
    public EmptyMessageImpl(MessageHeaders headers, @NotNull final AttachmentSet attachmentSet, final SOAPVersion version) {
        super(version);
        if (headers == null) {
            headers = new HeaderList(version);
        }
        this.attachmentSet = attachmentSet;
        this.headers = headers;
    }
    
    private EmptyMessageImpl(final EmptyMessageImpl that) {
        super(that);
        this.headers = new HeaderList(that.headers);
        this.attachmentSet = that.attachmentSet;
    }
    
    @Override
    public boolean hasHeaders() {
        return this.headers.hasHeaders();
    }
    
    @Override
    public MessageHeaders getHeaders() {
        return this.headers;
    }
    
    @Override
    public String getPayloadLocalPart() {
        return null;
    }
    
    @Override
    public String getPayloadNamespaceURI() {
        return null;
    }
    
    @Override
    public boolean hasPayload() {
        return false;
    }
    
    @Override
    public Source readPayloadAsSource() {
        return null;
    }
    
    @Override
    public XMLStreamReader readPayload() throws XMLStreamException {
        return null;
    }
    
    @Override
    public void writePayloadTo(final XMLStreamWriter sw) throws XMLStreamException {
    }
    
    public void writePayloadTo(final ContentHandler contentHandler, final ErrorHandler errorHandler, final boolean fragment) throws SAXException {
    }
    
    @Override
    public Message copy() {
        return new EmptyMessageImpl(this);
    }
}
