package org.apache.tika.sax;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;

public class TextContentHandler extends DefaultHandler
{
    private static final char[] SPACE;
    private final ContentHandler delegate;
    private final boolean addSpaceBetweenElements;
    
    public TextContentHandler(final ContentHandler delegate) {
        this(delegate, false);
    }
    
    public TextContentHandler(final ContentHandler delegate, final boolean addSpaceBetweenElements) {
        this.delegate = delegate;
        this.addSpaceBetweenElements = addSpaceBetweenElements;
    }
    
    @Override
    public void setDocumentLocator(final Locator locator) {
        this.delegate.setDocumentLocator(locator);
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        this.delegate.characters(ch, start, length);
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        this.delegate.ignorableWhitespace(ch, start, length);
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        if (this.addSpaceBetweenElements) {
            this.delegate.characters(TextContentHandler.SPACE, 0, TextContentHandler.SPACE.length);
        }
    }
    
    @Override
    public void startDocument() throws SAXException {
        this.delegate.startDocument();
    }
    
    @Override
    public void endDocument() throws SAXException {
        this.delegate.endDocument();
    }
    
    @Override
    public String toString() {
        return this.delegate.toString();
    }
    
    static {
        SPACE = new char[] { ' ' };
    }
}
