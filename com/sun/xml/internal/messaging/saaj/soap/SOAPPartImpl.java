package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.xml.internal.messaging.saaj.util.SAAJUtil;
import javax.xml.soap.SOAPElement;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.DOMConfiguration;
import javax.xml.transform.dom.DOMSource;
import java.io.PushbackReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.io.InputStreamReader;
import com.sun.xml.internal.messaging.saaj.util.XMLDeclarationParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import com.sun.xml.internal.messaging.saaj.util.MimeHeadersUtil;
import org.w3c.dom.NodeList;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Attr;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Text;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;
import javax.activation.DataSource;
import javax.activation.DataHandler;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeBodyPart;
import java.io.OutputStream;
import java.io.Reader;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import java.io.InputStream;
import java.io.IOException;
import javax.xml.transform.sax.SAXSource;
import com.sun.xml.internal.messaging.saaj.util.FastInfosetReflection;
import javax.xml.transform.stream.StreamSource;
import com.sun.xml.internal.messaging.saaj.util.JAXMStreamSource;
import java.util.Iterator;
import org.w3c.dom.Element;
import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.ElementImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.EnvelopeImpl;
import org.w3c.dom.Node;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.transform.Source;
import javax.xml.soap.MimeHeaders;
import java.util.logging.Logger;
import javax.xml.soap.SOAPPart;

public abstract class SOAPPartImpl extends SOAPPart implements SOAPDocument
{
    protected static final Logger log;
    protected MimeHeaders headers;
    protected Envelope envelope;
    protected Source source;
    protected SOAPDocumentImpl document;
    private boolean sourceWasSet;
    protected boolean omitXmlDecl;
    protected String sourceCharsetEncoding;
    protected MessageImpl message;
    static final boolean lazyContentLength;
    
    protected SOAPPartImpl() {
        this(null);
    }
    
    protected SOAPPartImpl(final MessageImpl message) {
        this.sourceWasSet = false;
        this.omitXmlDecl = true;
        this.sourceCharsetEncoding = null;
        this.document = new SOAPDocumentImpl(this);
        this.headers = new MimeHeaders();
        this.message = message;
        this.headers.setHeader("Content-Type", this.getContentType());
    }
    
    protected abstract String getContentType();
    
    protected abstract Envelope createEnvelopeFromSource() throws SOAPException;
    
    protected abstract Envelope createEmptyEnvelope(final String p0) throws SOAPException;
    
    protected abstract SOAPPartImpl duplicateType();
    
    protected String getContentTypeString() {
        return this.getContentType();
    }
    
    public boolean isFastInfoset() {
        return this.message != null && this.message.isFastInfoset();
    }
    
    @Override
    public SOAPEnvelope getEnvelope() throws SOAPException {
        if (this.sourceWasSet) {
            this.sourceWasSet = false;
        }
        this.lookForEnvelope();
        if (this.envelope != null) {
            if (this.source != null) {
                this.document.removeChild(this.envelope);
                this.envelope = this.createEnvelopeFromSource();
            }
        }
        else if (this.source != null) {
            this.envelope = this.createEnvelopeFromSource();
        }
        else {
            this.envelope = this.createEmptyEnvelope(null);
            this.document.insertBefore(this.envelope, null);
        }
        return this.envelope;
    }
    
    protected void lookForEnvelope() throws SOAPException {
        final Element envelopeChildElement = this.document.doGetDocumentElement();
        if (envelopeChildElement == null || envelopeChildElement instanceof Envelope) {
            this.envelope = (EnvelopeImpl)envelopeChildElement;
        }
        else {
            if (!(envelopeChildElement instanceof ElementImpl)) {
                SOAPPartImpl.log.severe("SAAJ0512.soap.incorrect.factory.used");
                throw new SOAPExceptionImpl("Unable to create envelope: incorrect factory used during tree construction");
            }
            final ElementImpl soapElement = (ElementImpl)envelopeChildElement;
            if (!soapElement.getLocalName().equalsIgnoreCase("Envelope")) {
                SOAPPartImpl.log.severe("SAAJ0514.soap.root.elem.not.named.envelope");
                throw new SOAPExceptionImpl("Unable to create envelope from given source because the root element is not named \"Envelope\"");
            }
            final String prefix = soapElement.getPrefix();
            final String uri = (prefix == null) ? soapElement.getNamespaceURI() : soapElement.getNamespaceURI(prefix);
            if (!uri.equals("http://schemas.xmlsoap.org/soap/envelope/") && !uri.equals("http://www.w3.org/2003/05/soap-envelope")) {
                SOAPPartImpl.log.severe("SAAJ0513.soap.unknown.ns");
                throw new SOAPVersionMismatchException("Unable to create envelope from given source because the namespace was not recognized");
            }
        }
    }
    
