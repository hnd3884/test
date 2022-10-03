package com.sun.org.apache.xml.internal.resolver.readers;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.xml.sax.InputSource;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.helpers.DefaultHandler;

public class SAXParserHandler extends DefaultHandler
{
    private EntityResolver er;
    private ContentHandler ch;
    
    public SAXParserHandler() {
        this.er = null;
        this.ch = null;
    }
    
    public void setEntityResolver(final EntityResolver er) {
        this.er = er;
    }
    
    public void setContentHandler(final ContentHandler ch) {
        this.ch = ch;
    }
    
    @Override
    public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException {
        if (this.er != null) {
            try {
                return this.er.resolveEntity(publicId, systemId);
            }
            catch (final IOException e) {
                System.out.println("resolveEntity threw IOException!");
                return null;
            }
        }
        return null;
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (this.ch != null) {
            this.ch.characters(ch, start, length);
        }
    }
    
    @Override
    public void endDocument() throws SAXException {
        if (this.ch != null) {
            this.ch.endDocument();
        }
    }
    
    @Override
    public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
        if (this.ch != null) {
            this.ch.endElement(namespaceURI, localName, qName);
        }
    }
    
    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
        if (this.ch != null) {
            this.ch.endPrefixMapping(prefix);
        }
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        if (this.ch != null) {
            this.ch.ignorableWhitespace(ch, start, length);
        }
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        if (this.ch != null) {
            this.ch.processingInstruction(target, data);
        }
    }
    
    @Override
    public void setDocumentLocator(final Locator locator) {
        if (this.ch != null) {
            this.ch.setDocumentLocator(locator);
        }
    }
    
    @Override
    public void skippedEntity(final String name) throws SAXException {
        if (this.ch != null) {
            this.ch.skippedEntity(name);
        }
    }
    
    @Override
    public void startDocument() throws SAXException {
        if (this.ch != null) {
            this.ch.startDocument();
        }
    }
    
    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
        if (this.ch != null) {
            this.ch.startElement(namespaceURI, localName, qName, atts);
        }
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        if (this.ch != null) {
            this.ch.startPrefixMapping(prefix, uri);
        }
    }
}
