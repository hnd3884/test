package org.xml.sax;

public class HandlerBase implements EntityResolver, DTDHandler, DocumentHandler, ErrorHandler
{
    public InputSource resolveEntity(final String s, final String s2) throws SAXException {
        return null;
    }
    
    public void notationDecl(final String s, final String s2, final String s3) {
    }
    
    public void unparsedEntityDecl(final String s, final String s2, final String s3, final String s4) {
    }
    
    public void setDocumentLocator(final Locator locator) {
    }
    
    public void startDocument() throws SAXException {
    }
    
    public void endDocument() throws SAXException {
    }
    
    public void startElement(final String s, final AttributeList list) throws SAXException {
    }
    
    public void endElement(final String s) throws SAXException {
    }
    
    public void characters(final char[] array, final int n, final int n2) throws SAXException {
    }
    
    public void ignorableWhitespace(final char[] array, final int n, final int n2) throws SAXException {
    }
    
    public void processingInstruction(final String s, final String s2) throws SAXException {
    }
    
    public void warning(final SAXParseException ex) throws SAXException {
    }
    
    public void error(final SAXParseException ex) throws SAXException {
    }
    
    public void fatalError(final SAXParseException ex) throws SAXException {
        throw ex;
    }
}
