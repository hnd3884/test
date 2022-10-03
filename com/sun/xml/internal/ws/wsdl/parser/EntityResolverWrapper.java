package com.sun.xml.internal.ws.wsdl.parser;

import java.io.IOException;
import org.xml.sax.SAXException;
import java.io.InputStream;
import org.xml.sax.InputSource;
import javax.xml.stream.XMLStreamReader;
import java.io.Closeable;
import com.sun.xml.internal.ws.streaming.TidyXMLStreamReader;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import java.net.URL;
import org.xml.sax.EntityResolver;
import com.sun.xml.internal.ws.api.wsdl.parser.XMLEntityResolver;

final class EntityResolverWrapper implements XMLEntityResolver
{
    private final EntityResolver core;
    private boolean useStreamFromEntityResolver;
    
    public EntityResolverWrapper(final EntityResolver core) {
        this.useStreamFromEntityResolver = false;
        this.core = core;
    }
    
    public EntityResolverWrapper(final EntityResolver core, final boolean useStreamFromEntityResolver) {
        this.useStreamFromEntityResolver = false;
        this.core = core;
        this.useStreamFromEntityResolver = useStreamFromEntityResolver;
    }
    
    @Override
    public Parser resolveEntity(final String publicId, String systemId) throws SAXException, IOException {
        final InputSource source = this.core.resolveEntity(publicId, systemId);
        if (source == null) {
            return null;
        }
        if (source.getSystemId() != null) {
            systemId = source.getSystemId();
        }
        final URL url = new URL(systemId);
        InputStream stream;
        if (this.useStreamFromEntityResolver) {
            stream = source.getByteStream();
        }
        else {
            stream = url.openStream();
        }
        return new Parser(url, new TidyXMLStreamReader(XMLStreamReaderFactory.create(url.toExternalForm(), stream, true), stream));
    }
}
