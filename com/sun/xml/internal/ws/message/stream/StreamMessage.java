package com.sun.xml.internal.ws.message.stream;

import com.sun.xml.internal.stream.buffer.AbstractCreatorProcessor;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferMark;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import java.util.Map;
import java.util.HashMap;
import com.sun.xml.internal.ws.protocol.soap.VersionMismatchException;
import org.xml.sax.Locator;
import com.sun.xml.internal.stream.buffer.stax.StreamReaderBufferCreator;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.internal.ws.api.message.Message;
import org.xml.sax.SAXException;
import javax.xml.stream.Location;
import org.xml.sax.SAXParseException;
import com.sun.xml.internal.ws.util.xml.DummyLocation;
import com.sun.istack.internal.XMLStreamReaderToContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import com.sun.xml.internal.ws.util.xml.XMLStreamReaderToXMLStreamWriter;
import javax.xml.stream.XMLStreamWriter;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.bind.api.Bridge;
import javax.xml.bind.JAXBException;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import com.sun.xml.internal.ws.message.AttachmentUnmarshallerImpl;
import javax.xml.bind.Unmarshaller;
import java.util.Enumeration;
import org.xml.sax.helpers.NamespaceSupport;
import com.sun.xml.internal.ws.util.xml.StAXSource;
import javax.xml.transform.Source;
import com.sun.xml.internal.ws.api.message.HeaderList;
import com.sun.xml.internal.ws.encoding.TagInfoset;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import java.util.Iterator;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.util.xml.XMLReaderComposite;
import java.util.ArrayList;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.istack.internal.NotNull;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.api.message.StreamingSOAP;
import com.sun.xml.internal.ws.message.AbstractMessageImpl;

public class StreamMessage extends AbstractMessageImpl implements StreamingSOAP
{
    @NotNull
    private XMLStreamReader reader;
    @Nullable
    private MessageHeaders headers;
    private String bodyPrologue;
    private String bodyEpilogue;
    private String payloadLocalName;
    private String payloadNamespaceURI;
    private Throwable consumedAt;
    private XMLStreamReader envelopeReader;
    private static final String SOAP_ENVELOPE = "Envelope";
    private static final String SOAP_HEADER = "Header";
    private static final String SOAP_BODY = "Body";
    static final StreamHeaderDecoder SOAP12StreamHeaderDecoder;
    static final StreamHeaderDecoder SOAP11StreamHeaderDecoder;
    
    public StreamMessage(final SOAPVersion v) {
        super(v);
        this.bodyPrologue = null;
        this.bodyEpilogue = null;
        this.payloadLocalName = null;
        this.payloadNamespaceURI = null;
    }
    
    public StreamMessage(final SOAPVersion v, @NotNull final XMLStreamReader envelope, @NotNull final AttachmentSet attachments) {
        super(v);
        this.bodyPrologue = null;
        this.bodyEpilogue = null;
        this.envelopeReader = envelope;
        this.attachmentSet = attachments;
    }
    
    @Override
    public XMLStreamReader readEnvelope() {
        if (this.envelopeReader == null) {
            final List<XMLStreamReader> hReaders = new ArrayList<XMLStreamReader>();
            final XMLReaderComposite.ElemInfo envElem = new XMLReaderComposite.ElemInfo(this.envelopeTag, null);
            final XMLReaderComposite.ElemInfo hdrElem = (this.headerTag != null) ? new XMLReaderComposite.ElemInfo(this.headerTag, envElem) : null;
            final XMLReaderComposite.ElemInfo bdyElem = new XMLReaderComposite.ElemInfo(this.bodyTag, envElem);
            for (final Header h : this.getHeaders().asList()) {
                try {
                    hReaders.add(h.readHeader());
                }
                catch (final XMLStreamException e) {
                    throw new RuntimeException(e);
                }
            }
            final XMLStreamReader soapHeader = (hdrElem != null) ? new XMLReaderComposite(hdrElem, hReaders.toArray(new XMLStreamReader[hReaders.size()])) : null;
            final XMLStreamReader[] payload = { this.readPayload() };
            final XMLStreamReader soapBody = new XMLReaderComposite(bdyElem, payload);
            final XMLStreamReader[] soapContent = (soapHeader != null) ? new XMLStreamReader[] { soapHeader, soapBody } : new XMLStreamReader[] { soapBody };
            return new XMLReaderComposite(envElem, soapContent);
        }
        return this.envelopeReader;
    }
    
