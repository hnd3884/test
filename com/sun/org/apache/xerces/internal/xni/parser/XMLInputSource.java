package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
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
    
    public XMLInputSource(final String publicId, final String systemId, final String baseSystemId) {
        this.fPublicId = publicId;
        this.fSystemId = systemId;
        this.fBaseSystemId = baseSystemId;
    }
    
    public XMLInputSource(final XMLResourceIdentifier resourceIdentifier) {
        this.fPublicId = resourceIdentifier.getPublicId();
        this.fSystemId = resourceIdentifier.getLiteralSystemId();
        this.fBaseSystemId = resourceIdentifier.getBaseSystemId();
    }
    
    public XMLInputSource(final String publicId, final String systemId, final String baseSystemId, final InputStream byteStream, final String encoding) {
        this.fPublicId = publicId;
        this.fSystemId = systemId;
        this.fBaseSystemId = baseSystemId;
        this.fByteStream = byteStream;
        this.fEncoding = encoding;
    }
    
    public XMLInputSource(final String publicId, final String systemId, final String baseSystemId, final Reader charStream, final String encoding) {
        this.fPublicId = publicId;
        this.fSystemId = systemId;
        this.fBaseSystemId = baseSystemId;
        this.fCharStream = charStream;
        this.fEncoding = encoding;
    }
    
    public void setPublicId(final String publicId) {
        this.fPublicId = publicId;
    }
    
    public String getPublicId() {
        return this.fPublicId;
    }
    
    public void setSystemId(final String systemId) {
        this.fSystemId = systemId;
    }
    
    public String getSystemId() {
        return this.fSystemId;
    }
    
    public void setBaseSystemId(final String baseSystemId) {
        this.fBaseSystemId = baseSystemId;
    }
    
    public String getBaseSystemId() {
        return this.fBaseSystemId;
    }
    
    public void setByteStream(final InputStream byteStream) {
        this.fByteStream = byteStream;
    }
    
    public InputStream getByteStream() {
        return this.fByteStream;
    }
    
    public void setCharacterStream(final Reader charStream) {
        this.fCharStream = charStream;
    }
    
    public Reader getCharacterStream() {
        return this.fCharStream;
    }
    
    public void setEncoding(final String encoding) {
        this.fEncoding = encoding;
    }
    
    public String getEncoding() {
        return this.fEncoding;
    }
}
