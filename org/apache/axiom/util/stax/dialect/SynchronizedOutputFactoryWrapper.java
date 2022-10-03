package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLStreamWriter;
import java.io.Writer;
import javax.xml.transform.Result;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLEventWriter;
import java.io.OutputStream;
import javax.xml.stream.XMLOutputFactory;
import org.apache.axiom.util.stax.wrapper.XMLOutputFactoryWrapper;

class SynchronizedOutputFactoryWrapper extends XMLOutputFactoryWrapper
{
    public SynchronizedOutputFactoryWrapper(final XMLOutputFactory parent) {
        super(parent);
    }
    
    @Override
    public synchronized XMLEventWriter createXMLEventWriter(final OutputStream stream, final String encoding) throws XMLStreamException {
        return super.createXMLEventWriter(stream, encoding);
    }
    
    @Override
    public synchronized XMLEventWriter createXMLEventWriter(final OutputStream stream) throws XMLStreamException {
        return super.createXMLEventWriter(stream);
    }
    
    @Override
    public synchronized XMLEventWriter createXMLEventWriter(final Result result) throws XMLStreamException {
        return super.createXMLEventWriter(result);
    }
    
    @Override
    public synchronized XMLEventWriter createXMLEventWriter(final Writer stream) throws XMLStreamException {
        return super.createXMLEventWriter(stream);
    }
    
    @Override
    public synchronized XMLStreamWriter createXMLStreamWriter(final OutputStream stream, final String encoding) throws XMLStreamException {
        return super.createXMLStreamWriter(stream, encoding);
    }
    
    @Override
    public synchronized XMLStreamWriter createXMLStreamWriter(final OutputStream stream) throws XMLStreamException {
        return super.createXMLStreamWriter(stream);
    }
    
    @Override
    public synchronized XMLStreamWriter createXMLStreamWriter(final Result result) throws XMLStreamException {
        return super.createXMLStreamWriter(result);
    }
    
    @Override
    public synchronized XMLStreamWriter createXMLStreamWriter(final Writer stream) throws XMLStreamException {
        return super.createXMLStreamWriter(stream);
    }
}
