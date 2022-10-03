package org.apache.axiom.om.impl.common.serializer.push.sax;

import org.xml.sax.SAXException;
import java.io.IOException;
import org.xml.sax.ContentHandler;
import java.io.Writer;

final class ContentHandlerWriter extends Writer
{
    private final ContentHandler contentHandler;
    
    public ContentHandlerWriter(final ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }
    
    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException {
        try {
            this.contentHandler.characters(cbuf, off, len);
        }
        catch (final SAXException ex) {
            final IOException ioException = new IOException();
            ioException.initCause(ex);
            throw ioException;
        }
    }
    
    @Override
    public void close() throws IOException {
    }
    
    @Override
    public void flush() throws IOException {
    }
}
