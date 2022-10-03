package com.sun.xml.internal.ws.message.saaj;

import java.util.HashMap;
import java.util.Map;
import javax.xml.soap.MimeHeader;
import java.io.IOException;
import com.sun.xml.internal.ws.util.ASCIIUtility;
import java.io.OutputStream;
import java.io.InputStream;
import javax.xml.transform.stream.StreamSource;
import javax.activation.DataHandler;
import com.sun.xml.internal.ws.streaming.DOMStreamReader;
import com.sun.istack.internal.FragmentContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.w3c.dom.Document;
import com.sun.xml.internal.bind.unmarshaller.DOMScanner;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import com.sun.istack.internal.XMLStreamException2;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.bind.api.Bridge;
import javax.xml.bind.JAXBException;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import com.sun.xml.internal.ws.message.AttachmentUnmarshallerImpl;
import javax.xml.bind.Unmarshaller;
import com.sun.xml.internal.ws.api.message.AttachmentEx;
import javax.xml.soap.AttachmentPart;
import com.sun.xml.internal.ws.api.message.Attachment;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.Source;
import org.w3c.dom.Attr;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.NotNull;
import org.w3c.dom.Node;
import com.sun.xml.internal.ws.util.DOMUtil;
import java.util.Iterator;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPException;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.api.message.Header;
import javax.xml.soap.SOAPHeaderElement;
import com.sun.xml.internal.ws.api.message.HeaderList;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import javax.xml.soap.SOAPElement;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.AttributesImpl;
import org.w3c.dom.NamedNodeMap;
import com.sun.xml.internal.ws.api.SOAPVersion;
import org.w3c.dom.Element;
import java.util.List;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import javax.xml.soap.SOAPMessage;
import com.sun.xml.internal.ws.api.message.Message;

public class SAAJMessage extends Message
{
    private boolean parsedMessage;
    private boolean accessedMessage;
    private final SOAPMessage sm;
    private MessageHeaders headers;
    private List<Element> bodyParts;
    private Element payload;
    private String payloadLocalName;
    private String payloadNamespace;
    private SOAPVersion soapVersion;
    private NamedNodeMap bodyAttrs;
    private NamedNodeMap headerAttrs;
    private NamedNodeMap envelopeAttrs;
    private static final AttributesImpl EMPTY_ATTS;
    private static final LocatorImpl NULL_LOCATOR;
    private XMLStreamReader soapBodyFirstChildReader;
    private SOAPElement soapBodyFirstChild;
    
    public SAAJMessage(final SOAPMessage sm) {
        this.sm = sm;
    }
    
    private SAAJMessage(MessageHeaders headers, final AttachmentSet as, final SOAPMessage sm, final SOAPVersion version) {
        this.sm = sm;
        this.parse();
        if (headers == null) {
            headers = new HeaderList(version);
        }
        this.headers = headers;
        this.attachmentSet = as;
    }
    
    private void parse() {
        if (!this.parsedMessage) {
            try {
                this.access();
                if (this.headers == null) {
                    this.headers = new HeaderList(this.getSOAPVersion());
                }
                final SOAPHeader header = this.sm.getSOAPHeader();
                if (header != null) {
                    this.headerAttrs = header.getAttributes();
                    final Iterator iter = header.examineAllHeaderElements();
                    while (iter.hasNext()) {
                        this.headers.add(new SAAJHeader(iter.next()));
                    }
                }
                this.attachmentSet = new SAAJAttachmentSet(this.sm);
                this.parsedMessage = true;
            }
            catch (final SOAPException e) {
                throw new WebServiceException(e);
            }
        }
    }
    
