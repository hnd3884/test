package com.sun.xml.internal.ws.message;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.ContentHandler;
import com.sun.xml.internal.ws.api.message.Message;
import org.xml.sax.helpers.XMLFilterImpl;

final class XMLReaderImpl extends XMLFilterImpl
{
    private final Message msg;
    private static final ContentHandler DUMMY;
    protected static final InputSource THE_SOURCE;
    
    XMLReaderImpl(final Message msg) {
        this.msg = msg;
    }
    
    @Override
    public void parse(final String systemId) {
        this.reportError();
    }
    
    private void reportError() {
        throw new IllegalStateException("This is a special XMLReader implementation that only works with the InputSource given in SAXSource.");
    }
    
    @Override
    public void parse(final InputSource input) throws SAXException {
        if (input != XMLReaderImpl.THE_SOURCE) {
            this.reportError();
        }
        this.msg.writeTo(this, this);
    }
    
    @Override
    public ContentHandler getContentHandler() {
        if (super.getContentHandler() == XMLReaderImpl.DUMMY) {
            return null;
        }
        return super.getContentHandler();
    }
    
    @Override
    public void setContentHandler(ContentHandler contentHandler) {
        if (contentHandler == null) {
            contentHandler = XMLReaderImpl.DUMMY;
        }
        super.setContentHandler(contentHandler);
    }
    
    static {
        DUMMY = new DefaultHandler();
        THE_SOURCE = new InputSource();
    }
}
