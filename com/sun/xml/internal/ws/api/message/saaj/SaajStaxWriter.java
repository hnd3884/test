package com.sun.xml.internal.ws.api.message.saaj;

import java.util.LinkedList;
import java.util.List;
import javax.xml.namespace.QName;
import java.util.NoSuchElementException;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import java.util.Arrays;
import org.w3c.dom.Comment;
import org.w3c.dom.Node;
import javax.xml.stream.XMLStreamException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamWriter;

public class SaajStaxWriter implements XMLStreamWriter
{
    protected SOAPMessage soap;
    protected String envURI;
    protected SOAPElement currentElement;
    protected DeferredElement deferredElement;
    protected static final String Envelope = "Envelope";
    protected static final String Header = "Header";
    protected static final String Body = "Body";
    protected static final String xmlns = "xmlns";
    
    public SaajStaxWriter(final SOAPMessage msg) throws SOAPException {
        this.soap = msg;
        this.currentElement = this.soap.getSOAPPart().getEnvelope();
        this.envURI = this.currentElement.getNamespaceURI();
        this.deferredElement = new DeferredElement();
    }
    
    public SOAPMessage getSOAPMessage() {
        return this.soap;
    }
    
    @Override
    public void writeStartElement(final String localName) throws XMLStreamException {
        this.currentElement = this.deferredElement.flushTo(this.currentElement);
        this.deferredElement.setLocalName(localName);
    }
    
    @Override
    public void writeStartElement(final String ns, final String ln) throws XMLStreamException {
        this.writeStartElement(null, ln, ns);
    }
    
    @Override
    public void writeStartElement(final String prefix, final String ln, final String ns) throws XMLStreamException {
        this.currentElement = this.deferredElement.flushTo(this.currentElement);
        if (this.envURI.equals(ns)) {
            try {
                if ("Envelope".equals(ln)) {
                    this.currentElement = this.soap.getSOAPPart().getEnvelope();
                    this.fixPrefix(prefix);
                    return;
                }
                if ("Header".equals(ln)) {
                    this.currentElement = this.soap.getSOAPHeader();
                    this.fixPrefix(prefix);
                    return;
                }
                if ("Body".equals(ln)) {
                    this.currentElement = this.soap.getSOAPBody();
                    this.fixPrefix(prefix);
                    return;
                }
            }
            catch (final SOAPException e) {
                throw new XMLStreamException(e);
            }
        }
        this.deferredElement.setLocalName(ln);
        this.deferredElement.setNamespaceUri(ns);
        this.deferredElement.setPrefix(prefix);
    }
    
    private void fixPrefix(final String prfx) throws XMLStreamException {
        final String oldPrfx = this.currentElement.getPrefix();
        if (prfx != null && !prfx.equals(oldPrfx)) {
            this.currentElement.setPrefix(prfx);
        }
    }
    
    @Override
    public void writeEmptyElement(final String uri, final String ln) throws XMLStreamException {
        this.writeStartElement(null, ln, uri);
    }
    
    @Override
    public void writeEmptyElement(final String prefix, final String ln, final String uri) throws XMLStreamException {
        this.writeStartElement(prefix, ln, uri);
    }
    
    @Override
    public void writeEmptyElement(final String ln) throws XMLStreamException {
        this.writeStartElement(null, ln, null);
    }
    
    @Override
    public void writeEndElement() throws XMLStreamException {
        this.currentElement = this.deferredElement.flushTo(this.currentElement);
        if (this.currentElement != null) {
            this.currentElement = this.currentElement.getParentElement();
        }
    }
    
    @Override
    public void writeEndDocument() throws XMLStreamException {
        this.currentElement = this.deferredElement.flushTo(this.currentElement);
    }
    
    @Override
    public void close() throws XMLStreamException {
    }
    
    @Override
    public void flush() throws XMLStreamException {
    }
    
    @Override
    public void writeAttribute(final String ln, final String val) throws XMLStreamException {
        this.writeAttribute(null, null, ln, val);
    }
    
    @Override
    public void writeAttribute(final String prefix, final String ns, final String ln, final String value) throws XMLStreamException {
        if (ns == null && prefix == null && "xmlns".equals(ln)) {
            this.writeNamespace("", value);
        }
        else if (this.deferredElement.isInitialized()) {
            this.deferredElement.addAttribute(prefix, ns, ln, value);
        }
        else {
            addAttibuteToElement(this.currentElement, prefix, ns, ln, value);
        }
    }
    
    @Override
    public void writeAttribute(final String ns, final String ln, final String val) throws XMLStreamException {
        this.writeAttribute(null, ns, ln, val);
    }
    
