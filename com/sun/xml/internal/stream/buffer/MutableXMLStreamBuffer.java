package com.sun.xml.internal.stream.buffer;

import org.xml.sax.DTDHandler;
import org.xml.sax.ContentHandler;
import java.io.IOException;
import org.xml.sax.SAXException;
import java.io.InputStream;
import org.xml.sax.XMLReader;
import com.sun.xml.internal.stream.buffer.sax.SAXBufferCreator;
import com.sun.xml.internal.stream.buffer.stax.StreamWriterBufferCreator;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.stream.buffer.stax.StreamReaderBufferCreator;
import javax.xml.stream.XMLStreamReader;

public class MutableXMLStreamBuffer extends XMLStreamBuffer
{
    public static final int DEFAULT_ARRAY_SIZE = 512;
    
    public MutableXMLStreamBuffer() {
        this(512);
    }
    
    public void setSystemId(final String systemId) {
        this.systemId = systemId;
    }
    
    public MutableXMLStreamBuffer(final int size) {
        this._structure = new FragmentedArray<byte[]>(new byte[size]);
        this._structureStrings = new FragmentedArray<String[]>(new String[size]);
        this._contentCharactersBuffer = new FragmentedArray<char[]>(new char[4096]);
        this._contentObjects = new FragmentedArray<Object[]>(new Object[size]);
        this._structure.getArray()[0] = -112;
    }
    
    public void createFromXMLStreamReader(final XMLStreamReader reader) throws XMLStreamException {
        this.reset();
        final StreamReaderBufferCreator c = new StreamReaderBufferCreator(this);
        c.create(reader);
    }
    
    public XMLStreamWriter createFromXMLStreamWriter() {
        this.reset();
        return new StreamWriterBufferCreator(this);
    }
    
    public SAXBufferCreator createFromSAXBufferCreator() {
        this.reset();
        final SAXBufferCreator c = new SAXBufferCreator();
        c.setBuffer(this);
        return c;
    }
    
    public void createFromXMLReader(final XMLReader reader, final InputStream in) throws SAXException, IOException {
        this.createFromXMLReader(reader, in, null);
    }
    
    public void createFromXMLReader(final XMLReader reader, final InputStream in, final String systemId) throws SAXException, IOException {
        this.reset();
        final SAXBufferCreator c = new SAXBufferCreator(this);
        reader.setContentHandler(c);
        reader.setDTDHandler(c);
        reader.setProperty("http://xml.org/sax/properties/lexical-handler", c);
        c.create(reader, in, systemId);
    }
    
    public void reset() {
        final int n = 0;
        this._contentObjectsPtr = n;
        this._contentCharactersBufferPtr = n;
        this._structureStringsPtr = n;
        this._structurePtr = n;
        this._structure.getArray()[0] = -112;
        this._contentObjects.setNext(null);
        final Object[] o = this._contentObjects.getArray();
        for (int i = 0; i < o.length && o[i] != null; ++i) {
            o[i] = null;
        }
        this.treeCount = 0;
    }
    
    protected void setHasInternedStrings(final boolean hasInternedStrings) {
        this._hasInternedStrings = hasInternedStrings;
    }
}
