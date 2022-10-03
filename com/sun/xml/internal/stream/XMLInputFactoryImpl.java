package com.sun.xml.internal.stream;

import javax.xml.transform.stream.StreamSource;
import com.sun.org.apache.xerces.internal.impl.XMLStreamFilterImpl;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.util.XMLEventAllocator;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import java.io.Reader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLEventReader;
import java.io.InputStream;
import com.sun.org.apache.xerces.internal.impl.XMLStreamReaderImpl;
import com.sun.org.apache.xerces.internal.impl.PropertyManager;
import javax.xml.stream.XMLInputFactory;

public class XMLInputFactoryImpl extends XMLInputFactory
{
    private PropertyManager fPropertyManager;
    private static final boolean DEBUG = false;
    private XMLStreamReaderImpl fTempReader;
    boolean fPropertyChanged;
    boolean fReuseInstance;
    
    public XMLInputFactoryImpl() {
        this.fPropertyManager = new PropertyManager(1);
        this.fTempReader = null;
        this.fPropertyChanged = false;
        this.fReuseInstance = false;
    }
    
    void initEventReader() {
        this.fPropertyChanged = true;
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final InputStream inputstream) throws XMLStreamException {
        this.initEventReader();
        return new XMLEventReaderImpl(this.createXMLStreamReader(inputstream));
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final Reader reader) throws XMLStreamException {
        this.initEventReader();
        return new XMLEventReaderImpl(this.createXMLStreamReader(reader));
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final Source source) throws XMLStreamException {
        this.initEventReader();
        return new XMLEventReaderImpl(this.createXMLStreamReader(source));
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final String systemId, final InputStream inputstream) throws XMLStreamException {
        this.initEventReader();
        return new XMLEventReaderImpl(this.createXMLStreamReader(systemId, inputstream));
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final InputStream stream, final String encoding) throws XMLStreamException {
        this.initEventReader();
        return new XMLEventReaderImpl(this.createXMLStreamReader(stream, encoding));
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final String systemId, final Reader reader) throws XMLStreamException {
        this.initEventReader();
        return new XMLEventReaderImpl(this.createXMLStreamReader(systemId, reader));
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final XMLStreamReader reader) throws XMLStreamException {
        return new XMLEventReaderImpl(reader);
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final InputStream inputstream) throws XMLStreamException {
        final XMLInputSource inputSource = new XMLInputSource(null, null, null, inputstream, null);
        return this.getXMLStreamReaderImpl(inputSource);
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final Reader reader) throws XMLStreamException {
        final XMLInputSource inputSource = new XMLInputSource(null, null, null, reader, null);
        return this.getXMLStreamReaderImpl(inputSource);
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final String systemId, final Reader reader) throws XMLStreamException {
        final XMLInputSource inputSource = new XMLInputSource(null, systemId, null, reader, null);
        return this.getXMLStreamReaderImpl(inputSource);
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final Source source) throws XMLStreamException {
        return new XMLStreamReaderImpl(this.jaxpSourcetoXMLInputSource(source), new PropertyManager(this.fPropertyManager));
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final String systemId, final InputStream inputstream) throws XMLStreamException {
        final XMLInputSource inputSource = new XMLInputSource(null, systemId, null, inputstream, null);
        return this.getXMLStreamReaderImpl(inputSource);
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final InputStream inputstream, final String encoding) throws XMLStreamException {
        final XMLInputSource inputSource = new XMLInputSource(null, null, null, inputstream, encoding);
        return this.getXMLStreamReaderImpl(inputSource);
    }
    
    @Override
    public XMLEventAllocator getEventAllocator() {
        return (XMLEventAllocator)this.getProperty("javax.xml.stream.allocator");
    }
    
    @Override
    public XMLReporter getXMLReporter() {
        return (XMLReporter)this.fPropertyManager.getProperty("javax.xml.stream.reporter");
    }
    
    @Override
    public XMLResolver getXMLResolver() {
        final Object object = this.fPropertyManager.getProperty("javax.xml.stream.resolver");
        return (XMLResolver)object;
    }
    
    @Override
    public void setXMLReporter(final XMLReporter xmlreporter) {
        this.fPropertyManager.setProperty("javax.xml.stream.reporter", xmlreporter);
    }
    
    @Override
    public void setXMLResolver(final XMLResolver xmlresolver) {
        this.fPropertyManager.setProperty("javax.xml.stream.resolver", xmlresolver);
    }
    
    @Override
    public XMLEventReader createFilteredReader(final XMLEventReader reader, final EventFilter filter) throws XMLStreamException {
        return new EventFilterSupport(reader, filter);
    }
    
    @Override
    public XMLStreamReader createFilteredReader(final XMLStreamReader reader, final StreamFilter filter) throws XMLStreamException {
        if (reader != null && filter != null) {
            return new XMLStreamFilterImpl(reader, filter);
        }
        return null;
    }
    
    @Override
    public Object getProperty(final String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("Property not supported");
        }
        if (this.fPropertyManager.containsProperty(name)) {
            return this.fPropertyManager.getProperty(name);
        }
        throw new IllegalArgumentException("Property not supported");
    }
    
    @Override
    public boolean isPropertySupported(final String name) {
        return name != null && this.fPropertyManager.containsProperty(name);
    }
    
    @Override
    public void setEventAllocator(final XMLEventAllocator allocator) {
        this.fPropertyManager.setProperty("javax.xml.stream.allocator", allocator);
    }
    
    @Override
    public void setProperty(final String name, final Object value) throws IllegalArgumentException {
        if (name == null || value == null || !this.fPropertyManager.containsProperty(name)) {
            throw new IllegalArgumentException("Property " + name + " is not supported");
        }
        if (name == "reuse-instance" || name.equals("reuse-instance")) {
            this.fReuseInstance = (boolean)value;
        }
        else {
            this.fPropertyChanged = true;
        }
        this.fPropertyManager.setProperty(name, value);
    }
    
    XMLStreamReader getXMLStreamReaderImpl(final XMLInputSource inputSource) throws XMLStreamException {
        if (this.fTempReader == null) {
            this.fPropertyChanged = false;
            return this.fTempReader = new XMLStreamReaderImpl(inputSource, new PropertyManager(this.fPropertyManager));
        }
        if (this.fReuseInstance && this.fTempReader.canReuse() && !this.fPropertyChanged) {
            this.fTempReader.reset();
            this.fTempReader.setInputSource(inputSource);
            this.fPropertyChanged = false;
            return this.fTempReader;
        }
        this.fPropertyChanged = false;
        return this.fTempReader = new XMLStreamReaderImpl(inputSource, new PropertyManager(this.fPropertyManager));
    }
    
    XMLInputSource jaxpSourcetoXMLInputSource(final Source source) {
        if (!(source instanceof StreamSource)) {
            throw new UnsupportedOperationException("Cannot create XMLStreamReader or XMLEventReader from a " + source.getClass().getName());
        }
        final StreamSource stSource = (StreamSource)source;
        final String systemId = stSource.getSystemId();
        final String publicId = stSource.getPublicId();
        final InputStream istream = stSource.getInputStream();
        final Reader reader = stSource.getReader();
        if (istream != null) {
            return new XMLInputSource(publicId, systemId, null, istream, null);
        }
        if (reader != null) {
            return new XMLInputSource(publicId, systemId, null, reader, null);
        }
        return new XMLInputSource(publicId, systemId, null);
    }
}
