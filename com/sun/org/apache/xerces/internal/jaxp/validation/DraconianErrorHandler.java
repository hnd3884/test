package com.sun.org.apache.xerces.internal.jaxp.validation;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;

final class DraconianErrorHandler implements ErrorHandler
{
    private static final DraconianErrorHandler ERROR_HANDLER_INSTANCE;
    
    private DraconianErrorHandler() {
    }
    
    public static DraconianErrorHandler getInstance() {
        return DraconianErrorHandler.ERROR_HANDLER_INSTANCE;
    }
    
    @Override
    public void warning(final SAXParseException e) throws SAXException {
    }
    
    @Override
    public void error(final SAXParseException e) throws SAXException {
        throw e;
    }
    
    @Override
    public void fatalError(final SAXParseException e) throws SAXException {
        throw e;
    }
    
    static {
        ERROR_HANDLER_INSTANCE = new DraconianErrorHandler();
    }
}
