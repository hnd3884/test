package com.sun.xml.internal.ws.message.jaxb;

import java.util.Iterator;
import java.util.List;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.util.xml.XMLReaderComposite;
import java.util.ArrayList;
import com.sun.xml.internal.ws.encoding.TagInfoset;
import java.io.OutputStream;
import com.sun.xml.internal.ws.streaming.XMLStreamWriterUtil;
import com.sun.xml.internal.ws.streaming.MtomStreamWriter;
import javax.xml.stream.XMLStreamWriter;
import com.sun.istack.internal.FragmentContentHandler;
import org.xml.sax.ErrorHandler;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferResult;
import org.xml.sax.SAXException;
import javax.xml.transform.Result;
import javax.xml.bind.util.JAXBResult;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import org.xml.sax.ContentHandler;
import com.sun.xml.internal.ws.message.RootElementSniffer;
import com.sun.xml.internal.ws.api.message.HeaderList;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.message.AttachmentSetImpl;
import com.sun.xml.internal.ws.spi.db.BindingContextFactory;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.bind.JAXBException;
import javax.xml.ws.WebServiceException;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.message.stream.StreamMessage;
import javax.xml.bind.attachment.AttachmentMarshaller;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import javax.xml.bind.JAXBContext;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.StreamingSOAP;
import com.sun.xml.internal.ws.message.AbstractMessageImpl;

public final class JAXBMessage extends AbstractMessageImpl implements StreamingSOAP
{
    private MessageHeaders headers;
    private final Object jaxbObject;
    private final XMLBridge bridge;
    private final JAXBContext rawContext;
    private String nsUri;
    private String localName;
    private XMLStreamBuffer infoset;
    
    public static Message create(final BindingContext context, final Object jaxbObject, final SOAPVersion soapVersion, final MessageHeaders headers, final AttachmentSet attachments) {
        if (!context.hasSwaRef()) {
            return new JAXBMessage(context, jaxbObject, soapVersion, headers, attachments);
        }
        try {
            final MutableXMLStreamBuffer xsb = new MutableXMLStreamBuffer();
            final Marshaller m = context.createMarshaller();
            final AttachmentMarshallerImpl am = new AttachmentMarshallerImpl(attachments);
            m.setAttachmentMarshaller(am);
            am.cleanup();
            m.marshal(jaxbObject, xsb.createFromXMLStreamWriter());
            return new StreamMessage(headers, attachments, xsb.readAsXMLStreamReader(), soapVersion);
        }
        catch (final JAXBException e) {
            throw new WebServiceException(e);
        }
        catch (final XMLStreamException e2) {
            throw new WebServiceException(e2);
        }
    }
    
    public static Message create(final BindingContext context, final Object jaxbObject, final SOAPVersion soapVersion) {
        return create(context, jaxbObject, soapVersion, null, null);
    }
    
    @Deprecated
    public static Message create(final JAXBContext context, final Object jaxbObject, final SOAPVersion soapVersion) {
        return create(BindingContextFactory.create(context), jaxbObject, soapVersion, null, null);
    }
    
    @Deprecated
    public static Message createRaw(final JAXBContext context, final Object jaxbObject, final SOAPVersion soapVersion) {
        return new JAXBMessage(context, jaxbObject, soapVersion, null, null);
    }
    
    private JAXBMessage(final BindingContext context, final Object jaxbObject, final SOAPVersion soapVer, final MessageHeaders headers, final AttachmentSet attachments) {
        super(soapVer);
        this.bridge = context.createFragmentBridge();
        this.rawContext = null;
        this.jaxbObject = jaxbObject;
        this.headers = headers;
        this.attachmentSet = attachments;
    }
    
    private JAXBMessage(final JAXBContext rawContext, final Object jaxbObject, final SOAPVersion soapVer, final MessageHeaders headers, final AttachmentSet attachments) {
        super(soapVer);
        this.rawContext = rawContext;
        this.bridge = null;
        this.jaxbObject = jaxbObject;
        this.headers = headers;
        this.attachmentSet = attachments;
    }
    
