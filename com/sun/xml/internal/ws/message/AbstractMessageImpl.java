package com.sun.xml.internal.ws.message;

import java.util.Collections;
import java.util.ArrayList;
import java.util.Map;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPException;
import com.sun.xml.internal.ws.message.saaj.SAAJMessage;
import com.sun.xml.internal.ws.api.message.MessageWritable;
import com.sun.xml.internal.ws.api.message.saaj.SAAJFactory;
import com.sun.xml.internal.ws.api.message.Packet;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import javax.xml.stream.XMLStreamException;
import java.util.Iterator;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.Header;
import javax.xml.stream.XMLStreamWriter;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.bind.api.Bridge;
import javax.xml.bind.JAXBException;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.bind.Unmarshaller;
import org.xml.sax.XMLReader;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.Source;
import java.util.List;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.AttributesImpl;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.encoding.TagInfoset;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Message;

public abstract class AbstractMessageImpl extends Message
{
    protected final SOAPVersion soapVersion;
    @NotNull
    protected TagInfoset envelopeTag;
    @NotNull
    protected TagInfoset headerTag;
    @NotNull
    protected TagInfoset bodyTag;
    protected static final AttributesImpl EMPTY_ATTS;
    protected static final LocatorImpl NULL_LOCATOR;
    protected static final List<TagInfoset> DEFAULT_TAGS;
    
    static void create(final SOAPVersion v, final List c) {
        final int base = v.ordinal() * 3;
        c.add(base, new TagInfoset(v.nsUri, "Envelope", "S", AbstractMessageImpl.EMPTY_ATTS, new String[] { "S", v.nsUri }));
        c.add(base + 1, new TagInfoset(v.nsUri, "Header", "S", AbstractMessageImpl.EMPTY_ATTS, new String[0]));
        c.add(base + 2, new TagInfoset(v.nsUri, "Body", "S", AbstractMessageImpl.EMPTY_ATTS, new String[0]));
    }
    
    protected AbstractMessageImpl(final SOAPVersion soapVersion) {
        this.soapVersion = soapVersion;
    }
    
    @Override
    public SOAPVersion getSOAPVersion() {
        return this.soapVersion;
    }
    
    protected AbstractMessageImpl(final AbstractMessageImpl that) {
        this.soapVersion = that.soapVersion;
    }
    
    @Override
    public Source readEnvelopeAsSource() {
        return new SAXSource(new XMLReaderImpl(this), XMLReaderImpl.THE_SOURCE);
    }
    
    @Override
    public <T> T readPayloadAsJAXB(final Unmarshaller unmarshaller) throws JAXBException {
        if (this.hasAttachments()) {
            unmarshaller.setAttachmentUnmarshaller(new AttachmentUnmarshallerImpl(this.getAttachments()));
        }
        try {
            return (T)unmarshaller.unmarshal(this.readPayloadAsSource());
        }
        finally {
            unmarshaller.setAttachmentUnmarshaller(null);
        }
    }
    
    @Override
    @Deprecated
    public <T> T readPayloadAsJAXB(final Bridge<T> bridge) throws JAXBException {
        return bridge.unmarshal(this.readPayloadAsSource(), this.hasAttachments() ? new AttachmentUnmarshallerImpl(this.getAttachments()) : null);
    }
    
    @Override
    public <T> T readPayloadAsJAXB(final XMLBridge<T> bridge) throws JAXBException {
        return bridge.unmarshal(this.readPayloadAsSource(), this.hasAttachments() ? new AttachmentUnmarshallerImpl(this.getAttachments()) : null);
    }
    
    @Override
    public void writeTo(final XMLStreamWriter w) throws XMLStreamException {
        final String soapNsUri = this.soapVersion.nsUri;
        w.writeStartDocument();
        w.writeStartElement("S", "Envelope", soapNsUri);
        w.writeNamespace("S", soapNsUri);
        if (this.hasHeaders()) {
            w.writeStartElement("S", "Header", soapNsUri);
            final MessageHeaders headers = this.getHeaders();
            for (final Header h : headers.asList()) {
                h.writeTo(w);
            }
            w.writeEndElement();
        }
        w.writeStartElement("S", "Body", soapNsUri);
        this.writePayloadTo(w);
        w.writeEndElement();
        w.writeEndElement();
        w.writeEndDocument();
    }
    
    @Override
    public void writeTo(final ContentHandler contentHandler, final ErrorHandler errorHandler) throws SAXException {
        final String soapNsUri = this.soapVersion.nsUri;
        contentHandler.setDocumentLocator(AbstractMessageImpl.NULL_LOCATOR);
        contentHandler.startDocument();
        contentHandler.startPrefixMapping("S", soapNsUri);
        contentHandler.startElement(soapNsUri, "Envelope", "S:Envelope", AbstractMessageImpl.EMPTY_ATTS);
        if (this.hasHeaders()) {
            contentHandler.startElement(soapNsUri, "Header", "S:Header", AbstractMessageImpl.EMPTY_ATTS);
            final MessageHeaders headers = this.getHeaders();
            for (final Header h : headers.asList()) {
                h.writeTo(contentHandler, errorHandler);
            }
            contentHandler.endElement(soapNsUri, "Header", "S:Header");
        }
        contentHandler.startElement(soapNsUri, "Body", "S:Body", AbstractMessageImpl.EMPTY_ATTS);
        this.writePayloadTo(contentHandler, errorHandler, true);
        contentHandler.endElement(soapNsUri, "Body", "S:Body");
        contentHandler.endElement(soapNsUri, "Envelope", "S:Envelope");
    }
    
    protected abstract void writePayloadTo(final ContentHandler p0, final ErrorHandler p1, final boolean p2) throws SAXException;
    
    public Message toSAAJ(final Packet p, final Boolean inbound) throws SOAPException {
        final SAAJMessage message = SAAJFactory.read(p);
        if (message instanceof MessageWritable) {
            ((MessageWritable)message).setMTOMConfiguration(p.getMtomFeature());
        }
        if (inbound != null) {
            this.transportHeaders(p, inbound, message.readAsSOAPMessage());
        }
        return message;
    }
    
    @Override
    public SOAPMessage readAsSOAPMessage() throws SOAPException {
        return SAAJFactory.read(this.soapVersion, this);
    }
    
    @Override
    public SOAPMessage readAsSOAPMessage(final Packet packet, final boolean inbound) throws SOAPException {
        final SOAPMessage msg = SAAJFactory.read(this.soapVersion, this, packet);
        this.transportHeaders(packet, inbound, msg);
        return msg;
    }
    
    private void transportHeaders(final Packet packet, final boolean inbound, final SOAPMessage msg) throws SOAPException {
        final Map<String, List<String>> headers = Message.getTransportHeaders(packet, inbound);
        if (headers != null) {
            Message.addSOAPMimeHeaders(msg.getMimeHeaders(), headers);
        }
        if (msg.saveRequired()) {
            msg.saveChanges();
        }
    }
    
    static {
        NULL_LOCATOR = new LocatorImpl();
        EMPTY_ATTS = new AttributesImpl();
        final List<TagInfoset> tagList = new ArrayList<TagInfoset>();
        create(SOAPVersion.SOAP_11, tagList);
        create(SOAPVersion.SOAP_12, tagList);
        DEFAULT_TAGS = Collections.unmodifiableList((List<? extends TagInfoset>)tagList);
    }
}
