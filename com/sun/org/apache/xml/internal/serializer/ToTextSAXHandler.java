package com.sun.org.apache.xml.internal.serializer;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import java.io.IOException;
import org.w3c.dom.Node;
import java.io.Writer;
import java.io.OutputStream;
import java.util.Properties;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public final class ToTextSAXHandler extends ToSAXHandler
{
    @Override
    public void endElement(final String elemName) throws SAXException {
        if (this.m_tracer != null) {
            super.fireEndElem(elemName);
        }
    }
    
    @Override
    public void endElement(final String arg0, final String arg1, final String arg2) throws SAXException {
        if (this.m_tracer != null) {
            super.fireEndElem(arg2);
        }
    }
    
    public ToTextSAXHandler(final ContentHandler hdlr, final LexicalHandler lex, final String encoding) {
        super(hdlr, lex, encoding);
    }
    
    public ToTextSAXHandler(final ContentHandler handler, final String encoding) {
        super(handler, encoding);
    }
    
    @Override
    public void comment(final char[] ch, final int start, final int length) throws SAXException {
        if (this.m_tracer != null) {
            super.fireCommentEvent(ch, start, length);
        }
    }
    
    @Override
    public void comment(final String data) throws SAXException {
        final int length = data.length();
        if (length > this.m_charsBuff.length) {
            this.m_charsBuff = new char[length * 2 + 1];
        }
        data.getChars(0, length, this.m_charsBuff, 0);
        this.comment(this.m_charsBuff, 0, length);
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
    public boolean reset() {
        return false;
    }
    
    @Override
    public void serialize(final Node node) throws IOException {
    }
    
    @Override
    public boolean setEscaping(final boolean escape) {
        return false;
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
    public void addAttribute(final String uri, final String localName, final String rawName, final String type, final String value, final boolean XSLAttribute) {
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
    public void endPrefixMapping(final String arg0) throws SAXException {
    }
    
    @Override
    public void ignorableWhitespace(final char[] arg0, final int arg1, final int arg2) throws SAXException {
    }
    
    @Override
    public void processingInstruction(final String arg0, final String arg1) throws SAXException {
        if (this.m_tracer != null) {
            super.fireEscapingEvent(arg0, arg1);
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
    public void startElement(final String arg0, final String arg1, final String arg2, final Attributes arg3) throws SAXException {
        this.flushPending();
        super.startElement(arg0, arg1, arg2, arg3);
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
    public void startElement(final String elementNamespaceURI, final String elementLocalName, final String elementName) throws SAXException {
        super.startElement(elementNamespaceURI, elementLocalName, elementName);
    }
    
    @Override
    public void startElement(final String elementName) throws SAXException {
        super.startElement(elementName);
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
    public void characters(final String characters) throws SAXException {
        final int length = characters.length();
        if (length > this.m_charsBuff.length) {
            this.m_charsBuff = new char[length * 2 + 1];
        }
        characters.getChars(0, length, this.m_charsBuff, 0);
        this.m_saxHandler.characters(this.m_charsBuff, 0, length);
    }
    
    @Override
    public void characters(final char[] characters, final int offset, final int length) throws SAXException {
        this.m_saxHandler.characters(characters, offset, length);
        if (this.m_tracer != null) {
            super.fireCharEvent(characters, offset, length);
        }
    }
    
    @Override
    public void addAttribute(final String name, final String value) {
    }
    
    @Override
    public boolean startPrefixMapping(final String prefix, final String uri, final boolean shouldFlush) throws SAXException {
        return false;
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
    }
    
    @Override
    public void namespaceAfterStartElement(final String prefix, final String uri) throws SAXException {
    }
}