    public StreamMessage(@Nullable final MessageHeaders headers, @NotNull final AttachmentSet attachmentSet, @NotNull final XMLStreamReader reader, @NotNull final SOAPVersion soapVersion) {
        super(soapVersion);
        this.bodyPrologue = null;
        this.bodyEpilogue = null;
        this.init(headers, attachmentSet, reader, soapVersion);
    }
    
    private void init(@Nullable final MessageHeaders headers, @NotNull final AttachmentSet attachmentSet, @NotNull final XMLStreamReader reader, @NotNull final SOAPVersion soapVersion) {
        this.headers = headers;
        this.attachmentSet = attachmentSet;
        this.reader = reader;
        if (reader.getEventType() == 7) {
            XMLStreamReaderUtil.nextElementContent(reader);
        }
        if (reader.getEventType() == 2) {
            final String body = reader.getLocalName();
            final String nsUri = reader.getNamespaceURI();
            assert body != null;
            assert nsUri != null;
            if (!body.equals("Body") || !nsUri.equals(soapVersion.nsUri)) {
                throw new WebServiceException("Malformed stream: {" + nsUri + "}" + body);
            }
            this.payloadLocalName = null;
            this.payloadNamespaceURI = null;
        }
        else {
            this.payloadLocalName = reader.getLocalName();
            this.payloadNamespaceURI = reader.getNamespaceURI();
        }
        final int base = soapVersion.ordinal() * 3;
        this.envelopeTag = StreamMessage.DEFAULT_TAGS.get(base);
        this.headerTag = StreamMessage.DEFAULT_TAGS.get(base + 1);
        this.bodyTag = StreamMessage.DEFAULT_TAGS.get(base + 2);
    }
    
    public StreamMessage(@NotNull final TagInfoset envelopeTag, @Nullable final TagInfoset headerTag, @NotNull final AttachmentSet attachmentSet, @Nullable final MessageHeaders headers, @NotNull final TagInfoset bodyTag, @NotNull final XMLStreamReader reader, @NotNull final SOAPVersion soapVersion) {
        this(envelopeTag, headerTag, attachmentSet, headers, null, bodyTag, null, reader, soapVersion);
    }
    
    public StreamMessage(@NotNull final TagInfoset envelopeTag, @Nullable final TagInfoset headerTag, @NotNull final AttachmentSet attachmentSet, @Nullable final MessageHeaders headers, @Nullable final String bodyPrologue, @NotNull final TagInfoset bodyTag, @Nullable final String bodyEpilogue, @NotNull final XMLStreamReader reader, @NotNull final SOAPVersion soapVersion) {
        super(soapVersion);
        this.bodyPrologue = null;
        this.bodyEpilogue = null;
        this.init(envelopeTag, headerTag, attachmentSet, headers, bodyPrologue, bodyTag, bodyEpilogue, reader, soapVersion);
    }
    
    private void init(@NotNull final TagInfoset envelopeTag, @Nullable final TagInfoset headerTag, @NotNull final AttachmentSet attachmentSet, @Nullable final MessageHeaders headers, @Nullable final String bodyPrologue, @NotNull final TagInfoset bodyTag, @Nullable final String bodyEpilogue, @NotNull final XMLStreamReader reader, @NotNull final SOAPVersion soapVersion) {
        this.init(headers, attachmentSet, reader, soapVersion);
        if (envelopeTag == null) {
            throw new IllegalArgumentException("EnvelopeTag TagInfoset cannot be null");
        }
        if (bodyTag == null) {
            throw new IllegalArgumentException("BodyTag TagInfoset cannot be null");
        }
        this.envelopeTag = envelopeTag;
        this.headerTag = headerTag;
        this.bodyTag = bodyTag;
        this.bodyPrologue = bodyPrologue;
        this.bodyEpilogue = bodyEpilogue;
    }
    
    @Override
    public boolean hasHeaders() {
        if (this.envelopeReader != null) {
            readEnvelope(this);
        }
        return this.headers != null && this.headers.hasHeaders();
    }
    
    @Override
    public MessageHeaders getHeaders() {
        if (this.envelopeReader != null) {
            readEnvelope(this);
        }
        if (this.headers == null) {
            this.headers = new HeaderList(this.getSOAPVersion());
        }
        return this.headers;
    }
    
    @Override
    public String getPayloadLocalPart() {
        if (this.envelopeReader != null) {
            readEnvelope(this);
        }
        return this.payloadLocalName;
    }
    