    @Override
    public void removeAllMimeHeaders() {
        this.headers.removeAllHeaders();
    }
    
    @Override
    public void removeMimeHeader(final String header) {
        this.headers.removeHeader(header);
    }
    
    @Override
    public String[] getMimeHeader(final String name) {
        return this.headers.getHeader(name);
    }
    
    @Override
    public void setMimeHeader(final String name, final String value) {
        this.headers.setHeader(name, value);
    }
    
    @Override
    public void addMimeHeader(final String name, final String value) {
        this.headers.addHeader(name, value);
    }
    
    @Override
    public Iterator getAllMimeHeaders() {
        return this.headers.getAllHeaders();
    }
    
    @Override
    public Iterator getMatchingMimeHeaders(final String[] names) {
        return this.headers.getMatchingHeaders(names);
    }
    
    @Override
    public Iterator getNonMatchingMimeHeaders(final String[] names) {
        return this.headers.getNonMatchingHeaders(names);
    }
    
    @Override
    public Source getContent() throws SOAPException {
        if (this.source != null) {
            InputStream bis = null;
            if (this.source instanceof JAXMStreamSource) {
                final StreamSource streamSource = (StreamSource)this.source;
                bis = streamSource.getInputStream();
            }
            else if (FastInfosetReflection.isFastInfosetSource(this.source)) {
                final SAXSource saxSource = (SAXSource)this.source;
                bis = saxSource.getInputSource().getByteStream();
            }
            if (bis != null) {
                try {
                    bis.reset();
                }
                catch (final IOException ex) {}
            }
            return this.source;
        }
        return ((Envelope)this.getEnvelope()).getContent();
    }
    
    @Override
    public void setContent(final Source source) throws SOAPException {
        try {
            if (source instanceof StreamSource) {
                final InputStream is = ((StreamSource)source).getInputStream();
                final Reader rdr = ((StreamSource)source).getReader();
                if (is != null) {
                    this.source = new JAXMStreamSource(is);
                }
                else {
                    if (rdr == null) {
                        SOAPPartImpl.log.severe("SAAJ0544.soap.no.valid.reader.for.src");
                        throw new SOAPExceptionImpl("Source does not have a valid Reader or InputStream");
                    }
                    this.source = new JAXMStreamSource(rdr);
                }
            }
            else if (FastInfosetReflection.isFastInfosetSource(source)) {
                final InputStream is = FastInfosetReflection.FastInfosetSource_getInputStream(source);
                if (!(is instanceof ByteInputStream)) {
                    final ByteOutputStream bout = new ByteOutputStream();
                    bout.write(is);
                    FastInfosetReflection.FastInfosetSource_setInputStream(source, bout.newInputStream());
                }
                this.source = source;
            }
            else {
                this.source = source;
            }
            this.sourceWasSet = true;
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            SOAPPartImpl.log.severe("SAAJ0545.soap.cannot.set.src.for.part");
            throw new SOAPExceptionImpl("Error setting the source for SOAPPart: " + ex.getMessage());
        }
    }
    
    public InputStream getContentAsStream() throws IOException {
        if (this.source != null) {
            InputStream is = null;
            if (this.source instanceof StreamSource && !this.isFastInfoset()) {
                is = ((StreamSource)this.source).getInputStream();
            }
            else if (FastInfosetReflection.isFastInfosetSource(this.source) && this.isFastInfoset()) {
                try {
                    is = FastInfosetReflection.FastInfosetSource_getInputStream(this.source);
                }
                catch (final Exception e) {
                    throw new IOException(e.toString());
                }
            }
            if (is != null) {
                if (SOAPPartImpl.lazyContentLength) {
                    return is;
                }
                if (!(is instanceof ByteInputStream)) {
                    SOAPPartImpl.log.severe("SAAJ0546.soap.stream.incorrect.type");
                    throw new IOException("Internal error: stream not of the right type");
                }
                return is;
            }
        }
        final ByteOutputStream b = new ByteOutputStream();
        Envelope env = null;
        try {
            env = (Envelope)this.getEnvelope();
            env.output(b, this.isFastInfoset());
        }
        catch (final SOAPException soapException) {
            SOAPPartImpl.log.severe("SAAJ0547.soap.cannot.externalize");
            throw new SOAPIOException("SOAP exception while trying to externalize: ", soapException);
        }
        return b.newInputStream();
    }
    
