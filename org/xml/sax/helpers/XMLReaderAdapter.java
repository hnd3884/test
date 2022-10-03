package org.xml.sax.helpers;

import org.xml.sax.AttributeList;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import java.io.IOException;
import org.xml.sax.InputSource;
import org.xml.sax.ErrorHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXNotSupportedException;
import java.util.Locale;
import org.xml.sax.SAXException;
import org.xml.sax.DocumentHandler;
import org.xml.sax.XMLReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.Parser;

public class XMLReaderAdapter implements Parser, ContentHandler
{
    XMLReader xmlReader;
    DocumentHandler documentHandler;
    AttributesAdapter qAtts;
    
    public XMLReaderAdapter() throws SAXException {
        this.setup(XMLReaderFactory.createXMLReader());
    }
    
    public XMLReaderAdapter(final XMLReader xmlReader) {
        this.setup(xmlReader);
    }
    
    private void setup(final XMLReader xmlReader) {
        if (xmlReader == null) {
            throw new NullPointerException("XMLReader must not be null");
        }
        this.xmlReader = xmlReader;
        this.qAtts = new AttributesAdapter();
    }
    
    public void setLocale(final Locale locale) throws SAXException {
        throw new SAXNotSupportedException("setLocale not supported");
    }
    
    public void setEntityResolver(final EntityResolver entityResolver) {
        this.xmlReader.setEntityResolver(entityResolver);
    }
    
    public void setDTDHandler(final DTDHandler dtdHandler) {
        this.xmlReader.setDTDHandler(dtdHandler);
    }
    
    public void setDocumentHandler(final DocumentHandler documentHandler) {
        this.documentHandler = documentHandler;
    }
    
    public void setErrorHandler(final ErrorHandler errorHandler) {
        this.xmlReader.setErrorHandler(errorHandler);
    }
    
    public void parse(final String s) throws IOException, SAXException {
        this.parse(new InputSource(s));
    }
    
    public void parse(final InputSource inputSource) throws IOException, SAXException {
        this.setupXMLReader();
        this.xmlReader.parse(inputSource);
    }
    
    private void setupXMLReader() throws SAXException {
        this.xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
        try {
            this.xmlReader.setFeature("http://xml.org/sax/features/namespaces", false);
        }
        catch (final SAXException ex) {}
        this.xmlReader.setContentHandler(this);
    }
    
    public void setDocumentLocator(final Locator documentLocator) {
        if (this.documentHandler != null) {
            this.documentHandler.setDocumentLocator(documentLocator);
        }
    }
    
    public void startDocument() throws SAXException {
        if (this.documentHandler != null) {
            this.documentHandler.startDocument();
        }
    }
    
    public void endDocument() throws SAXException {
        if (this.documentHandler != null) {
            this.documentHandler.endDocument();
        }
    }
    
    public void startPrefixMapping(final String s, final String s2) {
    }
    
    public void endPrefixMapping(final String s) {
    }
    
    public void startElement(final String s, final String s2, final String s3, final Attributes attributes) throws SAXException {
        if (this.documentHandler != null) {
            this.qAtts.setAttributes(attributes);
            this.documentHandler.startElement(s3, this.qAtts);
        }
    }
    
    public void endElement(final String s, final String s2, final String s3) throws SAXException {
        if (this.documentHandler != null) {
            this.documentHandler.endElement(s3);
        }
    }
    
    public void characters(final char[] array, final int n, final int n2) throws SAXException {
        if (this.documentHandler != null) {
            this.documentHandler.characters(array, n, n2);
        }
    }
    
    public void ignorableWhitespace(final char[] array, final int n, final int n2) throws SAXException {
        if (this.documentHandler != null) {
            this.documentHandler.ignorableWhitespace(array, n, n2);
        }
    }
    
    public void processingInstruction(final String s, final String s2) throws SAXException {
        if (this.documentHandler != null) {
            this.documentHandler.processingInstruction(s, s2);
        }
    }
    
    public void skippedEntity(final String s) throws SAXException {
    }
    
    final class AttributesAdapter implements AttributeList
    {
        private Attributes attributes;
        
        void setAttributes(final Attributes attributes) {
            this.attributes = attributes;
        }
        
        public int getLength() {
            return this.attributes.getLength();
        }
        
        public String getName(final int n) {
            return this.attributes.getQName(n);
        }
        
        public String getType(final int n) {
            return this.attributes.getType(n);
        }
        
        public String getValue(final int n) {
            return this.attributes.getValue(n);
        }
        
        public String getType(final String s) {
            return this.attributes.getType(s);
        }
        
        public String getValue(final String s) {
            return this.attributes.getValue(s);
        }
    }
}
