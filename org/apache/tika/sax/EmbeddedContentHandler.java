package org.apache.tika.sax;

import org.xml.sax.ContentHandler;

public class EmbeddedContentHandler extends ContentHandlerDecorator
{
    public EmbeddedContentHandler(final ContentHandler handler) {
        super(handler);
    }
    
    @Override
    public void startDocument() {
    }
    
    @Override
    public void endDocument() {
    }
}
