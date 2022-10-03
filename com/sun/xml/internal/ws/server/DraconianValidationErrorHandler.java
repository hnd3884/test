package com.sun.xml.internal.ws.server;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import com.sun.xml.internal.ws.developer.ValidationErrorHandler;

public class DraconianValidationErrorHandler extends ValidationErrorHandler
{
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
}
