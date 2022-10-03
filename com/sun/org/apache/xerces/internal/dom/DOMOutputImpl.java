package com.sun.org.apache.xerces.internal.dom;

import java.io.OutputStream;
import java.io.Writer;
import org.w3c.dom.ls.LSOutput;

public class DOMOutputImpl implements LSOutput
{
    protected Writer fCharStream;
    protected OutputStream fByteStream;
    protected String fSystemId;
    protected String fEncoding;
    
    public DOMOutputImpl() {
        this.fCharStream = null;
        this.fByteStream = null;
        this.fSystemId = null;
        this.fEncoding = null;
    }
    
    @Override
    public Writer getCharacterStream() {
        return this.fCharStream;
    }
    
    @Override
    public void setCharacterStream(final Writer characterStream) {
        this.fCharStream = characterStream;
    }
    
    @Override
    public OutputStream getByteStream() {
        return this.fByteStream;
    }
    
    @Override
    public void setByteStream(final OutputStream byteStream) {
        this.fByteStream = byteStream;
    }
    
    @Override
    public String getSystemId() {
        return this.fSystemId;
    }
    
    @Override
    public void setSystemId(final String systemId) {
        this.fSystemId = systemId;
    }
    
    @Override
    public String getEncoding() {
        return this.fEncoding;
    }
    
    @Override
    public void setEncoding(final String encoding) {
        this.fEncoding = encoding;
    }
}
