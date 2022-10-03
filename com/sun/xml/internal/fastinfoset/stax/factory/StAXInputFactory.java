package com.sun.xml.internal.fastinfoset.stax.factory;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.stax.util.StAXFilteredParser;
import javax.xml.stream.StreamFilter;
import com.sun.xml.internal.fastinfoset.stax.events.StAXFilteredEvent;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.util.XMLEventAllocator;
import com.sun.xml.internal.fastinfoset.stax.events.StAXEventReader;
import javax.xml.stream.XMLEventReader;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import com.sun.xml.internal.fastinfoset.tools.XML_SAX_FI;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import javax.xml.transform.Source;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser;
import java.io.InputStream;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.Reader;
import com.sun.xml.internal.fastinfoset.stax.StAXManager;
import javax.xml.stream.XMLInputFactory;

public class StAXInputFactory extends XMLInputFactory
{
    private StAXManager _manager;
    
    public StAXInputFactory() {
        this._manager = new StAXManager(1);
    }
    
    public static XMLInputFactory newInstance() {
        return XMLInputFactory.newInstance();
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final Reader xmlfile) throws XMLStreamException {
        return this.getXMLStreamReader(xmlfile);
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final InputStream s) throws XMLStreamException {
        return new StAXDocumentParser(s, this._manager);
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final String systemId, final Reader xmlfile) throws XMLStreamException {
        return this.getXMLStreamReader(xmlfile);
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final Source source) throws XMLStreamException {
        return null;
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final String systemId, final InputStream inputstream) throws XMLStreamException {
        return this.createXMLStreamReader(inputstream);
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final InputStream inputstream, final String encoding) throws XMLStreamException {
        return this.createXMLStreamReader(inputstream);
    }
    
    XMLStreamReader getXMLStreamReader(final String systemId, final InputStream inputstream, final String encoding) throws XMLStreamException {
        return this.createXMLStreamReader(inputstream);
    }
    
    XMLStreamReader getXMLStreamReader(final Reader xmlfile) throws XMLStreamException {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        final BufferedOutputStream bufferedStream = new BufferedOutputStream(byteStream);
        StAXDocumentParser sr = null;
        try {
            final XML_SAX_FI convertor = new XML_SAX_FI();
            convertor.convert(xmlfile, bufferedStream);
            final ByteArrayInputStream byteInputStream = new ByteArrayInputStream(byteStream.toByteArray());
            final InputStream document = new BufferedInputStream(byteInputStream);
            sr = new StAXDocumentParser();
            sr.setInputStream(document);
            sr.setManager(this._manager);
            return sr;
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final InputStream inputstream) throws XMLStreamException {
        return new StAXEventReader(this.createXMLStreamReader(inputstream));
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final Reader reader) throws XMLStreamException {
        return new StAXEventReader(this.createXMLStreamReader(reader));
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final Source source) throws XMLStreamException {
        return new StAXEventReader(this.createXMLStreamReader(source));
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final String systemId, final InputStream inputstream) throws XMLStreamException {
        return new StAXEventReader(this.createXMLStreamReader(systemId, inputstream));
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final InputStream stream, final String encoding) throws XMLStreamException {
        return new StAXEventReader(this.createXMLStreamReader(stream, encoding));
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final String systemId, final Reader reader) throws XMLStreamException {
        return new StAXEventReader(this.createXMLStreamReader(systemId, reader));
    }
    
    @Override
    public XMLEventReader createXMLEventReader(final XMLStreamReader streamReader) throws XMLStreamException {
        return new StAXEventReader(streamReader);
    }
    
    @Override
    public XMLEventAllocator getEventAllocator() {
        return (XMLEventAllocator)this.getProperty("javax.xml.stream.allocator");
    }
    
    @Override
    public XMLReporter getXMLReporter() {
        return (XMLReporter)this._manager.getProperty("javax.xml.stream.reporter");
    }
    
    @Override
    public XMLResolver getXMLResolver() {
        final Object object = this._manager.getProperty("javax.xml.stream.resolver");
        return (XMLResolver)object;
    }
    
    @Override
    public void setXMLReporter(final XMLReporter xmlreporter) {
        this._manager.setProperty("javax.xml.stream.reporter", xmlreporter);
    }
    
    @Override
    public void setXMLResolver(final XMLResolver xmlresolver) {
        this._manager.setProperty("javax.xml.stream.resolver", xmlresolver);
    }
    
    @Override
    public XMLEventReader createFilteredReader(final XMLEventReader reader, final EventFilter filter) throws XMLStreamException {
        return new StAXFilteredEvent(reader, filter);
    }
    
    @Override
    public XMLStreamReader createFilteredReader(final XMLStreamReader reader, final StreamFilter filter) throws XMLStreamException {
        if (reader != null && filter != null) {
            return new StAXFilteredParser(reader, filter);
        }
        return null;
    }
    
    @Override
    public Object getProperty(final String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.nullPropertyName"));
        }
        if (this._manager.containsProperty(name)) {
            return this._manager.getProperty(name);
        }
        throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.propertyNotSupported", new Object[] { name }));
    }
    
    @Override
    public boolean isPropertySupported(final String name) {
        return name != null && this._manager.containsProperty(name);
    }
    
    @Override
    public void setEventAllocator(final XMLEventAllocator allocator) {
        this._manager.setProperty("javax.xml.stream.allocator", allocator);
    }
    
    @Override
    public void setProperty(final String name, final Object value) throws IllegalArgumentException {
        this._manager.setProperty(name, value);
    }
}
