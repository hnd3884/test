package org.apache.xerces.util;

import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XNIException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.apache.xerces.xni.parser.XMLParseException;
import org.xml.sax.ErrorHandler;
import org.apache.xerces.xni.parser.XMLErrorHandler;

public class ErrorHandlerWrapper implements XMLErrorHandler
{
    protected ErrorHandler fErrorHandler;
    
    public ErrorHandlerWrapper() {
    }
    
    public ErrorHandlerWrapper(final ErrorHandler errorHandler) {
        this.setErrorHandler(errorHandler);
    }
    
    public void setErrorHandler(final ErrorHandler fErrorHandler) {
        this.fErrorHandler = fErrorHandler;
    }
    
    public ErrorHandler getErrorHandler() {
        return this.fErrorHandler;
    }
    
    public void warning(final String s, final String s2, final XMLParseException ex) throws XNIException {
        if (this.fErrorHandler != null) {
            final SAXParseException saxParseException = createSAXParseException(ex);
            try {
                this.fErrorHandler.warning(saxParseException);
            }
            catch (final SAXParseException ex2) {
                throw createXMLParseException(ex2);
            }
            catch (final SAXException ex3) {
                throw createXNIException(ex3);
            }
        }
    }
    
    public void error(final String s, final String s2, final XMLParseException ex) throws XNIException {
        if (this.fErrorHandler != null) {
            final SAXParseException saxParseException = createSAXParseException(ex);
            try {
                this.fErrorHandler.error(saxParseException);
            }
            catch (final SAXParseException ex2) {
                throw createXMLParseException(ex2);
            }
            catch (final SAXException ex3) {
                throw createXNIException(ex3);
            }
        }
    }
    
    public void fatalError(final String s, final String s2, final XMLParseException ex) throws XNIException {
        if (this.fErrorHandler != null) {
            final SAXParseException saxParseException = createSAXParseException(ex);
            try {
                this.fErrorHandler.fatalError(saxParseException);
            }
            catch (final SAXParseException ex2) {
                throw createXMLParseException(ex2);
            }
            catch (final SAXException ex3) {
                throw createXNIException(ex3);
            }
        }
    }
    
    protected static SAXParseException createSAXParseException(final XMLParseException ex) {
        return new SAXParseException(ex.getMessage(), ex.getPublicId(), ex.getExpandedSystemId(), ex.getLineNumber(), ex.getColumnNumber(), ex.getException());
    }
    
    protected static XMLParseException createXMLParseException(final SAXParseException ex) {
        return new XMLParseException(new XMLLocator() {
            private final /* synthetic */ String val$fPublicId = ex.getPublicId();
            private final /* synthetic */ String val$fExpandedSystemId = ex.getSystemId();
            private final /* synthetic */ int val$fColumnNumber = ex.getColumnNumber();
            private final /* synthetic */ int val$fLineNumber = ex.getLineNumber();
            
            public String getPublicId() {
                return this.val$fPublicId;
            }
            
            public String getExpandedSystemId() {
                return this.val$fExpandedSystemId;
            }
            
            public String getBaseSystemId() {
                return null;
            }
            
            public String getLiteralSystemId() {
                return null;
            }
            
            public int getColumnNumber() {
                return this.val$fColumnNumber;
            }
            
            public int getLineNumber() {
                return this.val$fLineNumber;
            }
            
            public int getCharacterOffset() {
                return -1;
            }
            
            public String getEncoding() {
                return null;
            }
            
            public String getXMLVersion() {
                return null;
            }
        }, ex.getMessage(), ex);
    }
    
    protected static XNIException createXNIException(final SAXException ex) {
        return new XNIException(ex.getMessage(), ex);
    }
}
