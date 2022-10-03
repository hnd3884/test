package com.sun.rowset.internal;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlErrorHandler extends DefaultHandler
{
    public int errorCounter;
    
    public XmlErrorHandler() {
        this.errorCounter = 0;
    }
    
    @Override
    public void error(final SAXParseException ex) throws SAXException {
        ++this.errorCounter;
    }
    
    @Override
    public void fatalError(final SAXParseException ex) throws SAXException {
        ++this.errorCounter;
    }
    
    @Override
    public void warning(final SAXParseException ex) throws SAXException {
    }
}
