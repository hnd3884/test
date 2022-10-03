package org.apache.xerces.dom;

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
    
    public Writer getCharacterStream() {
        return this.fCharStream;
    }
    
    public void setCharacterStream(final Writer fCharStream) {
        this.fCharStream = fCharStream;
    }
    
    public OutputStream getByteStream() {
        return this.fByteStream;
    }
    
    public void setByteStream(final OutputStream fByteStream) {
        this.fByteStream = fByteStream;
    }
    
    public String getSystemId() {
        return this.fSystemId;
    }
    
    public void setSystemId(final String fSystemId) {
        this.fSystemId = fSystemId;
    }
    
    public String getEncoding() {
        return this.fEncoding;
    }
    
    public void setEncoding(final String fEncoding) {
        this.fEncoding = fEncoding;
    }
}
