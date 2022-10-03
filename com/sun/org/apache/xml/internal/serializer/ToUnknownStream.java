package com.sun.org.apache.xml.internal.serializer;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.Transformer;
import org.xml.sax.Locator;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;
import java.io.Writer;
import java.io.OutputStream;
import java.util.Properties;
import java.io.IOException;
import org.xml.sax.ContentHandler;
import java.util.Vector;

public final class ToUnknownStream extends SerializerBase
{
    private SerializationHandler m_handler;
    private static final String EMPTYSTRING = "";
    private boolean m_wrapped_handler_not_initialized;
    private String m_firstElementPrefix;
    private String m_firstElementName;
    private String m_firstElementURI;
    private String m_firstElementLocalName;
    private boolean m_firstTagNotEmitted;
    private Vector m_namespaceURI;
    private Vector m_namespacePrefix;
    private boolean m_needToCallStartDocument;
    private boolean m_setVersion_called;
    private boolean m_setDoctypeSystem_called;
    private boolean m_setDoctypePublic_called;
    private boolean m_setMediaType_called;
    
    public ToUnknownStream() {
        this.m_wrapped_handler_not_initialized = false;
        this.m_firstElementLocalName = null;
        this.m_firstTagNotEmitted = true;
        this.m_namespaceURI = null;
        this.m_namespacePrefix = null;
        this.m_needToCallStartDocument = false;
        this.m_setVersion_called = false;
        this.m_setDoctypeSystem_called = false;
        this.m_setDoctypePublic_called = false;
        this.m_setMediaType_called = false;
        this.m_handler = new ToXMLStream();
    }
    
    @Override
    public ContentHandler asContentHandler() throws IOException {
        return this;
    }
    
    @Override
    public void close() {
        this.m_handler.close();
    }
    
    @Override
    public Properties getOutputFormat() {
        return this.m_handler.getOutputFormat();
    }
    
    @Override
    public OutputStream getOutputStream() {
        return this.m_handler.getOutputStream();
    }
    
    @Override
    public Writer getWriter() {
        return this.m_handler.getWriter();
    }
    
    @Override
    public boolean reset() {
        return this.m_handler.reset();
    }
    
    @Override
    public void serialize(final Node node) throws IOException {
        if (this.m_firstTagNotEmitted) {
            this.flush();
        }
        this.m_handler.serialize(node);
    }
    
    @Override
    public boolean setEscaping(final boolean escape) throws SAXException {
        return this.m_handler.setEscaping(escape);
    }
    
    @Override
    public void setOutputFormat(final Properties format) {
        this.m_handler.setOutputFormat(format);
    }
    
    @Override
    public void setOutputStream(final OutputStream output) {
        this.m_handler.setOutputStream(output);
    }
    
    @Override
    public void setWriter(final Writer writer) {
        this.m_handler.setWriter(writer);
    }
    
    @Override
    public void addAttribute(final String uri, final String localName, final String rawName, final String type, final String value) throws SAXException {
        this.addAttribute(uri, localName, rawName, type, value, false);
    }
    
    @Override
    public void addAttribute(final String uri, final String localName, final String rawName, final String type, final String value, final boolean XSLAttribute) throws SAXException {
        if (this.m_firstTagNotEmitted) {
            this.flush();
        }
        this.m_handler.addAttribute(uri, localName, rawName, type, value, XSLAttribute);
    }
    
    @Override
    public void addAttribute(final String rawName, final String value) {
        if (this.m_firstTagNotEmitted) {
            this.flush();
        }
        this.m_handler.addAttribute(rawName, value);
    }
    
    @Override
    public void addUniqueAttribute(final String rawName, final String value, final int flags) throws SAXException {
        if (this.m_firstTagNotEmitted) {
            this.flush();
        }
        this.m_handler.addUniqueAttribute(rawName, value, flags);
    }
    
