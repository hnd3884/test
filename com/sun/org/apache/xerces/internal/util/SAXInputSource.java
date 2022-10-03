package com.sun.org.apache.xerces.internal.util;

import java.io.Reader;
import java.io.InputStream;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;

public final class SAXInputSource extends XMLInputSource
{
    private XMLReader fXMLReader;
    private InputSource fInputSource;
    
    public SAXInputSource() {
        this((InputSource)null);
    }
    
    public SAXInputSource(final InputSource inputSource) {
        this(null, inputSource);
    }
    
    public SAXInputSource(final XMLReader reader, final InputSource inputSource) {
        super((inputSource != null) ? inputSource.getPublicId() : null, (inputSource != null) ? inputSource.getSystemId() : null, null);
        if (inputSource != null) {
            this.setByteStream(inputSource.getByteStream());
            this.setCharacterStream(inputSource.getCharacterStream());
            this.setEncoding(inputSource.getEncoding());
        }
        this.fInputSource = inputSource;
        this.fXMLReader = reader;
    }
    
    public void setXMLReader(final XMLReader reader) {
        this.fXMLReader = reader;
    }
    
    public XMLReader getXMLReader() {
        return this.fXMLReader;
    }
    
    public void setInputSource(final InputSource inputSource) {
        if (inputSource != null) {
            this.setPublicId(inputSource.getPublicId());
            this.setSystemId(inputSource.getSystemId());
            this.setByteStream(inputSource.getByteStream());
            this.setCharacterStream(inputSource.getCharacterStream());
            this.setEncoding(inputSource.getEncoding());
        }
        else {
            this.setPublicId(null);
            this.setSystemId(null);
            this.setByteStream(null);
            this.setCharacterStream(null);
            this.setEncoding(null);
        }
        this.fInputSource = inputSource;
    }
    
    public InputSource getInputSource() {
        return this.fInputSource;
    }
    
    @Override
    public void setPublicId(final String publicId) {
        super.setPublicId(publicId);
        if (this.fInputSource == null) {
            this.fInputSource = new InputSource();
        }
        this.fInputSource.setPublicId(publicId);
    }
    
    @Override
    public void setSystemId(final String systemId) {
        super.setSystemId(systemId);
        if (this.fInputSource == null) {
            this.fInputSource = new InputSource();
        }
        this.fInputSource.setSystemId(systemId);
    }
    
    @Override
    public void setByteStream(final InputStream byteStream) {
        super.setByteStream(byteStream);
        if (this.fInputSource == null) {
            this.fInputSource = new InputSource();
        }
        this.fInputSource.setByteStream(byteStream);
    }
    
    @Override
    public void setCharacterStream(final Reader charStream) {
        super.setCharacterStream(charStream);
        if (this.fInputSource == null) {
            this.fInputSource = new InputSource();
        }
        this.fInputSource.setCharacterStream(charStream);
    }
    
    @Override
    public void setEncoding(final String encoding) {
        super.setEncoding(encoding);
        if (this.fInputSource == null) {
            this.fInputSource = new InputSource();
        }
        this.fInputSource.setEncoding(encoding);
    }
}