    protected void access() {
        if (!this.accessedMessage) {
            try {
                this.envelopeAttrs = this.sm.getSOAPPart().getEnvelope().getAttributes();
                final Node body = this.sm.getSOAPBody();
                this.bodyAttrs = body.getAttributes();
                this.soapVersion = SOAPVersion.fromNsUri(body.getNamespaceURI());
                this.bodyParts = DOMUtil.getChildElements(body);
                this.payload = ((this.bodyParts.size() > 0) ? this.bodyParts.get(0) : null);
                if (this.payload != null) {
                    this.payloadLocalName = this.payload.getLocalName();
                    this.payloadNamespace = this.payload.getNamespaceURI();
                }
                this.accessedMessage = true;
            }
            catch (final SOAPException e) {
                throw new WebServiceException(e);
            }
        }
    }
    
    @Override
    public boolean hasHeaders() {
        this.parse();
        return this.headers.hasHeaders();
    }
    
    @NotNull
    @Override
    public MessageHeaders getHeaders() {
        this.parse();
        return this.headers;
    }
    
    @NotNull
    @Override
    public AttachmentSet getAttachments() {
        if (this.attachmentSet == null) {
            this.attachmentSet = new SAAJAttachmentSet(this.sm);
        }
        return this.attachmentSet;
    }
    
    @Override
    protected boolean hasAttachments() {
        return !this.getAttachments().isEmpty();
    }
    
    @Nullable
    @Override
    public String getPayloadLocalPart() {
        this.soapBodyFirstChild();
        return this.payloadLocalName;
    }
    
    @Override
    public String getPayloadNamespaceURI() {
        this.soapBodyFirstChild();
        return this.payloadNamespace;
    }
    
    @Override
    public boolean hasPayload() {
        return this.soapBodyFirstChild() != null;
    }
    
    private void addAttributes(final Element e, final NamedNodeMap attrs) {
        if (attrs == null) {
            return;
        }
        final String elPrefix = e.getPrefix();
        for (int i = 0; i < attrs.getLength(); ++i) {
            final Attr a = (Attr)attrs.item(i);
            if ("xmlns".equals(a.getPrefix()) || "xmlns".equals(a.getLocalName())) {
                if (elPrefix != null || !a.getLocalName().equals("xmlns")) {
                    if (elPrefix == null || !"xmlns".equals(a.getPrefix()) || !elPrefix.equals(a.getLocalName())) {
                        e.setAttributeNS(a.getNamespaceURI(), a.getName(), a.getValue());
                    }
                }
            }
            else {
                e.setAttributeNS(a.getNamespaceURI(), a.getName(), a.getValue());
            }
        }
    }
    
    @Override
    public Source readEnvelopeAsSource() {
        try {
            if (!this.parsedMessage) {
                final SOAPEnvelope se = this.sm.getSOAPPart().getEnvelope();
                return new DOMSource(se);
            }
            final SOAPMessage msg = this.soapVersion.getMessageFactory().createMessage();
            this.addAttributes(msg.getSOAPPart().getEnvelope(), this.envelopeAttrs);
            final SOAPBody newBody = msg.getSOAPPart().getEnvelope().getBody();
            this.addAttributes(newBody, this.bodyAttrs);
            for (final Element part : this.bodyParts) {
                final Node n = newBody.getOwnerDocument().importNode(part, true);
                newBody.appendChild(n);
            }
            this.addAttributes(msg.getSOAPHeader(), this.headerAttrs);
            for (final Header header : this.headers.asList()) {
                header.writeTo(msg);
            }
            final SOAPEnvelope se2 = msg.getSOAPPart().getEnvelope();
            return new DOMSource(se2);
        }
        catch (final SOAPException e) {
            throw new WebServiceException(e);
        }
    }
    
    @Override
    public SOAPMessage readAsSOAPMessage() throws SOAPException {
        if (!this.parsedMessage) {
            return this.sm;
        }
        final SOAPMessage msg = this.soapVersion.getMessageFactory().createMessage();
        this.addAttributes(msg.getSOAPPart().getEnvelope(), this.envelopeAttrs);
        final SOAPBody newBody = msg.getSOAPPart().getEnvelope().getBody();
        this.addAttributes(newBody, this.bodyAttrs);
        for (final Element part : this.bodyParts) {
            final Node n = newBody.getOwnerDocument().importNode(part, true);
            newBody.appendChild(n);
        }
        this.addAttributes(msg.getSOAPHeader(), this.headerAttrs);
        for (final Header header : this.headers.asList()) {
            header.writeTo(msg);
        }
        for (final Attachment att : this.getAttachments()) {
            final AttachmentPart part2 = msg.createAttachmentPart();
            part2.setDataHandler(att.asDataHandler());
            part2.setContentId('<' + att.getContentId() + '>');
            this.addCustomMimeHeaders(att, part2);
            msg.addAttachmentPart(part2);
        }
        msg.saveChanges();
        return msg;
    }
    
