package org.apache.xerces.stax.events;

import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.events.EndDocument;

public final class EndDocumentImpl extends XMLEventImpl implements EndDocument
{
    public EndDocumentImpl(final Location location) {
        super(8, location);
    }
    
    public void writeAsEncodedUnicode(final Writer writer) throws XMLStreamException {
    }
}
