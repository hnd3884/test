package com.sun.xml.internal.ws.util.xml;

import org.xml.sax.SAXParseException;
import javax.xml.stream.XMLStreamException;
import com.sun.istack.internal.SAXParseException2;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ext.LexicalHandler;
import com.sun.istack.internal.NotNull;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;
import javax.xml.stream.XMLStreamReader;
import com.sun.istack.internal.XMLStreamReaderToContentHandler;
import javax.xml.transform.sax.SAXSource;

public class StAXSource extends SAXSource
{
    private final XMLStreamReaderToContentHandler reader;
    private final XMLStreamReader staxReader;
    private final XMLFilterImpl repeater;
    private final XMLReader pseudoParser;
    
    public StAXSource(final XMLStreamReader reader, final boolean eagerQuit) {
        this(reader, eagerQuit, new String[0]);
    }
    
    public StAXSource(final XMLStreamReader reader, final boolean eagerQuit, @NotNull final String[] inscope) {
        this.repeater = new XMLFilterImpl();
        this.pseudoParser = new XMLReader() {
            private LexicalHandler lexicalHandler;
            private EntityResolver entityResolver;
            private DTDHandler dtdHandler;
            private ErrorHandler errorHandler;
            
            @Override
            public boolean getFeature(final String name) throws SAXNotRecognizedException {
                throw new SAXNotRecognizedException(name);
            }
            
            @Override
            public void setFeature(final String name, final boolean value) throws SAXNotRecognizedException {
                if (!name.equals("http://xml.org/sax/features/namespaces") || !value) {
                    if (!name.equals("http://xml.org/sax/features/namespace-prefixes") || value) {
                        throw new SAXNotRecognizedException(name);
                    }
                }
            }
            
            @Override
            public Object getProperty(final String name) throws SAXNotRecognizedException {
                if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
                    return this.lexicalHandler;
                }
                throw new SAXNotRecognizedException(name);
            }
            
            @Override
            public void setProperty(final String name, final Object value) throws SAXNotRecognizedException {
                if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
                    this.lexicalHandler = (LexicalHandler)value;
                    return;
                }
                throw new SAXNotRecognizedException(name);
            }
            
            @Override
            public void setEntityResolver(final EntityResolver resolver) {
                this.entityResolver = resolver;
            }
            
            @Override
            public EntityResolver getEntityResolver() {
                return this.entityResolver;
            }
            
            @Override
            public void setDTDHandler(final DTDHandler handler) {
                this.dtdHandler = handler;
            }
            
            @Override
            public DTDHandler getDTDHandler() {
                return this.dtdHandler;
            }
            
            @Override
            public void setContentHandler(final ContentHandler handler) {
                StAXSource.this.repeater.setContentHandler(handler);
            }
            
            @Override
            public ContentHandler getContentHandler() {
                return StAXSource.this.repeater.getContentHandler();
            }
            
            @Override
            public void setErrorHandler(final ErrorHandler handler) {
                this.errorHandler = handler;
            }
            
            @Override
            public ErrorHandler getErrorHandler() {
                return this.errorHandler;
            }
            
            @Override
            public void parse(final InputSource input) throws SAXException {
                this.parse();
            }
            
            @Override
            public void parse(final String systemId) throws SAXException {
                this.parse();
            }
            
            public void parse() throws SAXException {
                try {
                    StAXSource.this.reader.bridge();
                }
                catch (final XMLStreamException e) {
                    final SAXParseException se = new SAXParseException2(e.getMessage(), null, null, (e.getLocation() == null) ? -1 : e.getLocation().getLineNumber(), (e.getLocation() == null) ? -1 : e.getLocation().getColumnNumber(), e);
                    if (this.errorHandler != null) {
                        this.errorHandler.fatalError(se);
                    }
                    throw se;
                }
                finally {
                    try {
                        StAXSource.this.staxReader.close();
                    }
                    catch (final XMLStreamException ex) {}
                }
            }
        };
        if (reader == null) {
            throw new IllegalArgumentException();
        }
        this.staxReader = reader;
        final int eventType = reader.getEventType();
        if (eventType != 7 && eventType != 1) {
            throw new IllegalStateException();
        }
        this.reader = new XMLStreamReaderToContentHandler(reader, this.repeater, eagerQuit, false, inscope);
        super.setXMLReader(this.pseudoParser);
        super.setInputSource(new InputSource());
    }
}
