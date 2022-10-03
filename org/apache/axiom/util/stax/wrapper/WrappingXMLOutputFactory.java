package org.apache.axiom.util.stax.wrapper;

import java.io.Writer;
import javax.xml.transform.Result;
import javax.xml.stream.XMLStreamException;
import java.io.OutputStream;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;

public class WrappingXMLOutputFactory extends XMLOutputFactoryWrapper
{
    public WrappingXMLOutputFactory(final XMLOutputFactory parent) {
        super(parent);
    }
    
    protected XMLEventWriter wrap(final XMLEventWriter writer) {
        return writer;
    }
    
    protected XMLStreamWriter wrap(final XMLStreamWriter writer) {
        return writer;
    }
    
    @Override
    public XMLEventWriter createXMLEventWriter(final OutputStream stream, final String encoding) throws XMLStreamException {
        return this.wrap(super.createXMLEventWriter(stream, encoding));
    }
    
    @Override
    public XMLEventWriter createXMLEventWriter(final OutputStream stream) throws XMLStreamException {
        return this.wrap(super.createXMLEventWriter(stream));
    }
    
    @Override
    public XMLEventWriter createXMLEventWriter(final Result result) throws XMLStreamException {
        return this.wrap(super.createXMLEventWriter(result));
    }
    
    @Override
    public XMLEventWriter createXMLEventWriter(final Writer stream) throws XMLStreamException {
        return this.wrap(super.createXMLEventWriter(stream));
    }
    
    @Override
    public XMLStreamWriter createXMLStreamWriter(final OutputStream stream, final String encoding) throws XMLStreamException {
        return this.wrap(super.createXMLStreamWriter(stream, encoding));
    }
    
    @Override
    public XMLStreamWriter createXMLStreamWriter(final OutputStream stream) throws XMLStreamException {
        return this.wrap(super.createXMLStreamWriter(stream));
    }
    
    @Override
    public XMLStreamWriter createXMLStreamWriter(final Result result) throws XMLStreamException {
        return this.wrap(super.createXMLStreamWriter(result));
    }
    
    @Override
    public XMLStreamWriter createXMLStreamWriter(final Writer stream) throws XMLStreamException {
        return this.wrap(super.createXMLStreamWriter(stream));
    }
}
