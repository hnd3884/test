package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLEventWriter;
import java.io.OutputStream;
import javax.xml.stream.XMLOutputFactory;

class SJSXPOutputFactoryWrapper extends NormalizingXMLOutputFactoryWrapper
{
    public SJSXPOutputFactoryWrapper(final XMLOutputFactory parent, final AbstractStAXDialect dialect) {
        super(parent, dialect);
    }
    
    @Override
    public XMLEventWriter createXMLEventWriter(final OutputStream stream, final String encoding) throws XMLStreamException {
        if (encoding == null) {
            throw new IllegalArgumentException();
        }
        return super.createXMLEventWriter(stream, encoding);
    }
    
    @Override
    public XMLStreamWriter createXMLStreamWriter(final OutputStream stream, final String encoding) throws XMLStreamException {
        if (encoding == null) {
            throw new IllegalArgumentException();
        }
        return super.createXMLStreamWriter(stream, encoding);
    }
}
