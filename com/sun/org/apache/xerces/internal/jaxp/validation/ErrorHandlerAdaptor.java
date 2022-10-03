package com.sun.org.apache.xerces.internal.jaxp.validation;

import org.xml.sax.SAXException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import org.xml.sax.ErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;

public abstract class ErrorHandlerAdaptor implements XMLErrorHandler
{
    private boolean hadError;
    
    public ErrorHandlerAdaptor() {
        this.hadError = false;
    }
    
    public boolean hadError() {
        return this.hadError;
    }
    
    public void reset() {
        this.hadError = false;
    }
    
    protected abstract ErrorHandler getErrorHandler();
    
    @Override
    public void fatalError(final String domain, final String key, final XMLParseException e) {
        try {
            this.hadError = true;
            this.getErrorHandler().fatalError(Util.toSAXParseException(e));
        }
        catch (final SAXException se) {
            throw new WrappedSAXException(se);
        }
    }
    
    @Override
    public void error(final String domain, final String key, final XMLParseException e) {
        try {
            this.hadError = true;
            this.getErrorHandler().error(Util.toSAXParseException(e));
        }
        catch (final SAXException se) {
            throw new WrappedSAXException(se);
        }
    }
    
    @Override
    public void warning(final String domain, final String key, final XMLParseException e) {
        try {
            this.getErrorHandler().warning(Util.toSAXParseException(e));
        }
        catch (final SAXException se) {
            throw new WrappedSAXException(se);
        }
    }
}