    @Override
    public void characters(final String chars) throws SAXException {
        final int length = chars.length();
        if (length > this.m_charsBuff.length) {
            this.m_charsBuff = new char[length * 2 + 1];
        }
        chars.getChars(0, length, this.m_charsBuff, 0);
        this.characters(this.m_charsBuff, 0, length);
    }
    
    @Override
    public void endElement(final String elementName) throws SAXException {
        if (this.m_firstTagNotEmitted) {
            this.flush();
        }
        this.m_handler.endElement(elementName);
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        this.startPrefixMapping(prefix, uri, true);
    }
    
    @Override
    public void namespaceAfterStartElement(final String prefix, final String uri) throws SAXException {
        if (this.m_firstTagNotEmitted && this.m_firstElementURI == null && this.m_firstElementName != null) {
            final String prefix2 = SerializerBase.getPrefixPart(this.m_firstElementName);
            if (prefix2 == null && "".equals(prefix)) {
                this.m_firstElementURI = uri;
            }
        }
        this.startPrefixMapping(prefix, uri, false);
    }
    
    @Override
    public boolean startPrefixMapping(final String prefix, final String uri, final boolean shouldFlush) throws SAXException {
        boolean pushed = false;
        if (this.m_firstTagNotEmitted) {
            if (this.m_firstElementName != null && shouldFlush) {
                this.flush();
                pushed = this.m_handler.startPrefixMapping(prefix, uri, shouldFlush);
            }
            else {
                if (this.m_namespacePrefix == null) {
                    this.m_namespacePrefix = new Vector();
                    this.m_namespaceURI = new Vector();
                }
                this.m_namespacePrefix.addElement(prefix);
                this.m_namespaceURI.addElement(uri);
                if (this.m_firstElementURI == null && prefix.equals(this.m_firstElementPrefix)) {
                    this.m_firstElementURI = uri;
                }
            }
        }
        else {
            pushed = this.m_handler.startPrefixMapping(prefix, uri, shouldFlush);
        }
        return pushed;
    }
    
    @Override
    public void setVersion(final String version) {
        this.m_handler.setVersion(version);
        this.m_setVersion_called = true;
    }
    
    @Override
    public void startDocument() throws SAXException {
        this.m_needToCallStartDocument = true;
    }
    
