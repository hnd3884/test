package org.xml.sax.ext;

import java.io.IOException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DefaultHandler2 extends DefaultHandler implements LexicalHandler, DeclHandler, EntityResolver2
{
    public void startCDATA() throws SAXException {
    }
    
    public void endCDATA() throws SAXException {
    }
    
    public void startDTD(final String s, final String s2, final String s3) throws SAXException {
    }
    
    public void endDTD() throws SAXException {
    }
    
    public void startEntity(final String s) throws SAXException {
    }
    
    public void endEntity(final String s) throws SAXException {
    }
    
    public void comment(final char[] array, final int n, final int n2) throws SAXException {
    }
    
    public void attributeDecl(final String s, final String s2, final String s3, final String s4, final String s5) throws SAXException {
    }
    
    public void elementDecl(final String s, final String s2) throws SAXException {
    }
    
    public void externalEntityDecl(final String s, final String s2, final String s3) throws SAXException {
    }
    
    public void internalEntityDecl(final String s, final String s2) throws SAXException {
    }
    
    public InputSource getExternalSubset(final String s, final String s2) throws SAXException, IOException {
        return null;
    }
    
    public InputSource resolveEntity(final String s, final String s2, final String s3, final String s4) throws SAXException, IOException {
        return null;
    }
    
    public InputSource resolveEntity(final String s, final String s2) throws SAXException, IOException {
        return this.resolveEntity(null, s, null, s2);
    }
}
