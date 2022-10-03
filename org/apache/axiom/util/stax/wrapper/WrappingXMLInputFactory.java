package org.apache.axiom.util.stax.wrapper;

import javax.xml.transform.Source;
import java.io.Reader;
import java.io.InputStream;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;

public class WrappingXMLInputFactory extends XMLInputFactoryWrapper
{
    public WrappingXMLInputFactory(final XMLInputFactory parent) {
        super(parent);
    }
    
    protected XMLEventReader wrap(final XMLEventReader reader) {
        return reader;
    }
    
    protected XMLStreamReader wrap(final XMLStreamReader reader) {
        return reader;
    }
    
    @Override
    public XMLEventReader createFilteredReader(final XMLEventReader reader, final EventFilter filter) throws XMLStreamException {
        return this.wrap(super.createFilteredReader(reader, filter));
    }
    
    @Override
    public XMLStreamReader createFilteredReader(final XMLStreamReader reader, final StreamFilter filter) throws XMLStreamException {
        return this.wrap(super.createFilteredReader(reader, filter));
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final InputStream stream, final String encoding) throws XMLStreamException {
        return this.wrap(super.createXMLEventReader(stream, encoding));
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final InputStream stream) throws XMLStreamException {
        return this.wrap(super.createXMLEventReader(stream));
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final Reader reader) throws XMLStreamException {
        return this.wrap(super.createXMLEventReader(reader));
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final Source source) throws XMLStreamException {
        return this.wrap(super.createXMLEventReader(source));
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final String systemId, final InputStream stream) throws XMLStreamException {
        return this.wrap(super.createXMLEventReader(systemId, stream));
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final String systemId, final Reader reader) throws XMLStreamException {
        return this.wrap(super.createXMLEventReader(systemId, reader));
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final XMLStreamReader reader) throws XMLStreamException {
        return this.wrap(super.createXMLEventReader(reader));
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final InputStream stream, final String encoding) throws XMLStreamException {
        return this.wrap(super.createXMLStreamReader(stream, encoding));
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final InputStream stream) throws XMLStreamException {
        return this.wrap(super.createXMLStreamReader(stream));
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final Reader reader) throws XMLStreamException {
        return this.wrap(super.createXMLStreamReader(reader));
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final Source source) throws XMLStreamException {
        return this.wrap(super.createXMLStreamReader(source));
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final String systemId, final InputStream stream) throws XMLStreamException {
        return this.wrap(super.createXMLStreamReader(systemId, stream));
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final String systemId, final Reader reader) throws XMLStreamException {
        return this.wrap(super.createXMLStreamReader(systemId, reader));
    }
}
