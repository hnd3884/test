package jdk.internal.org.xml.sax.helpers;

import jdk.internal.org.xml.sax.SAXParseException;
import jdk.internal.org.xml.sax.Attributes;
import jdk.internal.org.xml.sax.Locator;
import jdk.internal.org.xml.sax.SAXException;
import java.io.IOException;
import jdk.internal.org.xml.sax.InputSource;
import jdk.internal.org.xml.sax.ErrorHandler;
import jdk.internal.org.xml.sax.ContentHandler;
import jdk.internal.org.xml.sax.DTDHandler;
import jdk.internal.org.xml.sax.EntityResolver;

public class DefaultHandler implements EntityResolver, DTDHandler, ContentHandler, ErrorHandler
{
    @Override
    public InputSource resolveEntity(final String s, final String s2) throws IOException, SAXException {
        return null;
    }
    
    @Override
    public void notationDecl(final String s, final String s2, final String s3) throws SAXException {
    }
    
    @Override
    public void unparsedEntityDecl(final String s, final String s2, final String s3, final String s4) throws SAXException {
    }
    
    @Override
    public void setDocumentLocator(final Locator locator) {
    }
    
    @Override
    public void startDocument() throws SAXException {
    }
    
    @Override
    public void endDocument() throws SAXException {
    }
    
    @Override
    public void startPrefixMapping(final String s, final String s2) throws SAXException {
    }
    
    @Override
    public void endPrefixMapping(final String s) throws SAXException {
    }
    
    @Override
    public void startElement(final String s, final String s2, final String s3, final Attributes attributes) throws SAXException {
    }
    
    @Override
    public void endElement(final String s, final String s2, final String s3) throws SAXException {
    }
    
    @Override
    public void characters(final char[] array, final int n, final int n2) throws SAXException {
    }
    
    @Override
    public void ignorableWhitespace(final char[] array, final int n, final int n2) throws SAXException {
    }
    
    @Override
    public void processingInstruction(final String s, final String s2) throws SAXException {
    }
    
    @Override
    public void skippedEntity(final String s) throws SAXException {
    }
    
    @Override
    public void warning(final SAXParseException ex) throws SAXException {
    }
    
    @Override
    public void error(final SAXParseException ex) throws SAXException {
    }
    
    @Override
    public void fatalError(final SAXParseException ex) throws SAXException {
        throw ex;
    }
}