    @Override
    public String getPayloadNamespaceURI() {
        if (this.envelopeReader != null) {
            readEnvelope(this);
        }
        return this.payloadNamespaceURI;
    }
    
    @Override
    public boolean hasPayload() {
        if (this.envelopeReader != null) {
            readEnvelope(this);
        }
        return this.payloadLocalName != null;
    }
    
    @Override
    public Source readPayloadAsSource() {
        if (!this.hasPayload()) {
            return null;
        }
        assert this.unconsumed();
        return new StAXSource(this.reader, true, this.getInscopeNamespaces());
    }
    
    private String[] getInscopeNamespaces() {
        final NamespaceSupport nss = new NamespaceSupport();
        nss.pushContext();
        for (int i = 0; i < this.envelopeTag.ns.length; i += 2) {
            nss.declarePrefix(this.envelopeTag.ns[i], this.envelopeTag.ns[i + 1]);
        }
        nss.pushContext();
        for (int i = 0; i < this.bodyTag.ns.length; i += 2) {
            nss.declarePrefix(this.bodyTag.ns[i], this.bodyTag.ns[i + 1]);
        }
        final List<String> inscope = new ArrayList<String>();
        final Enumeration en = nss.getPrefixes();
        while (en.hasMoreElements()) {
            final String prefix = en.nextElement();
            inscope.add(prefix);
            inscope.add(nss.getURI(prefix));
        }
        return inscope.toArray(new String[inscope.size()]);
    }
    
    @Override
    public Object readPayloadAsJAXB(final Unmarshaller unmarshaller) throws JAXBException {
        if (!this.hasPayload()) {
            return null;
        }
        assert this.unconsumed();
        if (this.hasAttachments()) {
            unmarshaller.setAttachmentUnmarshaller(new AttachmentUnmarshallerImpl(this.getAttachments()));
        }
        try {
            return unmarshaller.unmarshal(this.reader);
        }
        finally {
            unmarshaller.setAttachmentUnmarshaller(null);
            XMLStreamReaderUtil.readRest(this.reader);
            XMLStreamReaderUtil.close(this.reader);
            XMLStreamReaderFactory.recycle(this.reader);
        }
    }
    
    @Override
    @Deprecated
    public <T> T readPayloadAsJAXB(final Bridge<T> bridge) throws JAXBException {
        if (!this.hasPayload()) {
            return null;
        }
        assert this.unconsumed();
        final T r = bridge.unmarshal(this.reader, this.hasAttachments() ? new AttachmentUnmarshallerImpl(this.getAttachments()) : null);
        XMLStreamReaderUtil.readRest(this.reader);
        XMLStreamReaderUtil.close(this.reader);
        XMLStreamReaderFactory.recycle(this.reader);
        return r;
    }
    
    @Override
    public <T> T readPayloadAsJAXB(final XMLBridge<T> bridge) throws JAXBException {
        if (!this.hasPayload()) {
            return null;
        }
        assert this.unconsumed();
        final T r = bridge.unmarshal(this.reader, this.hasAttachments() ? new AttachmentUnmarshallerImpl(this.getAttachments()) : null);
        XMLStreamReaderUtil.readRest(this.reader);
        XMLStreamReaderUtil.close(this.reader);
        XMLStreamReaderFactory.recycle(this.reader);
        return r;
    }
    
    @Override
    public void consume() {
        assert this.unconsumed();
        XMLStreamReaderUtil.readRest(this.reader);
        XMLStreamReaderUtil.close(this.reader);
        XMLStreamReaderFactory.recycle(this.reader);
    }
    
    @Override
    public XMLStreamReader readPayload() {
        if (!this.hasPayload()) {
            return null;
        }
        assert this.unconsumed();
        return this.reader;
    }
    
    @Override
    public void writePayloadTo(final XMLStreamWriter writer) throws XMLStreamException {
        if (this.envelopeReader != null) {
            readEnvelope(this);
        }
        assert this.unconsumed();
        if (this.payloadLocalName == null) {
            return;
        }
        if (this.bodyPrologue != null) {
            writer.writeCharacters(this.bodyPrologue);
        }
        final XMLStreamReaderToXMLStreamWriter conv = new XMLStreamReaderToXMLStreamWriter();
        while (this.reader.getEventType() != 8) {
            final String name = this.reader.getLocalName();
            final String nsUri = this.reader.getNamespaceURI();
            if (this.reader.getEventType() == 2) {
                if (this.isBodyElement(name, nsUri)) {
                    break;
                }
                final String whiteSpaces = XMLStreamReaderUtil.nextWhiteSpaceContent(this.reader);
                if (whiteSpaces == null) {
                    continue;
                }
                writer.writeCharacters(this.bodyEpilogue = whiteSpaces);
            }
            else {
                conv.bridge(this.reader, writer);
            }
        }
        XMLStreamReaderUtil.readRest(this.reader);
        XMLStreamReaderUtil.close(this.reader);
        XMLStreamReaderFactory.recycle(this.reader);
    }
    