    public static Message create(final XMLBridge bridge, final Object jaxbObject, final SOAPVersion soapVer) {
        if (!bridge.context().hasSwaRef()) {
            return new JAXBMessage(bridge, jaxbObject, soapVer);
        }
        try {
            final MutableXMLStreamBuffer xsb = new MutableXMLStreamBuffer();
            final AttachmentSetImpl attachments = new AttachmentSetImpl();
            final AttachmentMarshallerImpl am = new AttachmentMarshallerImpl(attachments);
            bridge.marshal(jaxbObject, xsb.createFromXMLStreamWriter(), am);
            am.cleanup();
            return new StreamMessage(null, attachments, xsb.readAsXMLStreamReader(), soapVer);
        }
        catch (final JAXBException e) {
            throw new WebServiceException(e);
        }
        catch (final XMLStreamException e2) {
            throw new WebServiceException(e2);
        }
    }
    
    private JAXBMessage(final XMLBridge bridge, final Object jaxbObject, final SOAPVersion soapVer) {
        super(soapVer);
        this.bridge = bridge;
        this.rawContext = null;
        this.jaxbObject = jaxbObject;
        final QName tagName = bridge.getTypeInfo().tagName;
        this.nsUri = tagName.getNamespaceURI();
        this.localName = tagName.getLocalPart();
        this.attachmentSet = new AttachmentSetImpl();
    }
    
    public JAXBMessage(final JAXBMessage that) {
        super(that);
        this.headers = that.headers;
        if (this.headers != null) {
            this.headers = new HeaderList(this.headers);
        }
        this.attachmentSet = that.attachmentSet;
        this.jaxbObject = that.jaxbObject;
        this.bridge = that.bridge;
        this.rawContext = that.rawContext;
    }
    
    @Override
    public boolean hasHeaders() {
        return this.headers != null && this.headers.hasHeaders();
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
        if (this.localName == null) {
            this.sniff();
        }
        return this.localName;
    }
    
    @Override
    public String getPayloadNamespaceURI() {
        if (this.nsUri == null) {
            this.sniff();
        }
        return this.nsUri;
    }
    
    @Override
    public boolean hasPayload() {
        return true;
    }
    
    private void sniff() {
        final RootElementSniffer sniffer = new RootElementSniffer(false);
        try {
            if (this.rawContext != null) {
                final Marshaller m = this.rawContext.createMarshaller();
                m.setProperty("jaxb.fragment", Boolean.TRUE);
                m.marshal(this.jaxbObject, sniffer);
            }
            else {
                this.bridge.marshal(this.jaxbObject, sniffer, null);
            }
        }
        catch (final JAXBException e) {
            this.nsUri = sniffer.getNsUri();
            this.localName = sniffer.getLocalName();
        }
    }
    
    @Override
    public Source readPayloadAsSource() {
        return new JAXBBridgeSource(this.bridge, this.jaxbObject);
    }
    
    @Override
    public <T> T readPayloadAsJAXB(final Unmarshaller unmarshaller) throws JAXBException {
        final JAXBResult out = new JAXBResult(unmarshaller);
        try {
            out.getHandler().startDocument();
            if (this.rawContext != null) {
                final Marshaller m = this.rawContext.createMarshaller();
                m.setProperty("jaxb.fragment", Boolean.TRUE);
                m.marshal(this.jaxbObject, out);
            }
            else {
                this.bridge.marshal(this.jaxbObject, out);
            }
            out.getHandler().endDocument();
        }
        catch (final SAXException e) {
            throw new JAXBException(e);
        }
        return (T)out.getResult();
    }
    
    @Override
    public XMLStreamReader readPayload() throws XMLStreamException {
        try {
            if (this.infoset == null) {
                if (this.rawContext != null) {
                    final XMLStreamBufferResult sbr = new XMLStreamBufferResult();
                    final Marshaller m = this.rawContext.createMarshaller();
                    m.setProperty("jaxb.fragment", Boolean.TRUE);
                    m.marshal(this.jaxbObject, sbr);
                    this.infoset = sbr.getXMLStreamBuffer();
                }
                else {
                    final MutableXMLStreamBuffer buffer = new MutableXMLStreamBuffer();
                    this.writePayloadTo(buffer.createFromXMLStreamWriter());
                    this.infoset = buffer;
                }
            }
            final XMLStreamReader reader = this.infoset.readAsXMLStreamReader();
            if (reader.getEventType() == 7) {
                XMLStreamReaderUtil.nextElementContent(reader);
            }
            return reader;
        }
        catch (final JAXBException e) {
            throw new WebServiceException(e);
        }
    }
    