    private void addCustomMimeHeaders(final Attachment att, final AttachmentPart part) {
        if (att instanceof AttachmentEx) {
            final Iterator<AttachmentEx.MimeHeader> allMimeHeaders = ((AttachmentEx)att).getMimeHeaders();
            while (allMimeHeaders.hasNext()) {
                final AttachmentEx.MimeHeader mh = allMimeHeaders.next();
                final String name = mh.getName();
                if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Id".equalsIgnoreCase(name)) {
                    part.addMimeHeader(name, mh.getValue());
                }
            }
        }
    }
    
    @Override
    public Source readPayloadAsSource() {
        this.access();
        return (this.payload != null) ? new DOMSource(this.payload) : null;
    }
    
    @Override
    public <T> T readPayloadAsJAXB(final Unmarshaller unmarshaller) throws JAXBException {
        this.access();
        if (this.payload != null) {
            if (this.hasAttachments()) {
                unmarshaller.setAttachmentUnmarshaller(new AttachmentUnmarshallerImpl(this.getAttachments()));
            }
            return (T)unmarshaller.unmarshal(this.payload);
        }
        return null;
    }
    
    @Override
    @Deprecated
    public <T> T readPayloadAsJAXB(final Bridge<T> bridge) throws JAXBException {
        this.access();
        if (this.payload != null) {
            return bridge.unmarshal(this.payload, this.hasAttachments() ? new AttachmentUnmarshallerImpl(this.getAttachments()) : null);
        }
        return null;
    }
    
    @Override
    public <T> T readPayloadAsJAXB(final XMLBridge<T> bridge) throws JAXBException {
        this.access();
        if (this.payload != null) {
            return bridge.unmarshal(this.payload, this.hasAttachments() ? new AttachmentUnmarshallerImpl(this.getAttachments()) : null);
        }
        return null;
    }
    
    @Override
    public XMLStreamReader readPayload() throws XMLStreamException {
        return this.soapBodyFirstChildReader();
    }
    
    @Override
    public void writePayloadTo(final XMLStreamWriter sw) throws XMLStreamException {
        this.access();
        try {
            for (final Element part : this.bodyParts) {
                DOMUtil.serializeNode(part, sw);
            }
        }
        catch (final XMLStreamException e) {
            throw new WebServiceException(e);
        }
    }
    
    @Override
    public void writeTo(final XMLStreamWriter writer) throws XMLStreamException {
        try {
            writer.writeStartDocument();
            if (!this.parsedMessage) {
                DOMUtil.serializeNode(this.sm.getSOAPPart().getEnvelope(), writer);
            }
            else {
                final SOAPEnvelope env = this.sm.getSOAPPart().getEnvelope();
                DOMUtil.writeTagWithAttributes(env, writer);
                if (this.hasHeaders()) {
                    if (env.getHeader() != null) {
                        DOMUtil.writeTagWithAttributes(env.getHeader(), writer);
                    }
                    else {
                        writer.writeStartElement(env.getPrefix(), "Header", env.getNamespaceURI());
                    }
                    for (final Header h : this.headers.asList()) {
                        h.writeTo(writer);
                    }
                    writer.writeEndElement();
                }
                DOMUtil.serializeNode(this.sm.getSOAPBody(), writer);
                writer.writeEndElement();
            }
            writer.writeEndDocument();
            writer.flush();
        }
        catch (final SOAPException ex) {
            throw new XMLStreamException2(ex);
        }
    }
    
    @Override
    public void writeTo(final ContentHandler contentHandler, final ErrorHandler errorHandler) throws SAXException {
        final String soapNsUri = this.soapVersion.nsUri;
        if (!this.parsedMessage) {
            final DOMScanner ds = new DOMScanner();
            ds.setContentHandler(contentHandler);
            ds.scan(this.sm.getSOAPPart());
        }
        else {
            contentHandler.setDocumentLocator(SAAJMessage.NULL_LOCATOR);
            contentHandler.startDocument();
            contentHandler.startPrefixMapping("S", soapNsUri);
            this.startPrefixMapping(contentHandler, this.envelopeAttrs, "S");
            contentHandler.startElement(soapNsUri, "Envelope", "S:Envelope", this.getAttributes(this.envelopeAttrs));
            if (this.hasHeaders()) {
                this.startPrefixMapping(contentHandler, this.headerAttrs, "S");
                contentHandler.startElement(soapNsUri, "Header", "S:Header", this.getAttributes(this.headerAttrs));
                final MessageHeaders headers = this.getHeaders();
                for (final Header h : headers.asList()) {
                    h.writeTo(contentHandler, errorHandler);
                }
                this.endPrefixMapping(contentHandler, this.headerAttrs, "S");
                contentHandler.endElement(soapNsUri, "Header", "S:Header");
            }
            this.startPrefixMapping(contentHandler, this.bodyAttrs, "S");
            contentHandler.startElement(soapNsUri, "Body", "S:Body", this.getAttributes(this.bodyAttrs));
            this.writePayloadTo(contentHandler, errorHandler, true);
            this.endPrefixMapping(contentHandler, this.bodyAttrs, "S");
            contentHandler.endElement(soapNsUri, "Body", "S:Body");
            this.endPrefixMapping(contentHandler, this.envelopeAttrs, "S");
            contentHandler.endElement(soapNsUri, "Envelope", "S:Envelope");
        }
    }
    
    private AttributesImpl getAttributes(final NamedNodeMap attrs) {
        final AttributesImpl atts = new AttributesImpl();
        if (attrs == null) {
            return SAAJMessage.EMPTY_ATTS;
        }
        for (int i = 0; i < attrs.getLength(); ++i) {
            final Attr a = (Attr)attrs.item(i);
            if (!"xmlns".equals(a.getPrefix())) {
                if (!"xmlns".equals(a.getLocalName())) {
                    atts.addAttribute(fixNull(a.getNamespaceURI()), a.getLocalName(), a.getName(), a.getSchemaTypeInfo().getTypeName(), a.getValue());
                }
            }
        }
        return atts;
    }
    
    private void startPrefixMapping(final ContentHandler contentHandler, final NamedNodeMap attrs, final String excludePrefix) throws SAXException {
        if (attrs == null) {
            return;
        }
        for (int i = 0; i < attrs.getLength(); ++i) {
            final Attr a = (Attr)attrs.item(i);
            if (("xmlns".equals(a.getPrefix()) || "xmlns".equals(a.getLocalName())) && !fixNull(a.getPrefix()).equals(excludePrefix)) {
                contentHandler.startPrefixMapping(fixNull(a.getPrefix()), a.getNamespaceURI());
            }
        }
    }
    
    private void endPrefixMapping(final ContentHandler contentHandler, final NamedNodeMap attrs, final String excludePrefix) throws SAXException {
        if (attrs == null) {
            return;
        }
        for (int i = 0; i < attrs.getLength(); ++i) {
            final Attr a = (Attr)attrs.item(i);
            if (("xmlns".equals(a.getPrefix()) || "xmlns".equals(a.getLocalName())) && !fixNull(a.getPrefix()).equals(excludePrefix)) {
                contentHandler.endPrefixMapping(fixNull(a.getPrefix()));
            }
        }
    }
    
    private static String fixNull(final String s) {
        if (s == null) {
            return "";
        }
        return s;
    }
    
    private void writePayloadTo(ContentHandler contentHandler, final ErrorHandler errorHandler, final boolean fragment) throws SAXException {
        if (fragment) {
            contentHandler = new FragmentContentHandler(contentHandler);
        }
        final DOMScanner ds = new DOMScanner();
        ds.setContentHandler(contentHandler);
        ds.scan(this.payload);
    }
    
    @Override
    public Message copy() {
        try {
            if (!this.parsedMessage) {
                return new SAAJMessage(this.readAsSOAPMessage());
            }
            final SOAPMessage msg = this.soapVersion.getMessageFactory().createMessage();
            final SOAPBody newBody = msg.getSOAPPart().getEnvelope().getBody();
            for (final Element part : this.bodyParts) {
                final Node n = newBody.getOwnerDocument().importNode(part, true);
                newBody.appendChild(n);
            }
            this.addAttributes(newBody, this.bodyAttrs);
            return new SAAJMessage(this.getHeaders(), this.getAttachments(), msg, this.soapVersion);
        }
        catch (final SOAPException e) {
            throw new WebServiceException(e);
        }
    }
    
    @Override
    public SOAPVersion getSOAPVersion() {
        return this.soapVersion;
    }
    
    protected XMLStreamReader getXMLStreamReader(final SOAPElement soapElement) {
        return null;
    }
    
    protected XMLStreamReader createXMLStreamReader(final SOAPElement soapElement) {
        final DOMStreamReader dss = new DOMStreamReader();
        dss.setCurrentNode(soapElement);
        return dss;
    }
    
    protected XMLStreamReader soapBodyFirstChildReader() {
        if (this.soapBodyFirstChildReader != null) {
            return this.soapBodyFirstChildReader;
        }
        this.soapBodyFirstChild();
        if (this.soapBodyFirstChild != null) {
            this.soapBodyFirstChildReader = this.getXMLStreamReader(this.soapBodyFirstChild);
            if (this.soapBodyFirstChildReader == null) {
                this.soapBodyFirstChildReader = this.createXMLStreamReader(this.soapBodyFirstChild);
            }
            if (this.soapBodyFirstChildReader.getEventType() == 7) {
                try {
                    while (this.soapBodyFirstChildReader.getEventType() != 1) {
                        this.soapBodyFirstChildReader.next();
                    }
                }
                catch (final XMLStreamException e) {
                    throw new RuntimeException(e);
                }
            }
            return this.soapBodyFirstChildReader;
        }
        this.payloadLocalName = null;
        this.payloadNamespace = null;
        return null;
    }
    
    SOAPElement soapBodyFirstChild() {
        if (this.soapBodyFirstChild != null) {
            return this.soapBodyFirstChild;
        }
        try {
            boolean foundElement = false;
            for (Node n = this.sm.getSOAPBody().getFirstChild(); n != null && !foundElement; n = n.getNextSibling()) {
                if (n.getNodeType() == 1) {
                    foundElement = true;
                    if (n instanceof SOAPElement) {
                        this.soapBodyFirstChild = (SOAPElement)n;
                        this.payloadLocalName = this.soapBodyFirstChild.getLocalName();
                        this.payloadNamespace = this.soapBodyFirstChild.getNamespaceURI();
                        return this.soapBodyFirstChild;
                    }
                }
            }
            if (foundElement) {
                final Iterator i = this.sm.getSOAPBody().getChildElements();
                while (i.hasNext()) {
                    final Object o = i.next();
                    if (o instanceof SOAPElement) {
                        this.soapBodyFirstChild = (SOAPElement)o;
                        this.payloadLocalName = this.soapBodyFirstChild.getLocalName();
                        this.payloadNamespace = this.soapBodyFirstChild.getNamespaceURI();
                        return this.soapBodyFirstChild;
                    }
                }
            }
        }
        catch (final SOAPException e) {
            throw new RuntimeException(e);
        }
        return this.soapBodyFirstChild;
    }
    
    static {
        EMPTY_ATTS = new AttributesImpl();
        NULL_LOCATOR = new LocatorImpl();
    }
    
    protected static class SAAJAttachment implements AttachmentEx
    {
        final AttachmentPart ap;
        String contentIdNoAngleBracket;
        
        public SAAJAttachment(final AttachmentPart part) {
            this.ap = part;
        }
        
        @Override
        public String getContentId() {
            if (this.contentIdNoAngleBracket == null) {
                this.contentIdNoAngleBracket = this.ap.getContentId();
                if (this.contentIdNoAngleBracket != null && this.contentIdNoAngleBracket.charAt(0) == '<') {
                    this.contentIdNoAngleBracket = this.contentIdNoAngleBracket.substring(1, this.contentIdNoAngleBracket.length() - 1);
                }
            }
            return this.contentIdNoAngleBracket;
        }
        
        @Override
        public String getContentType() {
            return this.ap.getContentType();
        }
        
        @Override
        public byte[] asByteArray() {
            try {
                return this.ap.getRawContentBytes();
            }
            catch (final SOAPException e) {
                throw new WebServiceException(e);
            }
        }
        
        @Override
        public DataHandler asDataHandler() {
            try {
                return this.ap.getDataHandler();
            }
            catch (final SOAPException e) {
                throw new WebServiceException(e);
            }
        }
        
        @Override
        public Source asSource() {
            try {
                return new StreamSource(this.ap.getRawContent());
            }
            catch (final SOAPException e) {
                throw new WebServiceException(e);
            }
        }
        
        @Override
        public InputStream asInputStream() {
            try {
                return this.ap.getRawContent();
            }
            catch (final SOAPException e) {
                throw new WebServiceException(e);
            }
        }
        
        @Override
        public void writeTo(final OutputStream os) throws IOException {
            try {
                ASCIIUtility.copyStream(this.ap.getRawContent(), os);
            }
            catch (final SOAPException e) {
                throw new WebServiceException(e);
            }
        }
        
        @Override
        public void writeTo(final SOAPMessage saaj) {
            saaj.addAttachmentPart(this.ap);
        }
        
        AttachmentPart asAttachmentPart() {
            return this.ap;
        }
        
        @Override
        public Iterator<MimeHeader> getMimeHeaders() {
            final Iterator it = this.ap.getAllMimeHeaders();
            return new Iterator<MimeHeader>() {
                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }
                
                @Override
                public MimeHeader next() {
                    final javax.xml.soap.MimeHeader mh = it.next();
                    return new MimeHeader() {
                        @Override
                        public String getName() {
                            return mh.getName();
                        }
                        
                        @Override
                        public String getValue() {
                            return mh.getValue();
                        }
                    };
                }
                
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }
    
    protected static class SAAJAttachmentSet implements AttachmentSet
    {
        private Map<String, Attachment> attMap;
        private Iterator attIter;
        
        public SAAJAttachmentSet(final SOAPMessage sm) {
            this.attIter = sm.getAttachments();
        }
        
        @Override
        public Attachment get(final String contentId) {
            if (this.attMap == null) {
                if (!this.attIter.hasNext()) {
                    return null;
                }
                this.attMap = this.createAttachmentMap();
            }
            if (contentId.charAt(0) != '<') {
                return this.attMap.get('<' + contentId + '>');
            }
            return this.attMap.get(contentId);
        }
        
        @Override
        public boolean isEmpty() {
            if (this.attMap != null) {
                return this.attMap.isEmpty();
            }
            return !this.attIter.hasNext();
        }
        
        @Override
        public Iterator<Attachment> iterator() {
            if (this.attMap == null) {
                this.attMap = this.createAttachmentMap();
            }
            return this.attMap.values().iterator();
        }
        
        private Map<String, Attachment> createAttachmentMap() {
            final HashMap<String, Attachment> map = new HashMap<String, Attachment>();
            while (this.attIter.hasNext()) {
                final AttachmentPart ap = this.attIter.next();
                map.put(ap.getContentId(), new SAAJAttachment(ap));
            }
            return map;
        }
        
        @Override
        public void add(final Attachment att) {
            this.attMap.put('<' + att.getContentId() + '>', att);
        }
    }
}