    private boolean isBodyElement(final String name, final String nsUri) {
        return name.equals("Body") && nsUri.equals(this.soapVersion.nsUri);
    }
    
    @Override
    public void writeTo(final XMLStreamWriter sw) throws XMLStreamException {
        if (this.envelopeReader != null) {
            readEnvelope(this);
        }
        this.writeEnvelope(sw);
    }
    
    private void writeEnvelope(final XMLStreamWriter writer) throws XMLStreamException {
        if (this.envelopeReader != null) {
            readEnvelope(this);
        }
        writer.writeStartDocument();
        this.envelopeTag.writeStart(writer);
        final MessageHeaders hl = this.getHeaders();
        if (hl.hasHeaders() && this.headerTag == null) {
            this.headerTag = new TagInfoset(this.envelopeTag.nsUri, "Header", this.envelopeTag.prefix, StreamMessage.EMPTY_ATTS, new String[0]);
        }
        if (this.headerTag != null) {
            this.headerTag.writeStart(writer);
            if (hl.hasHeaders()) {
                for (final Header h : hl.asList()) {
                    h.writeTo(writer);
                }
            }
            writer.writeEndElement();
        }
        this.bodyTag.writeStart(writer);
        if (this.hasPayload()) {
            this.writePayloadTo(writer);
        }
        writer.writeEndElement();
        writer.writeEndElement();
        writer.writeEndDocument();
    }
    
    public void writePayloadTo(final ContentHandler contentHandler, final ErrorHandler errorHandler, final boolean fragment) throws SAXException {
        if (this.envelopeReader != null) {
            readEnvelope(this);
        }
        assert this.unconsumed();
        try {
            if (this.payloadLocalName == null) {
                return;
            }
            if (this.bodyPrologue != null) {
                final char[] chars = this.bodyPrologue.toCharArray();
                contentHandler.characters(chars, 0, chars.length);
            }
            final XMLStreamReaderToContentHandler conv = new XMLStreamReaderToContentHandler(this.reader, contentHandler, true, fragment, this.getInscopeNamespaces());
            while (this.reader.getEventType() != 8) {
                final String name = this.reader.getLocalName();
                final String nsUri = this.reader.getNamespaceURI();
                if (this.reader.getEventType() == 2) {
                    if (this.isBodyElement(name, nsUri)) {
                        break;
                    }
                    final String whiteSpaces = XMLStreamReaderUtil.nextWhiteSpaceContent(this.reader);
                    if (whiteSpaces == null) {
                        continue;
                    }
                    this.bodyEpilogue = whiteSpaces;
                    final char[] chars2 = whiteSpaces.toCharArray();
                    contentHandler.characters(chars2, 0, chars2.length);
                }
                else {
                    conv.bridge();
                }
            }
            XMLStreamReaderUtil.readRest(this.reader);
            XMLStreamReaderUtil.close(this.reader);
            XMLStreamReaderFactory.recycle(this.reader);
        }
        catch (final XMLStreamException e) {
            Location loc = e.getLocation();
            if (loc == null) {
                loc = DummyLocation.INSTANCE;
            }
            final SAXParseException x = new SAXParseException(e.getMessage(), loc.getPublicId(), loc.getSystemId(), loc.getLineNumber(), loc.getColumnNumber(), e);
            errorHandler.error(x);
        }
    }
    
