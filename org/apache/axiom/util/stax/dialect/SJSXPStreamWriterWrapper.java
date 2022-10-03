package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.util.stax.wrapper.XMLStreamWriterWrapper;

class SJSXPStreamWriterWrapper extends XMLStreamWriterWrapper
{
    public SJSXPStreamWriterWrapper(final XMLStreamWriter parent) {
        super(parent);
    }
    
    @Override
    public void writeStartDocument(final String encoding, final String version) throws XMLStreamException {
        if (encoding == null) {
            throw new IllegalArgumentException();
        }
        super.writeStartDocument(encoding, version);
    }
    
    @Override
    public Object getProperty(final String name) throws IllegalArgumentException {
        try {
            return super.getProperty(name);
        }
        catch (final NullPointerException ex) {
            throw new IllegalArgumentException();
        }
    }
    
    @Override
    public void writeStartElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        super.writeStartElement((prefix.length() == 0) ? localName : (prefix + ":" + localName));
    }
}
