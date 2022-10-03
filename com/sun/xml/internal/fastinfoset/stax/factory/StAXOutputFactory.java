package com.sun.xml.internal.fastinfoset.stax.factory;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentSerializer;
import java.io.IOException;
import java.io.FileWriter;
import java.io.File;
import javax.xml.transform.stream.StreamResult;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.fastinfoset.stax.events.StAXEventWriter;
import javax.xml.stream.XMLEventWriter;
import javax.xml.transform.Result;
import com.sun.xml.internal.fastinfoset.stax.StAXManager;
import javax.xml.stream.XMLOutputFactory;

public class StAXOutputFactory extends XMLOutputFactory
{
    private StAXManager _manager;
    
    public StAXOutputFactory() {
        this._manager = null;
        this._manager = new StAXManager(2);
    }
    
    @Override
    public XMLEventWriter createXMLEventWriter(final Result result) throws XMLStreamException {
        return new StAXEventWriter(this.createXMLStreamWriter(result));
    }
    
    @Override
    public XMLEventWriter createXMLEventWriter(final Writer writer) throws XMLStreamException {
        return new StAXEventWriter(this.createXMLStreamWriter(writer));
    }
    
    @Override
    public XMLEventWriter createXMLEventWriter(final OutputStream outputStream) throws XMLStreamException {
        return new StAXEventWriter(this.createXMLStreamWriter(outputStream));
    }
    
    @Override
    public XMLEventWriter createXMLEventWriter(final OutputStream outputStream, final String encoding) throws XMLStreamException {
        return new StAXEventWriter(this.createXMLStreamWriter(outputStream, encoding));
    }
    
    @Override
    public XMLStreamWriter createXMLStreamWriter(final Result result) throws XMLStreamException {
        if (result instanceof StreamResult) {
            final StreamResult streamResult = (StreamResult)result;
            if (streamResult.getWriter() != null) {
                return this.createXMLStreamWriter(streamResult.getWriter());
            }
            if (streamResult.getOutputStream() != null) {
                return this.createXMLStreamWriter(streamResult.getOutputStream());
            }
            if (streamResult.getSystemId() != null) {
                FileWriter writer = null;
                boolean isError = true;
                try {
                    writer = new FileWriter(new File(streamResult.getSystemId()));
                    final XMLStreamWriter streamWriter = this.createXMLStreamWriter(writer);
                    isError = false;
                    return streamWriter;
                }
                catch (final IOException ie) {
                    throw new XMLStreamException(ie);
                }
                finally {
                    if (isError && writer != null) {
                        try {
                            writer.close();
                        }
                        catch (final IOException ex) {}
                    }
                }
            }
        }
        else {
            FileWriter writer2 = null;
            boolean isError2 = true;
            try {
                writer2 = new FileWriter(new File(result.getSystemId()));
                final XMLStreamWriter streamWriter2 = this.createXMLStreamWriter(writer2);
                isError2 = false;
                return streamWriter2;
            }
            catch (final IOException ie2) {
                throw new XMLStreamException(ie2);
            }
            finally {
                if (isError2 && writer2 != null) {
                    try {
                        writer2.close();
                    }
                    catch (final IOException ex2) {}
                }
            }
        }
        throw new UnsupportedOperationException();
    }
    
    @Override
    public XMLStreamWriter createXMLStreamWriter(final Writer writer) throws XMLStreamException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public XMLStreamWriter createXMLStreamWriter(final OutputStream outputStream) throws XMLStreamException {
        return new StAXDocumentSerializer(outputStream, new StAXManager(this._manager));
    }
    
    @Override
    public XMLStreamWriter createXMLStreamWriter(final OutputStream outputStream, final String encoding) throws XMLStreamException {
        final StAXDocumentSerializer serializer = new StAXDocumentSerializer(outputStream, new StAXManager(this._manager));
        serializer.setEncoding(encoding);
        return serializer;
    }
    
    @Override
    public Object getProperty(final String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.propertyNotSupported", new Object[] { null }));
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
    public void setProperty(final String name, final Object value) throws IllegalArgumentException {
        this._manager.setProperty(name, value);
    }
}