    @Override
    public void startElement(final String qName) throws SAXException {
        this.startElement(null, null, qName, null);
    }
    
    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
        this.startElement(namespaceURI, localName, qName, null);
    }
    
    @Override
    public void startElement(final String namespaceURI, final String localName, final String elementName, final Attributes atts) throws SAXException {
        if (this.m_needToCallSetDocumentInfo) {
            super.setDocumentInfo();
            this.m_needToCallSetDocumentInfo = false;
        }
        if (this.m_firstTagNotEmitted) {
            if (this.m_firstElementName != null) {
                this.flush();
                this.m_handler.startElement(namespaceURI, localName, elementName, atts);
            }
            else {
                this.m_wrapped_handler_not_initialized = true;
                this.m_firstElementName = elementName;
                this.m_firstElementPrefix = this.getPrefixPartUnknown(elementName);
                this.m_firstElementURI = namespaceURI;
                this.m_firstElementLocalName = localName;
                if (this.m_tracer != null) {
                    this.firePseudoElement(elementName);
                }
                if (atts != null) {
                    super.addAttributes(atts);
                }
                if (atts != null) {
                    this.flush();
                }
            }
        }
        else {
            this.m_handler.startElement(namespaceURI, localName, elementName, atts);
        }
    }
    
    @Override
    public void comment(final String comment) throws SAXException {
        if (this.m_firstTagNotEmitted && this.m_firstElementName != null) {
            this.emitFirstTag();
        }
        else if (this.m_needToCallStartDocument) {
            this.m_handler.startDocument();
            this.m_needToCallStartDocument = false;
        }
        this.m_handler.comment(comment);
    }
    
    @Override
    public String getDoctypePublic() {
        return this.m_handler.getDoctypePublic();
    }
    
    @Override
    public String getDoctypeSystem() {
        return this.m_handler.getDoctypeSystem();
    }
    
    @Override
    public String getEncoding() {
        return this.m_handler.getEncoding();
    }
    
    @Override
    public boolean getIndent() {
        return this.m_handler.getIndent();
    }
    
    @Override
    public int getIndentAmount() {
        return this.m_handler.getIndentAmount();
    }
    
    @Override
    public String getMediaType() {
        return this.m_handler.getMediaType();
    }
    
    @Override
    public boolean getOmitXMLDeclaration() {
        return this.m_handler.getOmitXMLDeclaration();
    }
    
    @Override
    public String getStandalone() {
        return this.m_handler.getStandalone();
    }
    
    @Override
    public String getVersion() {
        return this.m_handler.getVersion();
    }
    
    @Override
    public void setDoctype(final String system, final String pub) {
        this.m_handler.setDoctypePublic(pub);
        this.m_handler.setDoctypeSystem(system);
    }
    
    @Override
    public void setDoctypePublic(final String doctype) {
        this.m_handler.setDoctypePublic(doctype);
        this.m_setDoctypePublic_called = true;
    }
    
    @Override
    public void setDoctypeSystem(final String doctype) {
        this.m_handler.setDoctypeSystem(doctype);
        this.m_setDoctypeSystem_called = true;
    }
    
    @Override
    public void setEncoding(final String encoding) {
        this.m_handler.setEncoding(encoding);
    }
    
    @Override
    public void setIndent(final boolean indent) {
        this.m_handler.setIndent(indent);
    }
    
    @Override
    public void setIndentAmount(final int value) {
        this.m_handler.setIndentAmount(value);
    }
    
    @Override
    public void setMediaType(final String mediaType) {
        this.m_handler.setMediaType(mediaType);
        this.m_setMediaType_called = true;
    }
    
    @Override
    public void setOmitXMLDeclaration(final boolean b) {
        this.m_handler.setOmitXMLDeclaration(b);
    }
    
    @Override
    public void setStandalone(final String standalone) {
        this.m_handler.setStandalone(standalone);
    }
    
    @Override
    public void attributeDecl(final String arg0, final String arg1, final String arg2, final String arg3, final String arg4) throws SAXException {
        this.m_handler.attributeDecl(arg0, arg1, arg2, arg3, arg4);
    }
    
    @Override
    public void elementDecl(final String arg0, final String arg1) throws SAXException {
        if (this.m_firstTagNotEmitted) {
            this.emitFirstTag();
        }
        this.m_handler.elementDecl(arg0, arg1);
    }
    
    @Override
    public void externalEntityDecl(final String name, final String publicId, final String systemId) throws SAXException {
        if (this.m_firstTagNotEmitted) {
            this.flush();
        }
        this.m_handler.externalEntityDecl(name, publicId, systemId);
    }
    
    @Override
    public void internalEntityDecl(final String arg0, final String arg1) throws SAXException {
        if (this.m_firstTagNotEmitted) {
            this.flush();
        }
        this.m_handler.internalEntityDecl(arg0, arg1);
    }
    
    @Override
    public void characters(final char[] characters, final int offset, final int length) throws SAXException {
        if (this.m_firstTagNotEmitted) {
            this.flush();
        }
        this.m_handler.characters(characters, offset, length);
    }
    
    @Override
    public void endDocument() throws SAXException {
        if (this.m_firstTagNotEmitted) {
            this.flush();
        }
        this.m_handler.endDocument();
    }
    
    @Override
    public void endElement(String namespaceURI, String localName, final String qName) throws SAXException {
        if (this.m_firstTagNotEmitted) {
            this.flush();
            if (namespaceURI == null && this.m_firstElementURI != null) {
                namespaceURI = this.m_firstElementURI;
            }
            if (localName == null && this.m_firstElementLocalName != null) {
                localName = this.m_firstElementLocalName;
            }
        }
        this.m_handler.endElement(namespaceURI, localName, qName);
    }
    
    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
        this.m_handler.endPrefixMapping(prefix);
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        if (this.m_firstTagNotEmitted) {
            this.flush();
        }
        this.m_handler.ignorableWhitespace(ch, start, length);
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        if (this.m_firstTagNotEmitted) {
            this.flush();
        }
        this.m_handler.processingInstruction(target, data);
    }
    
    @Override
    public void setDocumentLocator(final Locator locator) {
        super.setDocumentLocator(locator);
        this.m_handler.setDocumentLocator(locator);
    }
    
    @Override
    public void skippedEntity(final String name) throws SAXException {
        this.m_handler.skippedEntity(name);
    }
    
    @Override
    public void comment(final char[] ch, final int start, final int length) throws SAXException {
        if (this.m_firstTagNotEmitted) {
            this.flush();
        }
        this.m_handler.comment(ch, start, length);
    }
    
    @Override
    public void endCDATA() throws SAXException {
        this.m_handler.endCDATA();
    }
    
    @Override
    public void endDTD() throws SAXException {
        this.m_handler.endDTD();
    }
    
    @Override
    public void endEntity(final String name) throws SAXException {
        if (this.m_firstTagNotEmitted) {
            this.emitFirstTag();
        }
        this.m_handler.endEntity(name);
    }
    
    @Override
    public void startCDATA() throws SAXException {
        this.m_handler.startCDATA();
    }
    
    @Override
    public void startDTD(final String name, final String publicId, final String systemId) throws SAXException {
        this.m_handler.startDTD(name, publicId, systemId);
    }
    
    @Override
    public void startEntity(final String name) throws SAXException {
        this.m_handler.startEntity(name);
    }
    
    private void initStreamOutput() throws SAXException {
        final boolean firstElementIsHTML = this.isFirstElemHTML();
        if (firstElementIsHTML) {
            final SerializationHandler oldHandler = this.m_handler;
            final Properties htmlProperties = OutputPropertiesFactory.getDefaultMethodProperties("html");
            final Serializer serializer = SerializerFactory.getSerializer(htmlProperties);
            this.m_handler = (SerializationHandler)serializer;
            final Writer writer = oldHandler.getWriter();
            if (null != writer) {
                this.m_handler.setWriter(writer);
            }
            else {
                final OutputStream os = oldHandler.getOutputStream();
                if (null != os) {
                    this.m_handler.setOutputStream(os);
                }
            }
            this.m_handler.setVersion(oldHandler.getVersion());
            this.m_handler.setDoctypeSystem(oldHandler.getDoctypeSystem());
            this.m_handler.setDoctypePublic(oldHandler.getDoctypePublic());
            this.m_handler.setMediaType(oldHandler.getMediaType());
            this.m_handler.setTransformer(oldHandler.getTransformer());
        }
        if (this.m_needToCallStartDocument) {
            this.m_handler.startDocument();
            this.m_needToCallStartDocument = false;
        }
        this.m_wrapped_handler_not_initialized = false;
    }
    
    private void emitFirstTag() throws SAXException {
        if (this.m_firstElementName != null) {
            if (this.m_wrapped_handler_not_initialized) {
                this.initStreamOutput();
                this.m_wrapped_handler_not_initialized = false;
            }
            this.m_handler.startElement(this.m_firstElementURI, null, this.m_firstElementName, this.m_attributes);
            this.m_attributes = null;
            if (this.m_namespacePrefix != null) {
                for (int n = this.m_namespacePrefix.size(), i = 0; i < n; ++i) {
                    final String prefix = this.m_namespacePrefix.elementAt(i);
                    final String uri = this.m_namespaceURI.elementAt(i);
                    this.m_handler.startPrefixMapping(prefix, uri, false);
                }
                this.m_namespacePrefix = null;
                this.m_namespaceURI = null;
            }
            this.m_firstTagNotEmitted = false;
        }
    }
    
    private String getLocalNameUnknown(String value) {
        int idx = value.lastIndexOf(58);
        if (idx >= 0) {
            value = value.substring(idx + 1);
        }
        idx = value.lastIndexOf(64);
        if (idx >= 0) {
            value = value.substring(idx + 1);
        }
        return value;
    }
    
    private String getPrefixPartUnknown(final String qname) {
        final int index = qname.indexOf(58);
        return (index > 0) ? qname.substring(0, index) : "";
    }
    
    private boolean isFirstElemHTML() {
        boolean isHTML = this.getLocalNameUnknown(this.m_firstElementName).equalsIgnoreCase("html");
        if (isHTML && this.m_firstElementURI != null && !"".equals(this.m_firstElementURI)) {
            isHTML = false;
        }
        if (isHTML && this.m_namespacePrefix != null) {
            for (int max = this.m_namespacePrefix.size(), i = 0; i < max; ++i) {
                final String prefix = this.m_namespacePrefix.elementAt(i);
                final String uri = this.m_namespaceURI.elementAt(i);
                if (this.m_firstElementPrefix != null && this.m_firstElementPrefix.equals(prefix) && !"".equals(uri)) {
                    isHTML = false;
                    break;
                }
            }
        }
        return isHTML;
    }
    
    @Override
    public DOMSerializer asDOMSerializer() throws IOException {
        return this.m_handler.asDOMSerializer();
    }
    
    @Override
    public void setCdataSectionElements(final Vector URI_and_localNames) {
        this.m_handler.setCdataSectionElements(URI_and_localNames);
    }
    
    @Override
    public void addAttributes(final Attributes atts) throws SAXException {
        this.m_handler.addAttributes(atts);
    }
    
    @Override
    public NamespaceMappings getNamespaceMappings() {
        NamespaceMappings mappings = null;
        if (this.m_handler != null) {
            mappings = this.m_handler.getNamespaceMappings();
        }
        return mappings;
    }
    
    @Override
    public void flushPending() throws SAXException {
        this.flush();
        this.m_handler.flushPending();
    }
    
    private void flush() {
        try {
            if (this.m_firstTagNotEmitted) {
                this.emitFirstTag();
            }
            if (this.m_needToCallStartDocument) {
                this.m_handler.startDocument();
                this.m_needToCallStartDocument = false;
            }
        }
        catch (final SAXException e) {
            throw new RuntimeException(e.toString());
        }
    }
    
    @Override
    public String getPrefix(final String namespaceURI) {
        return this.m_handler.getPrefix(namespaceURI);
    }
    
    @Override
    public void entityReference(final String entityName) throws SAXException {
        this.m_handler.entityReference(entityName);
    }
    
    @Override
    public String getNamespaceURI(final String qname, final boolean isElement) {
        return this.m_handler.getNamespaceURI(qname, isElement);
    }
    
    @Override
    public String getNamespaceURIFromPrefix(final String prefix) {
        return this.m_handler.getNamespaceURIFromPrefix(prefix);
    }
    
    @Override
    public void setTransformer(final Transformer t) {
        this.m_handler.setTransformer(t);
        if (t instanceof SerializerTrace && ((SerializerTrace)t).hasTraceListeners()) {
            this.m_tracer = (SerializerTrace)t;
        }
        else {
            this.m_tracer = null;
        }
    }
    
    @Override
    public Transformer getTransformer() {
        return this.m_handler.getTransformer();
    }
    
    @Override
    public void setContentHandler(final ContentHandler ch) {
        this.m_handler.setContentHandler(ch);
    }
    
    @Override
    public void setSourceLocator(final SourceLocator locator) {
        this.m_handler.setSourceLocator(locator);
    }
    
    protected void firePseudoElement(final String elementName) {
        if (this.m_tracer != null) {
            final StringBuffer sb = new StringBuffer();
            sb.append('<');
            sb.append(elementName);
            final char[] ch = sb.toString().toCharArray();
            this.m_tracer.fireGenerateEvent(11, ch, 0, ch.length);
        }
    }
}
