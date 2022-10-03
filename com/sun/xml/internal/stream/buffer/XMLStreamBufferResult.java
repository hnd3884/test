package com.sun.xml.internal.stream.buffer;

import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;
import com.sun.xml.internal.stream.buffer.sax.SAXBufferCreator;
import javax.xml.transform.sax.SAXResult;

public class XMLStreamBufferResult extends SAXResult
{
    protected MutableXMLStreamBuffer _buffer;
    protected SAXBufferCreator _bufferCreator;
    
    public XMLStreamBufferResult() {
        this.setXMLStreamBuffer(new MutableXMLStreamBuffer());
    }
    
    public XMLStreamBufferResult(final MutableXMLStreamBuffer buffer) {
        this.setXMLStreamBuffer(buffer);
    }
    
    public MutableXMLStreamBuffer getXMLStreamBuffer() {
        return this._buffer;
    }
    
    public void setXMLStreamBuffer(final MutableXMLStreamBuffer buffer) {
        if (buffer == null) {
            throw new NullPointerException("buffer cannot be null");
        }
        this._buffer = buffer;
        this.setSystemId(this._buffer.getSystemId());
        if (this._bufferCreator != null) {
            this._bufferCreator.setXMLStreamBuffer(this._buffer);
        }
    }
    
    @Override
    public ContentHandler getHandler() {
        if (this._bufferCreator == null) {
            this.setHandler(this._bufferCreator = new SAXBufferCreator(this._buffer));
        }
        else if (super.getHandler() == null) {
            this.setHandler(this._bufferCreator);
        }
        return this._bufferCreator;
    }
    
    @Override
    public LexicalHandler getLexicalHandler() {
        return (LexicalHandler)this.getHandler();
    }
}
