package com.sun.xml.internal.stream;

import java.io.IOException;
import com.sun.xml.internal.stream.writers.XMLDOMWriterImpl;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.stream.XMLStreamWriter;
import java.io.Writer;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.Result;
import com.sun.xml.internal.stream.writers.XMLEventWriterImpl;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLEventWriter;
import java.io.OutputStream;
import com.sun.xml.internal.stream.writers.XMLStreamWriterImpl;
import com.sun.org.apache.xerces.internal.impl.PropertyManager;
import javax.xml.stream.XMLOutputFactory;

public class XMLOutputFactoryImpl extends XMLOutputFactory
{
    private PropertyManager fPropertyManager;
    private XMLStreamWriterImpl fStreamWriter;
    boolean fReuseInstance;
    private static final boolean DEBUG = false;
    private boolean fPropertyChanged;
    
    public XMLOutputFactoryImpl() {
        this.fPropertyManager = new PropertyManager(2);
        this.fStreamWriter = null;
        this.fReuseInstance = false;
    }
    
    @Override
    public XMLEventWriter createXMLEventWriter(final OutputStream outputStream) throws XMLStreamException {
        return this.createXMLEventWriter(outputStream, null);
    }
    
    @Override
    public XMLEventWriter createXMLEventWriter(final OutputStream outputStream, final String encoding) throws XMLStreamException {
        return new XMLEventWriterImpl(this.createXMLStreamWriter(outputStream, encoding));
    }
    
    @Override
    public XMLEventWriter createXMLEventWriter(final Result result) throws XMLStreamException {
        if (result instanceof StAXResult && ((StAXResult)result).getXMLEventWriter() != null) {
            return ((StAXResult)result).getXMLEventWriter();
        }
        return new XMLEventWriterImpl(this.createXMLStreamWriter(result));
    }
    
    @Override
    public XMLEventWriter createXMLEventWriter(final Writer writer) throws XMLStreamException {
        return new XMLEventWriterImpl(this.createXMLStreamWriter(writer));
    }
    
    @Override
    public XMLStreamWriter createXMLStreamWriter(final Result result) throws XMLStreamException {
        if (result instanceof StreamResult) {
            return this.createXMLStreamWriter((StreamResult)result, null);
        }
        if (result instanceof DOMResult) {
            return new XMLDOMWriterImpl((DOMResult)result);
        }
        if (result instanceof StAXResult) {
            if (((StAXResult)result).getXMLStreamWriter() != null) {
                return ((StAXResult)result).getXMLStreamWriter();
            }
            throw new UnsupportedOperationException("Result of type " + result + " is not supported");
        }
        else {
            if (result.getSystemId() != null) {
                return this.createXMLStreamWriter(new StreamResult(result.getSystemId()));
            }
            throw new UnsupportedOperationException("Result of type " + result + " is not supported. Supported result types are: DOMResult, StAXResult and StreamResult.");
        }
    }
    
    @Override
    public XMLStreamWriter createXMLStreamWriter(final Writer writer) throws XMLStreamException {
        return this.createXMLStreamWriter(this.toStreamResult(null, writer, null), null);
    }
    
    @Override
    public XMLStreamWriter createXMLStreamWriter(final OutputStream outputStream) throws XMLStreamException {
        return this.createXMLStreamWriter(outputStream, null);
    }
    
    @Override
    public XMLStreamWriter createXMLStreamWriter(final OutputStream outputStream, final String encoding) throws XMLStreamException {
        return this.createXMLStreamWriter(this.toStreamResult(outputStream, null, null), encoding);
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
    public void setProperty(final String name, final Object value) throws IllegalArgumentException {
        if (name == null || value == null || !this.fPropertyManager.containsProperty(name)) {
            throw new IllegalArgumentException("Property " + name + "is not supported");
        }
        if (name == "reuse-instance" || name.equals("reuse-instance")) {
            this.fReuseInstance = (boolean)value;
            if (this.fReuseInstance) {
                throw new IllegalArgumentException("Property " + name + " is not supported: XMLStreamWriters are not Thread safe");
            }
        }
        else {
            this.fPropertyChanged = true;
        }
        this.fPropertyManager.setProperty(name, value);
    }
    
    StreamResult toStreamResult(final OutputStream os, final Writer writer, final String systemId) {
        final StreamResult sr = new StreamResult();
        sr.setOutputStream(os);
        sr.setWriter(writer);
        sr.setSystemId(systemId);
        return sr;
    }
    
    XMLStreamWriter createXMLStreamWriter(final StreamResult sr, final String encoding) throws XMLStreamException {
        try {
            if (this.fReuseInstance && this.fStreamWriter != null && this.fStreamWriter.canReuse() && !this.fPropertyChanged) {
                this.fStreamWriter.reset();
                this.fStreamWriter.setOutput(sr, encoding);
                return this.fStreamWriter;
            }
            return this.fStreamWriter = new XMLStreamWriterImpl(sr, encoding, new PropertyManager(this.fPropertyManager));
        }
        catch (final IOException io) {
            throw new XMLStreamException(io);
        }
    }
}
