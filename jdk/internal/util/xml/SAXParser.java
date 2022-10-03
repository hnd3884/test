package jdk.internal.util.xml;

import jdk.internal.org.xml.sax.XMLReader;
import jdk.internal.org.xml.sax.DTDHandler;
import jdk.internal.org.xml.sax.ErrorHandler;
import jdk.internal.org.xml.sax.EntityResolver;
import jdk.internal.org.xml.sax.ContentHandler;
import java.io.File;
import java.io.IOException;
import jdk.internal.org.xml.sax.SAXException;
import jdk.internal.org.xml.sax.InputSource;
import jdk.internal.org.xml.sax.helpers.DefaultHandler;
import java.io.InputStream;

public abstract class SAXParser
{
    protected SAXParser() {
    }
    
    public void parse(final InputStream inputStream, final DefaultHandler defaultHandler) throws SAXException, IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }
        this.parse(new InputSource(inputStream), defaultHandler);
    }
    
    public void parse(final String s, final DefaultHandler defaultHandler) throws SAXException, IOException {
        if (s == null) {
            throw new IllegalArgumentException("uri cannot be null");
        }
        this.parse(new InputSource(s), defaultHandler);
    }
    
    public void parse(final File file, final DefaultHandler defaultHandler) throws SAXException, IOException {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        this.parse(new InputSource(file.toURI().toASCIIString()), defaultHandler);
    }
    
    public void parse(final InputSource inputSource, final DefaultHandler defaultHandler) throws SAXException, IOException {
        if (inputSource == null) {
            throw new IllegalArgumentException("InputSource cannot be null");
        }
        final XMLReader xmlReader = this.getXMLReader();
        if (defaultHandler != null) {
            xmlReader.setContentHandler(defaultHandler);
            xmlReader.setEntityResolver(defaultHandler);
            xmlReader.setErrorHandler(defaultHandler);
            xmlReader.setDTDHandler(defaultHandler);
        }
        xmlReader.parse(inputSource);
    }
    
    public abstract XMLReader getXMLReader() throws SAXException;
    
    public abstract boolean isNamespaceAware();
    
    public abstract boolean isValidating();
    
    public boolean isXIncludeAware() {
        throw new UnsupportedOperationException("This parser does not support specification \"" + this.getClass().getPackage().getSpecificationTitle() + "\" version \"" + this.getClass().getPackage().getSpecificationVersion() + "\"");
    }
}
