package com.sun.xml.internal.bind.v2.util;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;

public class FatalAdapter implements ErrorHandler
{
    private final ErrorHandler core;
    
    public FatalAdapter(final ErrorHandler handler) {
        this.core = handler;
    }
    
    @Override
    public void warning(final SAXParseException exception) throws SAXException {
        this.core.warning(exception);
    }
    
    @Override
    public void error(final SAXParseException exception) throws SAXException {
        this.core.fatalError(exception);
    }
    
    @Override
    public void fatalError(final SAXParseException exception) throws SAXException {
        this.core.fatalError(exception);
    }
}