    @Override
    public Message copy() {
        if (this.envelopeReader != null) {
            readEnvelope(this);
        }
        try {
            assert this.unconsumed();
            this.consumedAt = null;
            final MutableXMLStreamBuffer xsb = new MutableXMLStreamBuffer();
            final StreamReaderBufferCreator c = new StreamReaderBufferCreator(xsb);
            c.storeElement(this.envelopeTag.nsUri, this.envelopeTag.localName, this.envelopeTag.prefix, this.envelopeTag.ns);
            c.storeElement(this.bodyTag.nsUri, this.bodyTag.localName, this.bodyTag.prefix, this.bodyTag.ns);
            if (this.hasPayload()) {
                while (this.reader.getEventType() != 8) {
                    final String name = this.reader.getLocalName();
                    final String nsUri = this.reader.getNamespaceURI();
                    if (this.isBodyElement(name, nsUri)) {
                        break;
                    }
                    if (this.reader.getEventType() == 8) {
                        break;
                    }
                    c.create(this.reader);
                    if (this.reader.isWhiteSpace()) {
                        this.bodyEpilogue = XMLStreamReaderUtil.currentWhiteSpaceContent(this.reader);
                    }
                    else {
                        this.bodyEpilogue = null;
                    }
                }
            }
            c.storeEndElement();
            c.storeEndElement();
            c.storeEndElement();
            XMLStreamReaderUtil.readRest(this.reader);
            XMLStreamReaderUtil.close(this.reader);
            XMLStreamReaderFactory.recycle(this.reader);
            this.reader = xsb.readAsXMLStreamReader();
            final XMLStreamReader clone = xsb.readAsXMLStreamReader();
            this.proceedToRootElement(this.reader);
            this.proceedToRootElement(clone);
            return new StreamMessage(this.envelopeTag, this.headerTag, this.attachmentSet, HeaderList.copy(this.headers), this.bodyPrologue, this.bodyTag, this.bodyEpilogue, clone, this.soapVersion);
        }
        catch (final XMLStreamException e) {
            throw new WebServiceException("Failed to copy a message", e);
        }
    }
    
    private void proceedToRootElement(final XMLStreamReader xsr) throws XMLStreamException {
        assert xsr.getEventType() == 7;
        xsr.nextTag();
        xsr.nextTag();
        xsr.nextTag();
        assert xsr.getEventType() == 2;
    }
    
    @Override
    public void writeTo(final ContentHandler contentHandler, final ErrorHandler errorHandler) throws SAXException {
        if (this.envelopeReader != null) {
            readEnvelope(this);
        }
        contentHandler.setDocumentLocator(StreamMessage.NULL_LOCATOR);
        contentHandler.startDocument();
        this.envelopeTag.writeStart(contentHandler);
        if (this.hasHeaders() && this.headerTag == null) {
            this.headerTag = new TagInfoset(this.envelopeTag.nsUri, "Header", this.envelopeTag.prefix, StreamMessage.EMPTY_ATTS, new String[0]);
        }
        if (this.headerTag != null) {
            this.headerTag.writeStart(contentHandler);
            if (this.hasHeaders()) {
                final MessageHeaders headers = this.getHeaders();
                for (final Header h : headers.asList()) {
                    h.writeTo(contentHandler, errorHandler);
                }
            }
            this.headerTag.writeEnd(contentHandler);
        }
        this.bodyTag.writeStart(contentHandler);
        this.writePayloadTo(contentHandler, errorHandler, true);
        this.bodyTag.writeEnd(contentHandler);
        this.envelopeTag.writeEnd(contentHandler);
        contentHandler.endDocument();
    }
    
    private boolean unconsumed() {
        if (this.payloadLocalName == null) {
            return true;
        }
        if (this.reader.getEventType() != 1) {
            final AssertionError error = new AssertionError((Object)"StreamMessage has been already consumed. See the nested exception for where it's consumed");
            error.initCause(this.consumedAt);
            throw error;
        }
        this.consumedAt = new Exception().fillInStackTrace();
        return true;
    }
    
    public String getBodyPrologue() {
        if (this.envelopeReader != null) {
            readEnvelope(this);
        }
        return this.bodyPrologue;
    }
    
    public String getBodyEpilogue() {
        if (this.envelopeReader != null) {
            readEnvelope(this);
        }
        return this.bodyEpilogue;
    }
    
    public XMLStreamReader getReader() {
        if (this.envelopeReader != null) {
            readEnvelope(this);
        }
        assert this.unconsumed();
        return this.reader;
    }
    