    @Override
    public void writeNamespace(final String prefix, final String uri) throws XMLStreamException {
        final String thePrefix = (prefix == null || "xmlns".equals(prefix)) ? "" : prefix;
        if (this.deferredElement.isInitialized()) {
            this.deferredElement.addNamespaceDeclaration(thePrefix, uri);
        }
        else {
            try {
                this.currentElement.addNamespaceDeclaration(thePrefix, uri);
            }
            catch (final SOAPException e) {
                throw new XMLStreamException(e);
            }
        }
    }
    
    @Override
    public void writeDefaultNamespace(final String uri) throws XMLStreamException {
        this.writeNamespace("", uri);
    }
    
    @Override
    public void writeComment(final String data) throws XMLStreamException {
        this.currentElement = this.deferredElement.flushTo(this.currentElement);
        final Comment c = this.soap.getSOAPPart().createComment(data);
        this.currentElement.appendChild(c);
    }
    
    @Override
    public void writeProcessingInstruction(final String target) throws XMLStreamException {
        this.currentElement = this.deferredElement.flushTo(this.currentElement);
        final Node n = this.soap.getSOAPPart().createProcessingInstruction(target, "");
        this.currentElement.appendChild(n);
    }
    
    @Override
    public void writeProcessingInstruction(final String target, final String data) throws XMLStreamException {
        this.currentElement = this.deferredElement.flushTo(this.currentElement);
        final Node n = this.soap.getSOAPPart().createProcessingInstruction(target, data);
        this.currentElement.appendChild(n);
    }
    
    @Override
    public void writeCData(final String data) throws XMLStreamException {
        this.currentElement = this.deferredElement.flushTo(this.currentElement);
        final Node n = this.soap.getSOAPPart().createCDATASection(data);
        this.currentElement.appendChild(n);
    }
    
    @Override
    public void writeDTD(final String dtd) throws XMLStreamException {
        this.currentElement = this.deferredElement.flushTo(this.currentElement);
    }
    
    @Override
    public void writeEntityRef(final String name) throws XMLStreamException {
        this.currentElement = this.deferredElement.flushTo(this.currentElement);
        final Node n = this.soap.getSOAPPart().createEntityReference(name);
        this.currentElement.appendChild(n);
    }
    
    @Override
    public void writeStartDocument() throws XMLStreamException {
    }
    
    @Override
    public void writeStartDocument(final String version) throws XMLStreamException {
        if (version != null) {
            this.soap.getSOAPPart().setXmlVersion(version);
        }
    }
    
    @Override
    public void writeStartDocument(final String encoding, final String version) throws XMLStreamException {
        if (version != null) {
            this.soap.getSOAPPart().setXmlVersion(version);
        }
        if (encoding != null) {
            try {
                this.soap.setProperty("javax.xml.soap.character-set-encoding", encoding);
            }
            catch (final SOAPException e) {
                throw new XMLStreamException(e);
            }
        }
    }
    
    @Override
    public void writeCharacters(final String text) throws XMLStreamException {
        this.currentElement = this.deferredElement.flushTo(this.currentElement);
        try {
            this.currentElement.addTextNode(text);
        }
        catch (final SOAPException e) {
            throw new XMLStreamException(e);
        }
    }
    
    @Override
    public void writeCharacters(final char[] text, final int start, final int len) throws XMLStreamException {
        this.currentElement = this.deferredElement.flushTo(this.currentElement);
        final char[] chr = (start == 0 && len == text.length) ? text : Arrays.copyOfRange(text, start, start + len);
        try {
            this.currentElement.addTextNode(new String(chr));
        }
        catch (final SOAPException e) {
            throw new XMLStreamException(e);
        }
    }
    
    @Override
    public String getPrefix(final String uri) throws XMLStreamException {
        return this.currentElement.lookupPrefix(uri);
    }
    
    @Override
    public void setPrefix(final String prefix, final String uri) throws XMLStreamException {
        if (this.deferredElement.isInitialized()) {
            this.deferredElement.addNamespaceDeclaration(prefix, uri);
            return;
        }
        throw new XMLStreamException("Namespace not associated with any element");
    }
    
    @Override
    public void setDefaultNamespace(final String uri) throws XMLStreamException {
        this.setPrefix("", uri);
    }
    
    @Override
    public void setNamespaceContext(final NamespaceContext context) throws XMLStreamException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Object getProperty(final String name) throws IllegalArgumentException {
        if ("javax.xml.stream.isRepairingNamespaces".equals(name)) {
            return Boolean.FALSE;
        }
        return null;
    }
    
