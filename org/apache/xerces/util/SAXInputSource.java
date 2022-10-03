package org.apache.xerces.util;

import java.io.Reader;
import java.io.InputStream;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.apache.xerces.xni.parser.XMLInputSource;

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
    
    public SAXInputSource(final XMLReader fxmlReader, final InputSource fInputSource) {
        super((fInputSource != null) ? fInputSource.getPublicId() : null, (fInputSource != null) ? fInputSource.getSystemId() : null, null);
        if (fInputSource != null) {
            this.setByteStream(fInputSource.getByteStream());
            this.setCharacterStream(fInputSource.getCharacterStream());
            this.setEncoding(fInputSource.getEncoding());
        }
        this.fInputSource = fInputSource;
        this.fXMLReader = fxmlReader;
    }
    
    public void setXMLReader(final XMLReader fxmlReader) {
        this.fXMLReader = fxmlReader;
    }
    
    public XMLReader getXMLReader() {
        return this.fXMLReader;
    }
    
    public void setInputSource(final InputSource fInputSource) {
        if (fInputSource != null) {
            this.setPublicId(fInputSource.getPublicId());
            this.setSystemId(fInputSource.getSystemId());
            this.setByteStream(fInputSource.getByteStream());
            this.setCharacterStream(fInputSource.getCharacterStream());
            this.setEncoding(fInputSource.getEncoding());
        }
        else {
            this.setPublicId(null);
            this.setSystemId(null);
            this.setByteStream(null);
            this.setCharacterStream(null);
            this.setEncoding(null);
        }
        this.fInputSource = fInputSource;
    }
    
    public InputSource getInputSource() {
        return this.fInputSource;
    }
    
    public void setPublicId(final String s) {
        super.setPublicId(s);
        if (this.fInputSource == null) {
            this.fInputSource = new InputSource();
        }
        this.fInputSource.setPublicId(s);
    }
    
    public void setSystemId(final String s) {
        super.setSystemId(s);
        if (this.fInputSource == null) {
            this.fInputSource = new InputSource();
        }
        this.fInputSource.setSystemId(s);
    }
    
    public void setByteStream(final InputStream inputStream) {
        super.setByteStream(inputStream);
        if (this.fInputSource == null) {
            this.fInputSource = new InputSource();
        }
        this.fInputSource.setByteStream(inputStream);
    }
    
    public void setCharacterStream(final Reader reader) {
        super.setCharacterStream(reader);
        if (this.fInputSource == null) {
            this.fInputSource = new InputSource();
        }
        this.fInputSource.setCharacterStream(reader);
    }
    
    public void setEncoding(final String s) {
        super.setEncoding(s);
        if (this.fInputSource == null) {
            this.fInputSource = new InputSource();
        }
        this.fInputSource.setEncoding(s);
    }
}
