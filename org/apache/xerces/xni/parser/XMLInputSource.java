package org.apache.xerces.xni.parser;

import org.apache.xerces.xni.XMLResourceIdentifier;
import java.io.Reader;
import java.io.InputStream;

public class XMLInputSource
{
    protected String fPublicId;
    protected String fSystemId;
    protected String fBaseSystemId;
    protected InputStream fByteStream;
    protected Reader fCharStream;
    protected String fEncoding;
    
    public XMLInputSource(final String fPublicId, final String fSystemId, final String fBaseSystemId) {
        this.fPublicId = fPublicId;
        this.fSystemId = fSystemId;
        this.fBaseSystemId = fBaseSystemId;
    }
    
    public XMLInputSource(final XMLResourceIdentifier xmlResourceIdentifier) {
        this.fPublicId = xmlResourceIdentifier.getPublicId();
        this.fSystemId = xmlResourceIdentifier.getLiteralSystemId();
        this.fBaseSystemId = xmlResourceIdentifier.getBaseSystemId();
    }
    
    public XMLInputSource(final String fPublicId, final String fSystemId, final String fBaseSystemId, final InputStream fByteStream, final String fEncoding) {
        this.fPublicId = fPublicId;
        this.fSystemId = fSystemId;
        this.fBaseSystemId = fBaseSystemId;
        this.fByteStream = fByteStream;
        this.fEncoding = fEncoding;
    }
    
    public XMLInputSource(final String fPublicId, final String fSystemId, final String fBaseSystemId, final Reader fCharStream, final String fEncoding) {
        this.fPublicId = fPublicId;
        this.fSystemId = fSystemId;
        this.fBaseSystemId = fBaseSystemId;
        this.fCharStream = fCharStream;
        this.fEncoding = fEncoding;
    }
    
    public void setPublicId(final String fPublicId) {
        this.fPublicId = fPublicId;
    }
    
    public String getPublicId() {
        return this.fPublicId;
    }
    
    public void setSystemId(final String fSystemId) {
        this.fSystemId = fSystemId;
    }
    
    public String getSystemId() {
        return this.fSystemId;
    }
    
    public void setBaseSystemId(final String fBaseSystemId) {
        this.fBaseSystemId = fBaseSystemId;
    }
    
    public String getBaseSystemId() {
        return this.fBaseSystemId;
    }
    
    public void setByteStream(final InputStream fByteStream) {
        this.fByteStream = fByteStream;
    }
    
    public InputStream getByteStream() {
        return this.fByteStream;
    }
    
    public void setCharacterStream(final Reader fCharStream) {
        this.fCharStream = fCharStream;
    }
    
    public Reader getCharacterStream() {
        return this.fCharStream;
    }
    
    public void setEncoding(final String fEncoding) {
        this.fEncoding = fEncoding;
    }
    
    public String getEncoding() {
        return this.fEncoding;
    }
}
