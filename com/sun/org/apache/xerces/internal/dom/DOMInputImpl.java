package com.sun.org.apache.xerces.internal.dom;

import java.io.Reader;
import java.io.InputStream;
import org.w3c.dom.ls.LSInput;

public class DOMInputImpl implements LSInput
{
    protected String fPublicId;
    protected String fSystemId;
    protected String fBaseSystemId;
    protected InputStream fByteStream;
    protected Reader fCharStream;
    protected String fData;
    protected String fEncoding;
    protected boolean fCertifiedText;
    
    public DOMInputImpl() {
        this.fPublicId = null;
        this.fSystemId = null;
        this.fBaseSystemId = null;
        this.fByteStream = null;
        this.fCharStream = null;
        this.fData = null;
        this.fEncoding = null;
        this.fCertifiedText = false;
    }
    
    public DOMInputImpl(final String publicId, final String systemId, final String baseSystemId) {
        this.fPublicId = null;
        this.fSystemId = null;
        this.fBaseSystemId = null;
        this.fByteStream = null;
        this.fCharStream = null;
        this.fData = null;
        this.fEncoding = null;
        this.fCertifiedText = false;
        this.fPublicId = publicId;
        this.fSystemId = systemId;
        this.fBaseSystemId = baseSystemId;
    }
    
    public DOMInputImpl(final String publicId, final String systemId, final String baseSystemId, final InputStream byteStream, final String encoding) {
        this.fPublicId = null;
        this.fSystemId = null;
        this.fBaseSystemId = null;
        this.fByteStream = null;
        this.fCharStream = null;
        this.fData = null;
        this.fEncoding = null;
        this.fCertifiedText = false;
        this.fPublicId = publicId;
        this.fSystemId = systemId;
        this.fBaseSystemId = baseSystemId;
        this.fByteStream = byteStream;
        this.fEncoding = encoding;
    }
    
    public DOMInputImpl(final String publicId, final String systemId, final String baseSystemId, final Reader charStream, final String encoding) {
        this.fPublicId = null;
        this.fSystemId = null;
        this.fBaseSystemId = null;
        this.fByteStream = null;
        this.fCharStream = null;
        this.fData = null;
        this.fEncoding = null;
        this.fCertifiedText = false;
        this.fPublicId = publicId;
        this.fSystemId = systemId;
        this.fBaseSystemId = baseSystemId;
        this.fCharStream = charStream;
        this.fEncoding = encoding;
    }
    
    public DOMInputImpl(final String publicId, final String systemId, final String baseSystemId, final String data, final String encoding) {
        this.fPublicId = null;
        this.fSystemId = null;
        this.fBaseSystemId = null;
        this.fByteStream = null;
        this.fCharStream = null;
        this.fData = null;
        this.fEncoding = null;
        this.fCertifiedText = false;
        this.fPublicId = publicId;
        this.fSystemId = systemId;
        this.fBaseSystemId = baseSystemId;
        this.fData = data;
        this.fEncoding = encoding;
    }
    
    @Override
    public InputStream getByteStream() {
        return this.fByteStream;
    }
    
    @Override
    public void setByteStream(final InputStream byteStream) {
        this.fByteStream = byteStream;
    }
    
    @Override
    public Reader getCharacterStream() {
        return this.fCharStream;
    }
    
    @Override
    public void setCharacterStream(final Reader characterStream) {
        this.fCharStream = characterStream;
    }
    
    @Override
    public String getStringData() {
        return this.fData;
    }
    
    @Override
    public void setStringData(final String stringData) {
        this.fData = stringData;
    }
    
    @Override
    public String getEncoding() {
        return this.fEncoding;
    }
    
    @Override
    public void setEncoding(final String encoding) {
        this.fEncoding = encoding;
    }
    
    @Override
    public String getPublicId() {
        return this.fPublicId;
    }
    
    @Override
    public void setPublicId(final String publicId) {
        this.fPublicId = publicId;
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
    public String getBaseURI() {
        return this.fBaseSystemId;
    }
    
    @Override
    public void setBaseURI(final String baseURI) {
        this.fBaseSystemId = baseURI;
    }
    
    @Override
    public boolean getCertifiedText() {
        return this.fCertifiedText;
    }
    
    @Override
    public void setCertifiedText(final boolean certifiedText) {
        this.fCertifiedText = certifiedText;
    }
}
