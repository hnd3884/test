package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.DOMLocator;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import org.w3c.dom.DOMError;

public class DOMErrorImpl implements DOMError
{
    public short fSeverity;
    public String fMessage;
    public DOMLocatorImpl fLocator;
    public Exception fException;
    public String fType;
    public Object fRelatedData;
    
    public DOMErrorImpl() {
        this.fSeverity = 1;
        this.fMessage = null;
        this.fLocator = new DOMLocatorImpl();
        this.fException = null;
    }
    
    public DOMErrorImpl(final short severity, final XMLParseException exception) {
        this.fSeverity = 1;
        this.fMessage = null;
        this.fLocator = new DOMLocatorImpl();
        this.fException = null;
        this.fSeverity = severity;
        this.fException = exception;
        this.fLocator = this.createDOMLocator(exception);
    }
    
    @Override
    public short getSeverity() {
        return this.fSeverity;
    }
    
    @Override
    public String getMessage() {
        return this.fMessage;
    }
    
    @Override
    public DOMLocator getLocation() {
        return this.fLocator;
    }
    
    private DOMLocatorImpl createDOMLocator(final XMLParseException exception) {
        return new DOMLocatorImpl(exception.getLineNumber(), exception.getColumnNumber(), exception.getCharacterOffset(), exception.getExpandedSystemId());
    }
    
    @Override
    public Object getRelatedException() {
        return this.fException;
    }
    
    public void reset() {
        this.fSeverity = 1;
        this.fException = null;
    }
    
    @Override
    public String getType() {
        return this.fType;
    }
    
    @Override
    public Object getRelatedData() {
        return this.fRelatedData;
    }
}