    @Override
    protected void writePayloadTo(ContentHandler contentHandler, final ErrorHandler errorHandler, final boolean fragment) throws SAXException {
        try {
            if (fragment) {
                contentHandler = new FragmentContentHandler(contentHandler);
            }
            final AttachmentMarshallerImpl am = new AttachmentMarshallerImpl(this.attachmentSet);
            if (this.rawContext != null) {
                final Marshaller m = this.rawContext.createMarshaller();
                m.setProperty("jaxb.fragment", Boolean.TRUE);
                m.setAttachmentMarshaller(am);
                m.marshal(this.jaxbObject, contentHandler);
            }
            else {
                this.bridge.marshal(this.jaxbObject, contentHandler, am);
            }
            am.cleanup();
        }
        catch (final JAXBException e) {
            throw new WebServiceException(e.getMessage(), e);
        }
    }
    
    @Override
    public void writePayloadTo(final XMLStreamWriter sw) throws XMLStreamException {
        try {
            final AttachmentMarshaller am = (sw instanceof MtomStreamWriter) ? ((MtomStreamWriter)sw).getAttachmentMarshaller() : new AttachmentMarshallerImpl(this.attachmentSet);
            final String encoding = XMLStreamWriterUtil.getEncoding(sw);
            final OutputStream os = this.bridge.supportOutputStream() ? XMLStreamWriterUtil.getOutputStream(sw) : null;
            if (this.rawContext != null) {
                final Marshaller m = this.rawContext.createMarshaller();
                m.setProperty("jaxb.fragment", Boolean.TRUE);
                m.setAttachmentMarshaller(am);
                if (os != null) {
                    m.marshal(this.jaxbObject, os);
                }
                else {
                    m.marshal(this.jaxbObject, sw);
                }
            }
            else if (os != null && encoding != null && encoding.equalsIgnoreCase("utf-8")) {
                this.bridge.marshal(this.jaxbObject, os, sw.getNamespaceContext(), am);
            }
            else {
                this.bridge.marshal(this.jaxbObject, sw, am);
            }
        }
        catch (final JAXBException e) {
            throw new WebServiceException(e);
        }
    }
    
    @Override
    public Message copy() {
        return new JAXBMessage(this);
    }
    
    @Override
    public XMLStreamReader readEnvelope() {
        final int base = this.soapVersion.ordinal() * 3;
        this.envelopeTag = JAXBMessage.DEFAULT_TAGS.get(base);
        this.bodyTag = JAXBMessage.DEFAULT_TAGS.get(base + 2);
        final List<XMLStreamReader> hReaders = new ArrayList<XMLStreamReader>();
        final XMLReaderComposite.ElemInfo envElem = new XMLReaderComposite.ElemInfo(this.envelopeTag, null);
        final XMLReaderComposite.ElemInfo bdyElem = new XMLReaderComposite.ElemInfo(this.bodyTag, envElem);
        for (final Header h : this.getHeaders().asList()) {
            try {
                hReaders.add(h.readHeader());
            }
            catch (final XMLStreamException e) {
                throw new RuntimeException(e);
            }
        }
        XMLStreamReader soapHeader = null;
        if (hReaders.size() > 0) {
            this.headerTag = JAXBMessage.DEFAULT_TAGS.get(base + 1);
            final XMLReaderComposite.ElemInfo hdrElem = new XMLReaderComposite.ElemInfo(this.headerTag, envElem);
            soapHeader = new XMLReaderComposite(hdrElem, hReaders.toArray(new XMLStreamReader[hReaders.size()]));
        }
        try {
            final XMLStreamReader payload = this.readPayload();
            final XMLStreamReader soapBody = new XMLReaderComposite(bdyElem, new XMLStreamReader[] { payload });
            final XMLStreamReader[] soapContent = (soapHeader != null) ? new XMLStreamReader[] { soapHeader, soapBody } : new XMLStreamReader[] { soapBody };
            return new XMLReaderComposite(envElem, soapContent);
        }
        catch (final XMLStreamException e2) {
            throw new RuntimeException(e2);
        }
    }
}
