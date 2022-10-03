package org.apache.tika.sax;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;

public class ContentHandlerDecorator extends DefaultHandler
{
    private ContentHandler handler;
    
    public ContentHandlerDecorator(final ContentHandler handler) {
        assert handler != null;
        this.handler = handler;
    }
    
    protected ContentHandlerDecorator() {
        this(new DefaultHandler());
    }
    
    protected void setContentHandler(final ContentHandler handler) {
        assert handler != null;
        this.handler = handler;
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        try {
            this.handler.startPrefixMapping(prefix, uri);
        }
        catch (final SAXException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
        try {
            this.handler.endPrefixMapping(prefix);
        }
        catch (final SAXException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        try {
            this.handler.processingInstruction(target, data);
        }
        catch (final SAXException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setDocumentLocator(final Locator locator) {
        this.handler.setDocumentLocator(locator);
    }
    
    @Override
    public void startDocument() throws SAXException {
        try {
            this.handler.startDocument();
        }
        catch (final SAXException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void endDocument() throws SAXException {
        try {
            this.handler.endDocument();
        }
        catch (final SAXException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String name, final Attributes atts) throws SAXException {
        try {
            this.handler.startElement(uri, localName, name, atts);
        }
        catch (final SAXException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String name) throws SAXException {
        try {
            this.handler.endElement(uri, localName, name);
        }
        catch (final SAXException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        try {
            this.handler.characters(ch, start, length);
        }
        catch (final SAXException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        try {
            this.handler.ignorableWhitespace(ch, start, length);
        }
        catch (final SAXException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void skippedEntity(final String name) throws SAXException {
        try {
            this.handler.skippedEntity(name);
        }
        catch (final SAXException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public String toString() {
        return this.handler.toString();
    }
    
    protected void handleException(final SAXException exception) throws SAXException {
        throw exception;
    }
}
