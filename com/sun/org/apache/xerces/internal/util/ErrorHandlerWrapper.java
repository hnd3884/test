package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import org.xml.sax.ErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;

public class ErrorHandlerWrapper implements XMLErrorHandler
{
    protected ErrorHandler fErrorHandler;
    
    public ErrorHandlerWrapper() {
    }
    
    public ErrorHandlerWrapper(final ErrorHandler errorHandler) {
        this.setErrorHandler(errorHandler);
    }
    
    public void setErrorHandler(final ErrorHandler errorHandler) {
        this.fErrorHandler = errorHandler;
    }
    
    public ErrorHandler getErrorHandler() {
        return this.fErrorHandler;
    }
    
    @Override
    public void warning(final String domain, final String key, final XMLParseException exception) throws XNIException {
        if (this.fErrorHandler != null) {
            final SAXParseException saxException = createSAXParseException(exception);
            try {
                this.fErrorHandler.warning(saxException);
            }
            catch (final SAXParseException e) {
                throw createXMLParseException(e);
            }
            catch (final SAXException e2) {
                throw createXNIException(e2);
            }
        }
    }
    
    @Override
    public void error(final String domain, final String key, final XMLParseException exception) throws XNIException {
        if (this.fErrorHandler != null) {
            final SAXParseException saxException = createSAXParseException(exception);
            try {
                this.fErrorHandler.error(saxException);
            }
            catch (final SAXParseException e) {
                throw createXMLParseException(e);
            }
            catch (final SAXException e2) {
                throw createXNIException(e2);
            }
        }
    }
    
    @Override
    public void fatalError(final String domain, final String key, final XMLParseException exception) throws XNIException {
        if (this.fErrorHandler != null) {
            final SAXParseException saxException = createSAXParseException(exception);
            try {
                this.fErrorHandler.fatalError(saxException);
            }
            catch (final SAXParseException e) {
                throw createXMLParseException(e);
            }
            catch (final SAXException e2) {
                throw createXNIException(e2);
            }
        }
    }
    
    protected static SAXParseException createSAXParseException(final XMLParseException exception) {
        return new SAXParseException(exception.getMessage(), exception.getPublicId(), exception.getExpandedSystemId(), exception.getLineNumber(), exception.getColumnNumber(), exception.getException());
    }
    
    protected static XMLParseException createXMLParseException(final SAXParseException exception) {
        final String fPublicId = exception.getPublicId();
        final String fExpandedSystemId = exception.getSystemId();
        final int fLineNumber = exception.getLineNumber();
        final int fColumnNumber = exception.getColumnNumber();
        final XMLLocator location = new XMLLocator() {
            @Override
            public String getPublicId() {
                return fPublicId;
            }
            
            @Override
            public String getExpandedSystemId() {
                return fExpandedSystemId;
            }
            
            @Override
            public String getBaseSystemId() {
                return null;
            }
            
            @Override
            public String getLiteralSystemId() {
                return null;
            }
            
            @Override
            public int getColumnNumber() {
                return fColumnNumber;
            }
            
            @Override
            public int getLineNumber() {
                return fLineNumber;
            }
            
            @Override
            public int getCharacterOffset() {
                return -1;
            }
            
            @Override
            public String getEncoding() {
                return null;
            }
            
            @Override
            public String getXMLVersion() {
                return null;
            }
        };
        return new XMLParseException(location, exception.getMessage(), exception);
    }
    
    protected static XNIException createXNIException(final SAXException exception) {
        return new XNIException(exception.getMessage(), exception);
    }
}
