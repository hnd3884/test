package org.xml.sax.helpers;

import org.xml.sax.SAXParseException;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.xml.sax.InputSource;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;

public class DefaultHandler implements EntityResolver, DTDHandler, ContentHandler, ErrorHandler
{
    public InputSource resolveEntity(final String s, final String s2) throws IOException, SAXException {
        return null;
    }
    
    public void notationDecl(final String s, final String s2, final String s3) throws SAXException {
    }
    
    public void unparsedEntityDecl(final String s, final String s2, final String s3, final String s4) throws SAXException {
    }
    
    public void setDocumentLocator(final Locator locator) {
    }
    
    public void startDocument() throws SAXException {
    }
    
    public void endDocument() throws SAXException {
    }
    
    public void startPrefixMapping(final String s, final String s2) throws SAXException {
    }
    
    public void endPrefixMapping(final String s) throws SAXException {
    }
    
    public void startElement(final String s, final String s2, final String s3, final Attributes attributes) throws SAXException {
    }
    
    public void endElement(final String s, final String s2, final String s3) throws SAXException {
    }
    
    public void characters(final char[] array, final int n, final int n2) throws SAXException {
    }
    
    public void ignorableWhitespace(final char[] array, final int n, final int n2) throws SAXException {
    }
    
    public void processingInstruction(final String s, final String s2) throws SAXException {
    }
    
    public void skippedEntity(final String s) throws SAXException {
    }
    
    public void warning(final SAXParseException ex) throws SAXException {
    }
    
    public void error(final SAXParseException ex) throws SAXException {
    }
    
    public void fatalError(final SAXParseException ex) throws SAXException {
        throw ex;
    }
}
