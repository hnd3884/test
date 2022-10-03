package com.sun.org.apache.xml.internal.serializer;

import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.Attributes;
import java.io.IOException;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import java.io.Writer;
import java.io.OutputStream;
import java.util.Properties;

public final class ToXMLSAXHandler extends ToSAXHandler
{
    protected boolean m_escapeSetting;
    
    public ToXMLSAXHandler() {
        this.m_escapeSetting = true;
        this.m_prefixMap = new NamespaceMappings();
        this.initCDATA();
    }
    
    @Override
    public Properties getOutputFormat() {
        return null;
    }
    
    @Override
    public OutputStream getOutputStream() {
        return null;
    }
    
    @Override
    public Writer getWriter() {
        return null;
    }
    
    public void indent(final int n) throws SAXException {
    }
    
    @Override
    public void serialize(final Node node) throws IOException {
    }
    
    @Override
    public boolean setEscaping(final boolean escape) throws SAXException {
        final boolean oldEscapeSetting = this.m_escapeSetting;
        this.m_escapeSetting = escape;
        if (escape) {
            this.processingInstruction("javax.xml.transform.enable-output-escaping", "");
        }
        else {
            this.processingInstruction("javax.xml.transform.disable-output-escaping", "");
        }
        return oldEscapeSetting;
    }
    
    @Override
    public void setOutputFormat(final Properties format) {
    }
    
    @Override
    public void setOutputStream(final OutputStream output) {
    }
    
    @Override
    public void setWriter(final Writer writer) {
    }
    
    @Override
    public void attributeDecl(final String arg0, final String arg1, final String arg2, final String arg3, final String arg4) throws SAXException {
    }
    
    @Override
    public void elementDecl(final String arg0, final String arg1) throws SAXException {
    }
    
    @Override
    public void externalEntityDecl(final String arg0, final String arg1, final String arg2) throws SAXException {
    }
    
    @Override
    public void internalEntityDecl(final String arg0, final String arg1) throws SAXException {
    }
    
    @Override
    public void endDocument() throws SAXException {
        this.flushPending();
        this.m_saxHandler.endDocument();
        if (this.m_tracer != null) {
            super.fireEndDoc();
        }
    }
    
    @Override
    protected void closeStartTag() throws SAXException {
        this.m_elemContext.m_startTagOpen = false;
        final String localName = SerializerBase.getLocalName(this.m_elemContext.m_elementName);
        final String uri = this.getNamespaceURI(this.m_elemContext.m_elementName, true);
        if (this.m_needToCallStartDocument) {
            this.startDocumentInternal();
        }
        this.m_saxHandler.startElement(uri, localName, this.m_elemContext.m_elementName, this.m_attributes);
        this.m_attributes.clear();
        if (this.m_state != null) {
            this.m_state.setCurrentNode(null);
        }
    }
    
    public void closeCDATA() throws SAXException {
        if (this.m_lexHandler != null && this.m_cdataTagOpen) {
            this.m_lexHandler.endCDATA();
        }
        this.m_cdataTagOpen = false;
    }
    