    MimeBodyPart getMimePart() throws SOAPException {
        try {
            final MimeBodyPart headerEnvelope = new MimeBodyPart();
            headerEnvelope.setDataHandler(this.getDataHandler());
            AttachmentPartImpl.copyMimeHeaders(this.headers, headerEnvelope);
            return headerEnvelope;
        }
        catch (final SOAPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            SOAPPartImpl.log.severe("SAAJ0548.soap.cannot.externalize.hdr");
            throw new SOAPExceptionImpl("Unable to externalize header", ex2);
        }
    }
    
    MimeHeaders getMimeHeaders() {
        return this.headers;
    }
    
    DataHandler getDataHandler() {
        final DataSource ds = new DataSource() {
            @Override
            public OutputStream getOutputStream() throws IOException {
                throw new IOException("Illegal Operation");
            }
            
            @Override
            public String getContentType() {
                return SOAPPartImpl.this.getContentTypeString();
            }
            
            @Override
            public String getName() {
                return SOAPPartImpl.this.getContentId();
            }
            
            @Override
            public InputStream getInputStream() throws IOException {
                return SOAPPartImpl.this.getContentAsStream();
            }
        };
        return new DataHandler(ds);
    }
    
    @Override
    public SOAPDocumentImpl getDocument() {
        this.handleNewSource();
        return this.document;
    }
    
    @Override
    public SOAPPartImpl getSOAPPart() {
        return this;
    }
    
    @Override
    public DocumentType getDoctype() {
        return this.document.getDoctype();
    }
    
    @Override
    public DOMImplementation getImplementation() {
        return this.document.getImplementation();
    }
    
    @Override
    public Element getDocumentElement() {
        try {
            this.getEnvelope();
        }
        catch (final SOAPException ex) {}
        return this.document.getDocumentElement();
    }
    
    protected void doGetDocumentElement() {
        this.handleNewSource();
        try {
            this.lookForEnvelope();
        }
        catch (final SOAPException ex) {}
    }
    
    @Override
    public Element createElement(final String tagName) throws DOMException {
        return this.document.createElement(tagName);
    }
    
    @Override
    public DocumentFragment createDocumentFragment() {
        return this.document.createDocumentFragment();
    }
    
    @Override
    public Text createTextNode(final String data) {
        return this.document.createTextNode(data);
    }
    
    @Override
    public Comment createComment(final String data) {
        return this.document.createComment(data);
    }
    
    @Override
    public CDATASection createCDATASection(final String data) throws DOMException {
        return this.document.createCDATASection(data);
    }
    
    @Override
    public ProcessingInstruction createProcessingInstruction(final String target, final String data) throws DOMException {
        return this.document.createProcessingInstruction(target, data);
    }
    
    @Override
    public Attr createAttribute(final String name) throws DOMException {
        return this.document.createAttribute(name);
    }
    
    @Override
    public EntityReference createEntityReference(final String name) throws DOMException {
        return this.document.createEntityReference(name);
    }
    
    @Override
    public NodeList getElementsByTagName(final String tagname) {
        this.handleNewSource();
        return this.document.getElementsByTagName(tagname);
    }
    
    @Override
    public Node importNode(final Node importedNode, final boolean deep) throws DOMException {
        this.handleNewSource();
        return this.document.importNode(importedNode, deep);
    }
    
    @Override
    public Element createElementNS(final String namespaceURI, final String qualifiedName) throws DOMException {
        return this.document.createElementNS(namespaceURI, qualifiedName);
    }
    
    @Override
    public Attr createAttributeNS(final String namespaceURI, final String qualifiedName) throws DOMException {
        return this.document.createAttributeNS(namespaceURI, qualifiedName);
    }
    
    @Override
    public NodeList getElementsByTagNameNS(final String namespaceURI, final String localName) {
        this.handleNewSource();
        return this.document.getElementsByTagNameNS(namespaceURI, localName);
    }
    
    @Override
    public Element getElementById(final String elementId) {
        this.handleNewSource();
        return this.document.getElementById(elementId);
    }
    
    @Override
    public Node appendChild(final Node newChild) throws DOMException {
        this.handleNewSource();
        return this.document.appendChild(newChild);
    }
    
    @Override
    public Node cloneNode(final boolean deep) {
        this.handleNewSource();
        return this.document.cloneNode(deep);
    }
    
    protected SOAPPartImpl doCloneNode() {
        this.handleNewSource();
        final SOAPPartImpl newSoapPart = this.duplicateType();
        newSoapPart.headers = MimeHeadersUtil.copy(this.headers);
        newSoapPart.source = this.source;
        return newSoapPart;
    }
    
