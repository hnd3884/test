package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.util.stax.wrapper.XMLStreamWriterWrapper;

class Woodstox3StreamWriterWrapper extends XMLStreamWriterWrapper
{
    public Woodstox3StreamWriterWrapper(final XMLStreamWriter parent) {
        super(parent);
    }
    
    @Override
    public void writeStartDocument(final String encoding, final String version) throws XMLStreamException {
        if (encoding == null) {
            throw new IllegalArgumentException();
        }
        super.writeStartDocument(encoding, version);
    }
}
