package org.apache.axiom.util.stax.wrapper;

import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.transform.Source;
import java.io.Reader;
import java.io.InputStream;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;

public class XMLInputFactoryWrapper extends XMLInputFactory
{
    private final XMLInputFactory parent;
    
    public XMLInputFactoryWrapper(final XMLInputFactory parent) {
        this.parent = parent;
    }
    
    @Override
    public XMLEventReader createFilteredReader(final XMLEventReader reader, final EventFilter filter) throws XMLStreamException {
        return this.parent.createFilteredReader(reader, filter);
    }
    
    @Override
    public XMLStreamReader createFilteredReader(final XMLStreamReader reader, final StreamFilter filter) throws XMLStreamException {
        return this.parent.createFilteredReader(reader, filter);
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final InputStream stream, final String encoding) throws XMLStreamException {
        return this.parent.createXMLEventReader(stream, encoding);
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final InputStream stream) throws XMLStreamException {
        return this.parent.createXMLEventReader(stream);
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final Reader reader) throws XMLStreamException {
        return this.parent.createXMLEventReader(reader);
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final Source source) throws XMLStreamException {
        return this.parent.createXMLEventReader(source);
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final String systemId, final InputStream stream) throws XMLStreamException {
        return this.parent.createXMLEventReader(systemId, stream);
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final String systemId, final Reader reader) throws XMLStreamException {
        return this.parent.createXMLEventReader(systemId, reader);
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final XMLStreamReader reader) throws XMLStreamException {
        return this.parent.createXMLEventReader(reader);
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final InputStream stream, final String encoding) throws XMLStreamException {
        return this.parent.createXMLStreamReader(stream, encoding);
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final InputStream stream) throws XMLStreamException {
        return this.parent.createXMLStreamReader(stream);
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final Reader reader) throws XMLStreamException {
        return this.parent.createXMLStreamReader(reader);
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final Source source) throws XMLStreamException {
        return this.parent.createXMLStreamReader(source);
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final String systemId, final InputStream stream) throws XMLStreamException {
        return this.parent.createXMLStreamReader(systemId, stream);
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final String systemId, final Reader reader) throws XMLStreamException {
        return this.parent.createXMLStreamReader(systemId, reader);
    }
    
    @Override
    public XMLEventAllocator getEventAllocator() {
        return this.parent.getEventAllocator();
    }
    
    @Override
    public Object getProperty(final String name) throws IllegalArgumentException {
        return this.parent.getProperty(name);
    }
    
    @Override
    public XMLReporter getXMLReporter() {
        return this.parent.getXMLReporter();
    }
    
    @Override
    public XMLResolver getXMLResolver() {
        return this.parent.getXMLResolver();
    }
    
    @Override
    public boolean isPropertySupported(final String name) {
        return this.parent.isPropertySupported(name);
    }
    
    @Override
    public void setEventAllocator(final XMLEventAllocator allocator) {
        this.parent.setEventAllocator(allocator);
    }
    
    @Override
    public void setProperty(final String name, final Object value) throws IllegalArgumentException {
        this.parent.setProperty(name, value);
    }
    
    @Override
    public void setXMLReporter(final XMLReporter reporter) {
        this.parent.setXMLReporter(reporter);
    }
    
    @Override
    public void setXMLResolver(final XMLResolver resolver) {
        this.parent.setXMLResolver(resolver);
    }
}
