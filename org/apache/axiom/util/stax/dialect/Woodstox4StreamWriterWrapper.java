package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.util.stax.wrapper.XMLStreamWriterWrapper;

class Woodstox4StreamWriterWrapper extends XMLStreamWriterWrapper
{
    public Woodstox4StreamWriterWrapper(final XMLStreamWriter parent) {
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