    @Override
    public void endElement(String namespaceURI, String localName, final String qName) throws SAXException {
        this.flushPending();
        if (namespaceURI == null) {
            if (this.m_elemContext.m_elementURI != null) {
                namespaceURI = this.m_elemContext.m_elementURI;
            }
            else {
                namespaceURI = this.getNamespaceURI(qName, true);
            }
        }
        if (localName == null) {
            if (this.m_elemContext.m_elementLocalName != null) {
                localName = this.m_elemContext.m_elementLocalName;
            }
            else {
                localName = SerializerBase.getLocalName(qName);
            }
        }
        this.m_saxHandler.endElement(namespaceURI, localName, qName);
        if (this.m_tracer != null) {
            super.fireEndElem(qName);
        }
        this.m_prefixMap.popNamespaces(this.m_elemContext.m_currentElemDepth, this.m_saxHandler);
        this.m_elemContext = this.m_elemContext.m_prev;
    }
    
    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
    }
    
    @Override
    public void ignorableWhitespace(final char[] arg0, final int arg1, final int arg2) throws SAXException {
        this.m_saxHandler.ignorableWhitespace(arg0, arg1, arg2);
    }
    
    @Override
    public void setDocumentLocator(final Locator arg0) {
        super.setDocumentLocator(arg0);
        this.m_saxHandler.setDocumentLocator(arg0);
    }
    
    @Override
    public void skippedEntity(final String arg0) throws SAXException {
        this.m_saxHandler.skippedEntity(arg0);
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        this.startPrefixMapping(prefix, uri, true);
    }
    
    @Override
    public boolean startPrefixMapping(final String prefix, final String uri, final boolean shouldFlush) throws SAXException {
        int pushDepth;
        if (shouldFlush) {
            this.flushPending();
            pushDepth = this.m_elemContext.m_currentElemDepth + 1;
        }
        else {
            pushDepth = this.m_elemContext.m_currentElemDepth;
        }
        final boolean pushed = this.m_prefixMap.pushNamespace(prefix, uri, pushDepth);
        if (pushed) {
            this.m_saxHandler.startPrefixMapping(prefix, uri);
            if (this.getShouldOutputNSAttr()) {
                if ("".equals(prefix)) {
                    final String name = "xmlns";
                    this.addAttributeAlways("http://www.w3.org/2000/xmlns/", name, name, "CDATA", uri, false);
                }
                else if (!"".equals(uri)) {
                    final String name = "xmlns:" + prefix;
                    this.addAttributeAlways("http://www.w3.org/2000/xmlns/", prefix, name, "CDATA", uri, false);
                }
            }
        }
        return pushed;
    }
    
    @Override
    public void comment(final char[] arg0, final int arg1, final int arg2) throws SAXException {
        this.flushPending();
        if (this.m_lexHandler != null) {
            this.m_lexHandler.comment(arg0, arg1, arg2);
        }
        if (this.m_tracer != null) {
            super.fireCommentEvent(arg0, arg1, arg2);
        }
    }
    
    @Override
    public void endCDATA() throws SAXException {
    }
    
    @Override
    public void endDTD() throws SAXException {
        if (this.m_lexHandler != null) {
            this.m_lexHandler.endDTD();
        }
    }
    
    @Override
    public void startEntity(final String arg0) throws SAXException {
        if (this.m_lexHandler != null) {
            this.m_lexHandler.startEntity(arg0);
        }
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
    
    public ToXMLSAXHandler(final ContentHandler handler, final String encoding) {
        super(handler, encoding);
        this.m_escapeSetting = true;
        this.initCDATA();
        this.m_prefixMap = new NamespaceMappings();
    }
    
    public ToXMLSAXHandler(final ContentHandler handler, final LexicalHandler lex, final String encoding) {
        super(handler, lex, encoding);
        this.m_escapeSetting = true;
        this.initCDATA();
        this.m_prefixMap = new NamespaceMappings();
    }
    
    @Override
    public void startElement(final String elementNamespaceURI, final String elementLocalName, final String elementName) throws SAXException {
        this.startElement(elementNamespaceURI, elementLocalName, elementName, null);
    }
    
    @Override
    public void startElement(final String elementName) throws SAXException {
        this.startElement(null, null, elementName, null);
    }
    
    @Override
    public void characters(final char[] ch, final int off, final int len) throws SAXException {
        if (this.m_needToCallStartDocument) {
            this.startDocumentInternal();
            this.m_needToCallStartDocument = false;
        }
        if (this.m_elemContext.m_startTagOpen) {
            this.closeStartTag();
            this.m_elemContext.m_startTagOpen = false;
        }
        if (this.m_elemContext.m_isCdataSection && !this.m_cdataTagOpen && this.m_lexHandler != null) {
            this.m_lexHandler.startCDATA();
            this.m_cdataTagOpen = true;
        }
        this.m_saxHandler.characters(ch, off, len);
        if (this.m_tracer != null) {
            this.fireCharEvent(ch, off, len);
        }
    }
    
    @Override
    public void endElement(final String elemName) throws SAXException {
        this.endElement(null, null, elemName);
    }
    
    @Override
    public void namespaceAfterStartElement(final String prefix, final String uri) throws SAXException {
        this.startPrefixMapping(prefix, uri, false);
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        this.flushPending();
        this.m_saxHandler.processingInstruction(target, data);
        if (this.m_tracer != null) {
            super.fireEscapingEvent(target, data);
        }
    }
    
    protected boolean popNamespace(final String prefix) {
        try {
            if (this.m_prefixMap.popNamespace(prefix)) {
                this.m_saxHandler.endPrefixMapping(prefix);
                return true;
            }
        }
        catch (final SAXException ex) {}
        return false;
    }
    
    @Override
    public void startCDATA() throws SAXException {
        if (!this.m_cdataTagOpen) {
            this.flushPending();
            if (this.m_lexHandler != null) {
                this.m_lexHandler.startCDATA();
                this.m_cdataTagOpen = true;
            }
        }
    }
    
    @Override
    public void startElement(final String namespaceURI, final String localName, final String name, final Attributes atts) throws SAXException {
        this.flushPending();
        super.startElement(namespaceURI, localName, name, atts);
        if (this.m_needToOutputDocTypeDecl) {
            final String doctypeSystem = this.getDoctypeSystem();
            if (doctypeSystem != null && this.m_lexHandler != null) {
                final String doctypePublic = this.getDoctypePublic();
                if (doctypeSystem != null) {
                    this.m_lexHandler.startDTD(name, doctypePublic, doctypeSystem);
                }
            }
            this.m_needToOutputDocTypeDecl = false;
        }
        this.m_elemContext = this.m_elemContext.push(namespaceURI, localName, name);
        if (namespaceURI != null) {
            this.ensurePrefixIsDeclared(namespaceURI, name);
        }
        if (atts != null) {
            this.addAttributes(atts);
        }
        this.m_elemContext.m_isCdataSection = this.isCdataSection();
    }
    
    private void ensurePrefixIsDeclared(final String ns, final String rawName) throws SAXException {
        if (ns != null && ns.length() > 0) {
            final int index;
            final boolean no_prefix = (index = rawName.indexOf(":")) < 0;
            final String prefix = no_prefix ? "" : rawName.substring(0, index);
            if (null != prefix) {
                final String foundURI = this.m_prefixMap.lookupNamespace(prefix);
                if (null == foundURI || !foundURI.equals(ns)) {
                    this.startPrefixMapping(prefix, ns, false);
                    if (this.getShouldOutputNSAttr()) {
                        this.addAttributeAlways("http://www.w3.org/2000/xmlns/", no_prefix ? "xmlns" : prefix, no_prefix ? "xmlns" : ("xmlns:" + prefix), "CDATA", ns, false);
                    }
                }
            }
        }
    }
    
    @Override
    public void addAttribute(final String uri, final String localName, final String rawName, final String type, final String value, final boolean XSLAttribute) throws SAXException {
        if (this.m_elemContext.m_startTagOpen) {
            this.ensurePrefixIsDeclared(uri, rawName);
            this.addAttributeAlways(uri, localName, rawName, type, value, false);
        }
    }
    
    @Override
    public boolean reset() {
        boolean wasReset = false;
        if (super.reset()) {
            this.resetToXMLSAXHandler();
            wasReset = true;
        }
        return wasReset;
    }
    
    private void resetToXMLSAXHandler() {
        this.m_escapeSetting = true;
    }
}