    @Override
    public NamespaceContext getNamespaceContext() {
        return new NamespaceContext() {
            @Override
            public String getNamespaceURI(final String prefix) {
                return SaajStaxWriter.this.currentElement.getNamespaceURI(prefix);
            }
            
            @Override
            public String getPrefix(final String namespaceURI) {
                return SaajStaxWriter.this.currentElement.lookupPrefix(namespaceURI);
            }
            
            @Override
            public Iterator getPrefixes(final String namespaceURI) {
                return new Iterator<String>() {
                    String prefix = NamespaceContext.this.getPrefix(namespaceURI);
                    
                    @Override
                    public boolean hasNext() {
                        return this.prefix != null;
                    }
                    
                    @Override
                    public String next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        final String next = this.prefix;
                        this.prefix = null;
                        return next;
                    }
                    
                    @Override
                    public void remove() {
                    }
                };
            }
        };
    }
    
    static void addAttibuteToElement(final SOAPElement element, final String prefix, final String ns, final String ln, final String value) throws XMLStreamException {
        try {
            if (ns == null) {
                element.setAttributeNS("", ln, value);
            }
            else {
                final QName name = (prefix == null) ? new QName(ns, ln) : new QName(ns, ln, prefix);
                element.addAttribute(name, value);
            }
        }
        catch (final SOAPException e) {
            throw new XMLStreamException(e);
        }
    }
    
    static class DeferredElement
    {
        private String prefix;
        private String localName;
        private String namespaceUri;
        private final List<NamespaceDeclaration> namespaceDeclarations;
        private final List<AttributeDeclaration> attributeDeclarations;
        
        DeferredElement() {
            this.namespaceDeclarations = new LinkedList<NamespaceDeclaration>();
            this.attributeDeclarations = new LinkedList<AttributeDeclaration>();
            this.reset();
        }
        
        public void setPrefix(final String prefix) {
            this.prefix = prefix;
        }
        
        public void setLocalName(final String localName) {
            if (localName == null) {
                throw new IllegalArgumentException("localName can not be null");
            }
            this.localName = localName;
        }
        
        public void setNamespaceUri(final String namespaceUri) {
            this.namespaceUri = namespaceUri;
        }
        
        public void addNamespaceDeclaration(final String prefix, final String namespaceUri) {
            if (null == this.namespaceUri && null != namespaceUri && prefix.equals(emptyIfNull(this.prefix))) {
                this.namespaceUri = namespaceUri;
            }
            this.namespaceDeclarations.add(new NamespaceDeclaration(prefix, namespaceUri));
        }
        
        public void addAttribute(final String prefix, final String ns, final String ln, final String value) {
            if (ns == null && prefix == null && "xmlns".equals(ln)) {
                this.addNamespaceDeclaration(prefix, value);
            }
            else {
                this.attributeDeclarations.add(new AttributeDeclaration(prefix, ns, ln, value));
            }
        }
        
        public SOAPElement flushTo(final SOAPElement target) throws XMLStreamException {
            try {
                if (this.localName != null) {
                    SOAPElement newElement;
                    if (this.namespaceUri == null) {
                        newElement = target.addChildElement(this.localName);
                    }
                    else if (this.prefix == null) {
                        newElement = target.addChildElement(new QName(this.namespaceUri, this.localName));
                    }
                    else {
                        newElement = target.addChildElement(this.localName, this.prefix, this.namespaceUri);
                    }
                    for (final NamespaceDeclaration namespace : this.namespaceDeclarations) {
                        newElement.addNamespaceDeclaration(namespace.prefix, namespace.namespaceUri);
                    }
                    for (final AttributeDeclaration attribute : this.attributeDeclarations) {
                        SaajStaxWriter.addAttibuteToElement(newElement, attribute.prefix, attribute.namespaceUri, attribute.localName, attribute.value);
                    }
                    this.reset();
                    return newElement;
                }
                return target;
            }
            catch (final SOAPException e) {
                throw new XMLStreamException(e);
            }
        }
        
        public boolean isInitialized() {
            return this.localName != null;
        }
        
        private void reset() {
            this.localName = null;
            this.prefix = null;
            this.namespaceUri = null;
            this.namespaceDeclarations.clear();
            this.attributeDeclarations.clear();
        }
        
        private static String emptyIfNull(final String s) {
            return (s == null) ? "" : s;
        }
    }
    
    static class NamespaceDeclaration
    {
        final String prefix;
        final String namespaceUri;
        
        NamespaceDeclaration(final String prefix, final String namespaceUri) {
            this.prefix = prefix;
            this.namespaceUri = namespaceUri;
        }
    }
    
    static class AttributeDeclaration
    {
        final String prefix;
        final String namespaceUri;
        final String localName;
        final String value;
        
        AttributeDeclaration(final String prefix, final String namespaceUri, final String localName, final String value) {
            this.prefix = prefix;
            this.namespaceUri = namespaceUri;
            this.localName = localName;
            this.value = value;
        }
    }
}
