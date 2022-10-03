package org.apache.axiom.util.stax.wrapper;

import javax.xml.stream.XMLStreamWriter;
import java.io.Writer;
import javax.xml.transform.Result;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLEventWriter;
import java.io.OutputStream;
import javax.xml.stream.XMLOutputFactory;

public class XMLOutputFactoryWrapper extends XMLOutputFactory
{
    private final XMLOutputFactory parent;
    
    public XMLOutputFactoryWrapper(final XMLOutputFactory parent) {
        this.parent = parent;
    }
    
    @Override
    public XMLEventWriter createXMLEventWriter(final OutputStream stream, final String encoding) throws XMLStreamException {
        return this.parent.createXMLEventWriter(stream, encoding);
    }
    
    @Override
    public XMLEventWriter createXMLEventWriter(final OutputStream stream) throws XMLStreamException {
        return this.parent.createXMLEventWriter(stream);
    }
    
    @Override
    public XMLEventWriter createXMLEventWriter(final Result result) throws XMLStreamException {
        return this.parent.createXMLEventWriter(result);
    }
    
    @Override
    public XMLEventWriter createXMLEventWriter(final Writer stream) throws XMLStreamException {
        return this.parent.createXMLEventWriter(stream);
    }
    
    @Override
    public XMLStreamWriter createXMLStreamWriter(final OutputStream stream, final String encoding) throws XMLStreamException {
        return this.parent.createXMLStreamWriter(stream, encoding);
    }
    
    @Override
    public XMLStreamWriter createXMLStreamWriter(final OutputStream stream) throws XMLStreamException {
        return this.parent.createXMLStreamWriter(stream);
    }
    
    @Override
    public XMLStreamWriter createXMLStreamWriter(final Result result) throws XMLStreamException {
        return this.parent.createXMLStreamWriter(result);
    }
    
    @Override
    public XMLStreamWriter createXMLStreamWriter(final Writer stream) throws XMLStreamException {
        return this.parent.createXMLStreamWriter(stream);
    }
    
    @Override
    public Object getProperty(final String name) throws IllegalArgumentException {
        return this.parent.getProperty(name);
    }
    
    @Override
    public boolean isPropertySupported(final String name) {
        return this.parent.isPropertySupported(name);
    }
    
    @Override
    public void setProperty(final String name, final Object value) throws IllegalArgumentException {
        this.parent.setProperty(name, value);
    }
}
