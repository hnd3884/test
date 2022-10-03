package com.sun.xml.internal.stream.buffer;

import org.xml.sax.XMLReader;
import java.io.InputStream;
import org.xml.sax.InputSource;
import java.io.ByteArrayInputStream;
import com.sun.xml.internal.stream.buffer.sax.SAXBufferProcessor;
import javax.xml.transform.sax.SAXSource;

public class XMLStreamBufferSource extends SAXSource
{
    protected XMLStreamBuffer _buffer;
    protected SAXBufferProcessor _bufferProcessor;
    
    public XMLStreamBufferSource(final XMLStreamBuffer buffer) {
        super(new InputSource(new ByteArrayInputStream(new byte[0])));
        this.setXMLStreamBuffer(buffer);
    }
    
    public XMLStreamBuffer getXMLStreamBuffer() {
        return this._buffer;
    }
    
    public void setXMLStreamBuffer(final XMLStreamBuffer buffer) {
        if (buffer == null) {
            throw new NullPointerException("buffer cannot be null");
        }
        this._buffer = buffer;
        if (this._bufferProcessor != null) {
            this._bufferProcessor.setBuffer(this._buffer, false);
        }
    }
    
    @Override
    public XMLReader getXMLReader() {
        if (this._bufferProcessor == null) {
            this.setXMLReader(this._bufferProcessor = new SAXBufferProcessor(this._buffer, false));
        }
        else if (super.getXMLReader() == null) {
            this.setXMLReader(this._bufferProcessor);
        }
        return this._bufferProcessor;
    }
}