    @Override
    public NamedNodeMap getAttributes() {
        return this.document.getAttributes();
    }
    
    @Override
    public NodeList getChildNodes() {
        this.handleNewSource();
        return this.document.getChildNodes();
    }
    
    @Override
    public Node getFirstChild() {
        this.handleNewSource();
        return this.document.getFirstChild();
    }
    
    @Override
    public Node getLastChild() {
        this.handleNewSource();
        return this.document.getLastChild();
    }
    
    @Override
    public String getLocalName() {
        return this.document.getLocalName();
    }
    
    @Override
    public String getNamespaceURI() {
        return this.document.getNamespaceURI();
    }
    
    @Override
    public Node getNextSibling() {
        this.handleNewSource();
        return this.document.getNextSibling();
    }
    
    @Override
    public String getNodeName() {
        return this.document.getNodeName();
    }
    
    @Override
    public short getNodeType() {
        return this.document.getNodeType();
    }
    
    @Override
    public String getNodeValue() throws DOMException {
        return this.document.getNodeValue();
    }
    
    @Override
    public Document getOwnerDocument() {
        return this.document.getOwnerDocument();
    }
    
    @Override
    public Node getParentNode() {
        return this.document.getParentNode();
    }
    
    @Override
    public String getPrefix() {
        return this.document.getPrefix();
    }
    
    @Override
    public Node getPreviousSibling() {
        return this.document.getPreviousSibling();
    }
    
    @Override
    public boolean hasAttributes() {
        return this.document.hasAttributes();
    }
    
    @Override
    public boolean hasChildNodes() {
        this.handleNewSource();
        return this.document.hasChildNodes();
    }
    
    @Override
    public Node insertBefore(final Node arg0, final Node arg1) throws DOMException {
        this.handleNewSource();
        return this.document.insertBefore(arg0, arg1);
    }
    
    @Override
    public boolean isSupported(final String arg0, final String arg1) {
        return this.document.isSupported(arg0, arg1);
    }
    
    @Override
    public void normalize() {
        this.handleNewSource();
        this.document.normalize();
    }
    
    @Override
    public Node removeChild(final Node arg0) throws DOMException {
        this.handleNewSource();
        return this.document.removeChild(arg0);
    }
    
    @Override
    public Node replaceChild(final Node arg0, final Node arg1) throws DOMException {
        this.handleNewSource();
        return this.document.replaceChild(arg0, arg1);
    }
    
    @Override
    public void setNodeValue(final String arg0) throws DOMException {
        this.document.setNodeValue(arg0);
    }
    
    @Override
    public void setPrefix(final String arg0) throws DOMException {
        this.document.setPrefix(arg0);
    }
    
    private void handleNewSource() {
        if (this.sourceWasSet) {
            try {
                this.getEnvelope();
            }
            catch (final SOAPException ex) {}
        }
    }
    
    protected XMLDeclarationParser lookForXmlDecl() throws SOAPException {
        if (this.source != null && this.source instanceof StreamSource) {
            Reader reader = null;
            final InputStream inputStream = ((StreamSource)this.source).getInputStream();
            Label_0134: {
                if (inputStream != null) {
                    if (this.getSourceCharsetEncoding() == null) {
                        reader = new InputStreamReader(inputStream);
                        break Label_0134;
                    }
                    try {
                        reader = new InputStreamReader(inputStream, this.getSourceCharsetEncoding());
                        break Label_0134;
                    }
                    catch (final UnsupportedEncodingException uee) {
                        SOAPPartImpl.log.log(Level.SEVERE, "SAAJ0551.soap.unsupported.encoding", new Object[] { this.getSourceCharsetEncoding() });
                        throw new SOAPExceptionImpl("Unsupported encoding " + this.getSourceCharsetEncoding(), uee);
                    }
                }
                reader = ((StreamSource)this.source).getReader();
            }
            if (reader != null) {
                final PushbackReader pushbackReader = new PushbackReader(reader, 4096);
                final XMLDeclarationParser ev = new XMLDeclarationParser(pushbackReader);
                try {
                    ev.parse();
                }
                catch (final Exception e) {
                    SOAPPartImpl.log.log(Level.SEVERE, "SAAJ0552.soap.xml.decl.parsing.failed");
                    throw new SOAPExceptionImpl("XML declaration parsing failed", e);
                }
                final String xmlDecl = ev.getXmlDeclaration();
                if (xmlDecl != null && xmlDecl.length() > 0) {
                    this.omitXmlDecl = false;
                }
                if (SOAPPartImpl.lazyContentLength) {
                    this.source = new StreamSource(pushbackReader);
                }
                return ev;
            }
        }
        else if (this.source == null || this.source instanceof DOMSource) {}
        return null;
    }
    
