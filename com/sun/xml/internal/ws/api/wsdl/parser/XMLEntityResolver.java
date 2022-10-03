package com.sun.xml.internal.ws.api.wsdl.parser;

import com.sun.xml.internal.ws.api.server.SDDocumentSource;
import javax.xml.stream.XMLStreamReader;
import java.net.URL;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import org.xml.sax.SAXException;

public interface XMLEntityResolver
{
    Parser resolveEntity(final String p0, final String p1) throws SAXException, IOException, XMLStreamException;
    
    public static final class Parser
    {
        public final URL systemId;
        public final XMLStreamReader parser;
        
        public Parser(final URL systemId, final XMLStreamReader parser) {
            assert parser != null;
            this.systemId = systemId;
            this.parser = parser;
        }
        
        public Parser(final SDDocumentSource doc) throws IOException, XMLStreamException {
            this.systemId = doc.getSystemId();
            this.parser = doc.read();
        }
    }
}
