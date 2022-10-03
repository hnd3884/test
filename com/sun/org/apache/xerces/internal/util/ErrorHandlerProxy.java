package com.sun.org.apache.xerces.internal.util;

import org.xml.sax.SAXException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;

public abstract class ErrorHandlerProxy implements ErrorHandler
{
    @Override
    public void error(final SAXParseException e) throws SAXException {
        final XMLErrorHandler eh = this.getErrorHandler();
        if (eh instanceof ErrorHandlerWrapper) {
            ((ErrorHandlerWrapper)eh).fErrorHandler.error(e);
        }
        else {
            eh.error("", "", ErrorHandlerWrapper.createXMLParseException(e));
        }
    }
    
    @Override
    public void fatalError(final SAXParseException e) throws SAXException {
        final XMLErrorHandler eh = this.getErrorHandler();
        if (eh instanceof ErrorHandlerWrapper) {
            ((ErrorHandlerWrapper)eh).fErrorHandler.fatalError(e);
        }
        else {
            eh.fatalError("", "", ErrorHandlerWrapper.createXMLParseException(e));
        }
    }
    
    @Override
    public void warning(final SAXParseException e) throws SAXException {
        final XMLErrorHandler eh = this.getErrorHandler();
        if (eh instanceof ErrorHandlerWrapper) {
            ((ErrorHandlerWrapper)eh).fErrorHandler.warning(e);
        }
        else {
            eh.warning("", "", ErrorHandlerWrapper.createXMLParseException(e));
        }
    }
    
    protected abstract XMLErrorHandler getErrorHandler();
}
