package com.sun.istack.internal;

import org.xml.sax.SAXException;

public class SAXException2 extends SAXException
{
    public SAXException2(final String message) {
        super(message);
    }
    
    public SAXException2(final Exception e) {
        super(e);
    }
    
    public SAXException2(final String message, final Exception e) {
        super(message, e);
    }
    
    @Override
    public Throwable getCause() {
        return this.getException();
    }
}
