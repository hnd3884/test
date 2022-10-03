package com.sun.istack.internal;

import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

public class SAXParseException2 extends SAXParseException
{
    public SAXParseException2(final String message, final Locator locator) {
        super(message, locator);
    }
    
    public SAXParseException2(final String message, final Locator locator, final Exception e) {
        super(message, locator, e);
    }
    
    public SAXParseException2(final String message, final String publicId, final String systemId, final int lineNumber, final int columnNumber) {
        super(message, publicId, systemId, lineNumber, columnNumber);
    }
    
    public SAXParseException2(final String message, final String publicId, final String systemId, final int lineNumber, final int columnNumber, final Exception e) {
        super(message, publicId, systemId, lineNumber, columnNumber, e);
    }
    
    @Override
    public Throwable getCause() {
        return this.getException();
    }
}
