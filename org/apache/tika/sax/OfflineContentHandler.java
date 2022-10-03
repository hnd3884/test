package org.apache.tika.sax;

import java.io.InputStream;
import org.apache.commons.io.input.ClosedInputStream;
import org.xml.sax.InputSource;
import org.xml.sax.ContentHandler;

public class OfflineContentHandler extends ContentHandlerDecorator
{
    public OfflineContentHandler(final ContentHandler handler) {
        super(handler);
    }
    
    @Override
    public InputSource resolveEntity(final String publicId, final String systemId) {
        return new InputSource((InputStream)new ClosedInputStream());
    }
}
