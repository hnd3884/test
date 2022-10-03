package com.sun.org.apache.xerces.internal.util;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;

public class DraconianErrorHandler implements ErrorHandler
{
    public static final ErrorHandler theInstance;
    
    private DraconianErrorHandler() {
    }
    
    @Override
    public void error(final SAXParseException e) throws SAXException {
        throw e;
    }
    
    @Override
    public void fatalError(final SAXParseException e) throws SAXException {
        throw e;
    }
    
    @Override
    public void warning(final SAXParseException e) throws SAXException {
    }
    
    static {
        theInstance = new DraconianErrorHandler();
    }
}
