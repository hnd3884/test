package org.apache.xerces.util;

import org.xml.sax.SAXException;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;

public abstract class ErrorHandlerProxy implements ErrorHandler
{
    public void error(final SAXParseException ex) throws SAXException {
        final XMLErrorHandler errorHandler = this.getErrorHandler();
        if (errorHandler instanceof ErrorHandlerWrapper) {
            ((ErrorHandlerWrapper)errorHandler).fErrorHandler.error(ex);
        }
        else {
            errorHandler.error("", "", ErrorHandlerWrapper.createXMLParseException(ex));
        }
    }
    
    public void fatalError(final SAXParseException ex) throws SAXException {
        final XMLErrorHandler errorHandler = this.getErrorHandler();
        if (errorHandler instanceof ErrorHandlerWrapper) {
            ((ErrorHandlerWrapper)errorHandler).fErrorHandler.fatalError(ex);
        }
        else {
            errorHandler.fatalError("", "", ErrorHandlerWrapper.createXMLParseException(ex));
        }
    }
    
    public void warning(final SAXParseException ex) throws SAXException {
        final XMLErrorHandler errorHandler = this.getErrorHandler();
        if (errorHandler instanceof ErrorHandlerWrapper) {
            ((ErrorHandlerWrapper)errorHandler).fErrorHandler.warning(ex);
        }
        else {
            errorHandler.warning("", "", ErrorHandlerWrapper.createXMLParseException(ex));
        }
    }
    
    protected abstract XMLErrorHandler getErrorHandler();
}
