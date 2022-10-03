package com.sun.xml.internal.ws.message;

import com.sun.xml.internal.ws.api.message.Message;
import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.unmarshaller.DOMScanner;
import com.sun.istack.internal.FragmentContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.util.DOMUtil;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.ws.streaming.DOMStreamReader;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.bind.api.Bridge;
import javax.xml.bind.JAXBException;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.bind.Unmarshaller;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.Source;
import com.sun.xml.internal.ws.api.message.HeaderList;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.SOAPVersion;
import org.w3c.dom.Element;
import com.sun.xml.internal.ws.api.message.MessageHeaders;

public final class DOMMessage extends AbstractMessageImpl
{
    private MessageHeaders headers;
    private final Element payload;
    
    public DOMMessage(final SOAPVersion ver, final Element payload) {
        this(ver, null, payload);
    }
    
    public DOMMessage(final SOAPVersion ver, final MessageHeaders headers, final Element payload) {
        this(ver, headers, payload, null);
    }
    
    public DOMMessage(final SOAPVersion ver, final MessageHeaders headers, final Element payload, final AttachmentSet attachments) {
        super(ver);
        this.headers = headers;
        this.payload = payload;
        this.attachmentSet = attachments;
        assert payload != null;
    }
    
    private DOMMessage(final DOMMessage that) {
        super(that);
        this.headers = HeaderList.copy(that.headers);
        this.payload = that.payload;
    }
    
    @Override
    public boolean hasHeaders() {
        return this.getHeaders().hasHeaders();
    }
    
    @Override
    public MessageHeaders getHeaders() {
        if (this.headers == null) {
            this.headers = new HeaderList(this.getSOAPVersion());
        }
        return this.headers;
    }
    
    @Override
    public String getPayloadLocalPart() {
        return this.payload.getLocalName();
    }
    
    @Override
    public String getPayloadNamespaceURI() {
        return this.payload.getNamespaceURI();
    }
    
    @Override
    public boolean hasPayload() {
        return true;
    }
    
    @Override
    public Source readPayloadAsSource() {
        return new DOMSource(this.payload);
    }
    
    @Override
    public <T> T readPayloadAsJAXB(final Unmarshaller unmarshaller) throws JAXBException {
        if (this.hasAttachments()) {
            unmarshaller.setAttachmentUnmarshaller(new AttachmentUnmarshallerImpl(this.getAttachments()));
        }
        try {
            return (T)unmarshaller.unmarshal(this.payload);
        }
        finally {
            unmarshaller.setAttachmentUnmarshaller(null);
        }
    }
    
    @Override
    @Deprecated
    public <T> T readPayloadAsJAXB(final Bridge<T> bridge) throws JAXBException {
        return bridge.unmarshal(this.payload, this.hasAttachments() ? new AttachmentUnmarshallerImpl(this.getAttachments()) : null);
    }
    
    @Override
    public XMLStreamReader readPayload() throws XMLStreamException {
        final DOMStreamReader dss = new DOMStreamReader();
        dss.setCurrentNode(this.payload);
        dss.nextTag();
        assert dss.getEventType() == 1;
        return dss;
    }
    
    @Override
    public void writePayloadTo(final XMLStreamWriter sw) {
        try {
            if (this.payload != null) {
                DOMUtil.serializeNode(this.payload, sw);
            }
        }
        catch (final XMLStreamException e) {
            throw new WebServiceException(e);
        }
    }
    
    @Override
    protected void writePayloadTo(ContentHandler contentHandler, final ErrorHandler errorHandler, final boolean fragment) throws SAXException {
        if (fragment) {
            contentHandler = new FragmentContentHandler(contentHandler);
        }
        final DOMScanner ds = new DOMScanner();
        ds.setContentHandler(contentHandler);
        ds.scan(this.payload);
    }
    
    @Override
    public Message copy() {
        return new DOMMessage(this);
    }
}
