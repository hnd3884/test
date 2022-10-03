package org.apache.xerces.jaxp.validation;

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
    
    public void warning(final SAXParseException ex) throws SAXException {
    }
    
    public void error(final SAXParseException ex) throws SAXException {
        throw ex;
    }
    
    public void fatalError(final SAXParseException ex) throws SAXException {
        throw ex;
    }
    
    static {
        ERROR_HANDLER_INSTANCE = new DraconianErrorHandler();
    }
}
