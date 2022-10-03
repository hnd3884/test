package org.apache.tika.sax;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;

public class ExpandedTitleContentHandler extends ContentHandlerDecorator
{
    private static final String TITLE_TAG = "TITLE";
    private boolean isTitleTagOpen;
    
    public ExpandedTitleContentHandler() {
    }
    
    public ExpandedTitleContentHandler(final ContentHandler handler) {
        super(handler);
    }
    
    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        this.isTitleTagOpen = false;
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts) throws SAXException {
        super.startElement(uri, localName, qName, atts);
        if ("TITLE".equalsIgnoreCase(localName) && "http://www.w3.org/1999/xhtml".equals(uri)) {
            this.isTitleTagOpen = true;
        }
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if ("TITLE".equalsIgnoreCase(localName) && "http://www.w3.org/1999/xhtml".equals(uri)) {
            this.isTitleTagOpen = false;
        }
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (this.isTitleTagOpen && length == 0) {
            try {
                super.characters(new char[0], 0, 1);
            }
            catch (final ArrayIndexOutOfBoundsException ex) {}
        }
        else {
            super.characters(ch, start, length);
        }
    }
}
