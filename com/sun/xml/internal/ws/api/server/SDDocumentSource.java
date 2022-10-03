package com.sun.xml.internal.ws.api.server;

import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import java.io.InputStream;
import java.io.Closeable;
import com.sun.xml.internal.ws.streaming.TidyXMLStreamReader;
import java.net.URL;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLInputFactory;

public abstract class SDDocumentSource
{
    public abstract XMLStreamReader read(final XMLInputFactory p0) throws IOException, XMLStreamException;
    
    public abstract XMLStreamReader read() throws IOException, XMLStreamException;
    
    public abstract URL getSystemId();
    
    public static SDDocumentSource create(final URL url) {
        return new SDDocumentSource() {
            private final URL systemId = url;
            
            @Override
            public XMLStreamReader read(final XMLInputFactory xif) throws IOException, XMLStreamException {
                final InputStream is = url.openStream();
                return new TidyXMLStreamReader(xif.createXMLStreamReader(this.systemId.toExternalForm(), is), is);
            }
            
            @Override
            public XMLStreamReader read() throws IOException, XMLStreamException {
                final InputStream is = url.openStream();
                return new TidyXMLStreamReader(XMLStreamReaderFactory.create(this.systemId.toExternalForm(), is, false), is);
            }
            
            @Override
            public URL getSystemId() {
                return this.systemId;
            }
        };
    }
    
    public static SDDocumentSource create(final URL systemId, final XMLStreamBuffer xsb) {
        return new SDDocumentSource() {
            @Override
            public XMLStreamReader read(final XMLInputFactory xif) throws XMLStreamException {
                return xsb.readAsXMLStreamReader();
            }
            
            @Override
            public XMLStreamReader read() throws XMLStreamException {
                return xsb.readAsXMLStreamReader();
            }
            
            @Override
            public URL getSystemId() {
                return systemId;
            }
        };
    }
}
