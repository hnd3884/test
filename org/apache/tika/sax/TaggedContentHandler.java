package org.apache.tika.sax;

import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;

public class TaggedContentHandler extends ContentHandlerDecorator
{
    public TaggedContentHandler(final ContentHandler proxy) {
        super(proxy);
    }
    
    public boolean isCauseOf(final SAXException exception) {
        if (exception instanceof TaggedSAXException) {
            final TaggedSAXException tagged = (TaggedSAXException)exception;
            return this == tagged.getTag();
        }
        return false;
    }
    
    public void throwIfCauseOf(final Exception exception) throws SAXException {
        if (exception instanceof TaggedSAXException) {
            final TaggedSAXException tagged = (TaggedSAXException)exception;
            if (this == tagged.getTag()) {
                throw tagged.getCause();
            }
        }
    }
    
    @Override
    protected void handleException(final SAXException e) throws SAXException {
        throw new TaggedSAXException(e, this);
    }
}
