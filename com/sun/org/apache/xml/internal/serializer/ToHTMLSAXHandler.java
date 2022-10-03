package com.sun.org.apache.xml.internal.serializer;

import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import java.io.IOException;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import java.io.Writer;
import java.io.OutputStream;
import java.util.Properties;

public final class ToHTMLSAXHandler extends ToSAXHandler
{
    private boolean m_dtdHandled;
    protected boolean m_escapeSetting;
    
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
    public void setIndent(final boolean indent) {
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
    public void attributeDecl(final String eName, final String aName, final String type, final String valueDefault, final String value) throws SAXException {
    }
    
    @Override
    public void elementDecl(final String name, final String model) throws SAXException {
    }
    
    @Override
    public void externalEntityDecl(final String arg0, final String arg1, final String arg2) throws SAXException {
    }
    
    @Override
    public void internalEntityDecl(final String name, final String value) throws SAXException {
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        this.flushPending();
        this.m_saxHandler.endElement(uri, localName, qName);
        if (this.m_tracer != null) {
            super.fireEndElem(qName);
        }
    }
    
    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        this.flushPending();
        this.m_saxHandler.processingInstruction(target, data);
        if (this.m_tracer != null) {
            super.fireEscapingEvent(target, data);
        }
    }
    
    @Override
    public void setDocumentLocator(final Locator arg0) {
        super.setDocumentLocator(arg0);
    }
    
    @Override
    public void skippedEntity(final String arg0) throws SAXException {
    }
    
    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
        this.flushPending();
        super.startElement(namespaceURI, localName, qName, atts);
        this.m_saxHandler.startElement(namespaceURI, localName, qName, atts);
        this.m_elemContext.m_startTagOpen = false;
    }
    
    @Override
    public void comment(final char[] ch, final int start, final int length) throws SAXException {
        this.flushPending();
        if (this.m_lexHandler != null) {
            this.m_lexHandler.comment(ch, start, length);
        }
        if (this.m_tracer != null) {
            super.fireCommentEvent(ch, start, length);
        }
    }
    
    @Override
    public void endCDATA() throws SAXException {
    }
    
    @Override
    public void endDTD() throws SAXException {
    }
    
    @Override
    public void startCDATA() throws SAXException {
    }
    
    @Override
    public void startEntity(final String arg0) throws SAXException {
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
        this.m_saxHandler.startElement("", this.m_elemContext.m_elementName, this.m_elemContext.m_elementName, this.m_attributes);
        this.m_attributes.clear();
    }
    
    @Override
    public void close() {
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
    
    public ToHTMLSAXHandler(final ContentHandler handler, final String encoding) {
        super(handler, encoding);
        this.m_dtdHandled = false;
        this.m_escapeSetting = true;
    }
    
    public ToHTMLSAXHandler(final ContentHandler handler, final LexicalHandler lex, final String encoding) {
        super(handler, lex, encoding);
        this.m_dtdHandled = false;
        this.m_escapeSetting = true;
    }
    
    @Override
    public void startElement(final String elementNamespaceURI, final String elementLocalName, final String elementName) throws SAXException {
        super.startElement(elementNamespaceURI, elementLocalName, elementName);
        this.flushPending();
        if (!this.m_dtdHandled) {
            final String doctypeSystem = this.getDoctypeSystem();
            final String doctypePublic = this.getDoctypePublic();
            if ((doctypeSystem != null || doctypePublic != null) && this.m_lexHandler != null) {
                this.m_lexHandler.startDTD(elementName, doctypePublic, doctypeSystem);
            }
            this.m_dtdHandled = true;
        }
        this.m_elemContext = this.m_elemContext.push(elementNamespaceURI, elementLocalName, elementName);
    }
    
    @Override
    public void startElement(final String elementName) throws SAXException {
        this.startElement(null, null, elementName);
    }
    
    @Override
    public void endElement(final String elementName) throws SAXException {
        this.flushPending();
        this.m_saxHandler.endElement("", elementName, elementName);
        if (this.m_tracer != null) {
            super.fireEndElem(elementName);
        }
    }
    
    @Override
    public void characters(final char[] ch, final int off, final int len) throws SAXException {
        this.flushPending();
        this.m_saxHandler.characters(ch, off, len);
        if (this.m_tracer != null) {
            super.fireCharEvent(ch, off, len);
        }
    }
    
    @Override
    public void flushPending() throws SAXException {
        if (this.m_needToCallStartDocument) {
            this.startDocumentInternal();
            this.m_needToCallStartDocument = false;
        }
        if (this.m_elemContext.m_startTagOpen) {
            this.closeStartTag();
            this.m_elemContext.m_startTagOpen = false;
        }
    }
    
    @Override
    public boolean startPrefixMapping(final String prefix, final String uri, final boolean shouldFlush) throws SAXException {
        if (shouldFlush) {
            this.flushPending();
        }
        this.m_saxHandler.startPrefixMapping(prefix, uri);
        return false;
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        this.startPrefixMapping(prefix, uri, true);
    }
    
    @Override
    public void namespaceAfterStartElement(final String prefix, final String uri) throws SAXException {
        if (this.m_elemContext.m_elementURI == null) {
            final String prefix2 = SerializerBase.getPrefixPart(this.m_elemContext.m_elementName);
            if (prefix2 == null && "".equals(prefix)) {
                this.m_elemContext.m_elementURI = uri;
            }
        }
        this.startPrefixMapping(prefix, uri, false);
    }
    
    @Override
    public boolean reset() {
        boolean wasReset = false;
        if (super.reset()) {
            this.resetToHTMLSAXHandler();
            wasReset = true;
        }
        return wasReset;
    }
    
    private void resetToHTMLSAXHandler() {
        this.m_escapeSetting = true;
    }
}
