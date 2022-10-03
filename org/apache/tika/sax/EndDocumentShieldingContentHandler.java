package org.apache.tika.sax;

import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;

public class EndDocumentShieldingContentHandler extends ContentHandlerDecorator
{
    private boolean endDocumentCalled;
    
    public EndDocumentShieldingContentHandler(final ContentHandler handler) {
        super(handler);
        this.endDocumentCalled = false;
    }
    
    @Override
    public void endDocument() throws SAXException {
        this.endDocumentCalled = true;
    }
    
    public void reallyEndDocument() throws SAXException {
        super.endDocument();
    }
    
    public boolean isEndDocumentWasCalled() {
        return this.endDocumentCalled;
    }
}