    private static void readEnvelope(final StreamMessage message) {
        if (message.envelopeReader == null) {
            return;
        }
        final XMLStreamReader reader = message.envelopeReader;
        message.envelopeReader = null;
        final SOAPVersion soapVersion = message.soapVersion;
        if (reader.getEventType() != 1) {
            XMLStreamReaderUtil.nextElementContent(reader);
        }
        XMLStreamReaderUtil.verifyReaderState(reader, 1);
        if ("Envelope".equals(reader.getLocalName()) && !soapVersion.nsUri.equals(reader.getNamespaceURI())) {
            throw new VersionMismatchException(soapVersion, new Object[] { soapVersion.nsUri, reader.getNamespaceURI() });
        }
        XMLStreamReaderUtil.verifyTag(reader, soapVersion.nsUri, "Envelope");
        final TagInfoset envelopeTag = new TagInfoset(reader);
        final Map<String, String> namespaces = new HashMap<String, String>();
        for (int i = 0; i < reader.getNamespaceCount(); ++i) {
            namespaces.put(reader.getNamespacePrefix(i), reader.getNamespaceURI(i));
        }
        XMLStreamReaderUtil.nextElementContent(reader);
        XMLStreamReaderUtil.verifyReaderState(reader, 1);
        HeaderList headers = null;
        TagInfoset headerTag = null;
        if (reader.getLocalName().equals("Header") && reader.getNamespaceURI().equals(soapVersion.nsUri)) {
            headerTag = new TagInfoset(reader);
            for (int j = 0; j < reader.getNamespaceCount(); ++j) {
                namespaces.put(reader.getNamespacePrefix(j), reader.getNamespaceURI(j));
            }
            XMLStreamReaderUtil.nextElementContent(reader);
            if (reader.getEventType() == 1) {
                headers = new HeaderList(soapVersion);
                try {
                    final StreamHeaderDecoder headerDecoder = SOAPVersion.SOAP_11.equals(soapVersion) ? StreamMessage.SOAP11StreamHeaderDecoder : StreamMessage.SOAP12StreamHeaderDecoder;
                    cacheHeaders(reader, namespaces, headers, headerDecoder);
                }
                catch (final XMLStreamException e) {
                    throw new WebServiceException(e);
                }
            }
            XMLStreamReaderUtil.nextElementContent(reader);
        }
        XMLStreamReaderUtil.verifyTag(reader, soapVersion.nsUri, "Body");
        final TagInfoset bodyTag = new TagInfoset(reader);
        final String bodyPrologue = XMLStreamReaderUtil.nextWhiteSpaceContent(reader);
        message.init(envelopeTag, headerTag, message.attachmentSet, headers, bodyPrologue, bodyTag, null, reader, soapVersion);
    }
    
    private static XMLStreamBuffer cacheHeaders(final XMLStreamReader reader, final Map<String, String> namespaces, final HeaderList headers, final StreamHeaderDecoder headerDecoder) throws XMLStreamException {
        final MutableXMLStreamBuffer buffer = createXMLStreamBuffer();
        final StreamReaderBufferCreator creator = new StreamReaderBufferCreator();
        creator.setXMLStreamBuffer(buffer);
        while (reader.getEventType() == 1) {
            Map<String, String> headerBlockNamespaces = namespaces;
            if (reader.getNamespaceCount() > 0) {
                headerBlockNamespaces = new HashMap<String, String>(namespaces);
                for (int i = 0; i < reader.getNamespaceCount(); ++i) {
                    headerBlockNamespaces.put(reader.getNamespacePrefix(i), reader.getNamespaceURI(i));
                }
            }
            final XMLStreamBuffer mark = new XMLStreamBufferMark(headerBlockNamespaces, creator);
            headers.add(headerDecoder.decodeHeader(reader, mark));
            creator.createElementFragment(reader, false);
            if (reader.getEventType() != 1 && reader.getEventType() != 2) {
                XMLStreamReaderUtil.nextElementContent(reader);
            }
        }
        return buffer;
    }
    
    private static MutableXMLStreamBuffer createXMLStreamBuffer() {
        return new MutableXMLStreamBuffer();
    }
    
    static {
        SOAP12StreamHeaderDecoder = new StreamHeaderDecoder() {
            @Override
            public Header decodeHeader(final XMLStreamReader reader, final XMLStreamBuffer mark) {
                return new StreamHeader12(reader, mark);
            }
        };
        SOAP11StreamHeaderDecoder = new StreamHeaderDecoder() {
            @Override
            public Header decodeHeader(final XMLStreamReader reader, final XMLStreamBuffer mark) {
                return new StreamHeader11(reader, mark);
            }
        };
    }
    
    protected interface StreamHeaderDecoder
    {
        Header decodeHeader(final XMLStreamReader p0, final XMLStreamBuffer p1);
    }
}