    public void setSourceCharsetEncoding(final String charset) {
        this.sourceCharsetEncoding = charset;
    }
    
    @Override
    public Node renameNode(final Node n, final String namespaceURI, final String qualifiedName) throws DOMException {
        this.handleNewSource();
        return this.document.renameNode(n, namespaceURI, qualifiedName);
    }
    
    @Override
    public void normalizeDocument() {
        this.document.normalizeDocument();
    }
    
    @Override
    public DOMConfiguration getDomConfig() {
        return this.document.getDomConfig();
    }
    
    @Override
    public Node adoptNode(final Node source) throws DOMException {
        this.handleNewSource();
        return this.document.adoptNode(source);
    }
    
    @Override
    public void setDocumentURI(final String documentURI) {
        this.document.setDocumentURI(documentURI);
    }
    
    @Override
    public String getDocumentURI() {
        return this.document.getDocumentURI();
    }
    
    @Override
    public void setStrictErrorChecking(final boolean strictErrorChecking) {
        this.document.setStrictErrorChecking(strictErrorChecking);
    }
    
    @Override
    public String getInputEncoding() {
        return this.document.getInputEncoding();
    }
    
    @Override
    public String getXmlEncoding() {
        return this.document.getXmlEncoding();
    }
    
    @Override
    public boolean getXmlStandalone() {
        return this.document.getXmlStandalone();
    }
    
    @Override
    public void setXmlStandalone(final boolean xmlStandalone) throws DOMException {
        this.document.setXmlStandalone(xmlStandalone);
    }
    
    @Override
    public String getXmlVersion() {
        return this.document.getXmlVersion();
    }
    
    @Override
    public void setXmlVersion(final String xmlVersion) throws DOMException {
        this.document.setXmlVersion(xmlVersion);
    }
    
    @Override
    public boolean getStrictErrorChecking() {
        return this.document.getStrictErrorChecking();
    }
    
    @Override
    public String getBaseURI() {
        return this.document.getBaseURI();
    }
    
    @Override
    public short compareDocumentPosition(final Node other) throws DOMException {
        return this.document.compareDocumentPosition(other);
    }
    
    @Override
    public String getTextContent() throws DOMException {
        return this.document.getTextContent();
    }
    
    @Override
    public void setTextContent(final String textContent) throws DOMException {
        this.document.setTextContent(textContent);
    }
    
    @Override
    public boolean isSameNode(final Node other) {
        return this.document.isSameNode(other);
    }
    
    @Override
    public String lookupPrefix(final String namespaceURI) {
        return this.document.lookupPrefix(namespaceURI);
    }
    
    @Override
    public boolean isDefaultNamespace(final String namespaceURI) {
        return this.document.isDefaultNamespace(namespaceURI);
    }
    
    @Override
    public String lookupNamespaceURI(final String prefix) {
        return this.document.lookupNamespaceURI(prefix);
    }
    
    @Override
    public boolean isEqualNode(final Node arg) {
        return this.document.isEqualNode(arg);
    }
    
    @Override
    public Object getFeature(final String feature, final String version) {
        return this.document.getFeature(feature, version);
    }
    
    @Override
    public Object setUserData(final String key, final Object data, final UserDataHandler handler) {
        return this.document.setUserData(key, data, handler);
    }
    
    @Override
    public Object getUserData(final String key) {
        return this.document.getUserData(key);
    }
    
    @Override
    public void recycleNode() {
    }
    
    @Override
    public String getValue() {
        return null;
    }
    
    @Override
    public void setValue(final String value) {
        SOAPPartImpl.log.severe("SAAJ0571.soappart.setValue.not.defined");
        throw new IllegalStateException("Setting value of a soap part is not defined");
    }
    
    @Override
    public void setParentElement(final SOAPElement parent) throws SOAPException {
        SOAPPartImpl.log.severe("SAAJ0570.soappart.parent.element.not.defined");
        throw new SOAPExceptionImpl("The parent element of a soap part is not defined");
    }
    
    @Override
    public SOAPElement getParentElement() {
        return null;
    }
    
    @Override
    public void detachNode() {
    }
    
    public String getSourceCharsetEncoding() {
        return this.sourceCharsetEncoding;
    }
    
    static {
        log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap", "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");
        lazyContentLength = SAAJUtil.getSystemBoolean("saaj.lazy.contentlength");
    }
}
