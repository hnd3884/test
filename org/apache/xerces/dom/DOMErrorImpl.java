package org.apache.xerces.dom;

import org.w3c.dom.DOMLocator;
import org.apache.xerces.xni.parser.XMLParseException;
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
    
    public DOMErrorImpl(final short fSeverity, final XMLParseException fException) {
        this.fSeverity = 1;
        this.fMessage = null;
        this.fLocator = new DOMLocatorImpl();
        this.fException = null;
        this.fSeverity = fSeverity;
        this.fException = fException;
        this.fLocator = this.createDOMLocator(fException);
    }
    
    public short getSeverity() {
        return this.fSeverity;
    }
    
    public String getMessage() {
        return this.fMessage;
    }
    
    public DOMLocator getLocation() {
        return this.fLocator;
    }
    
    private DOMLocatorImpl createDOMLocator(final XMLParseException ex) {
        return new DOMLocatorImpl(ex.getLineNumber(), ex.getColumnNumber(), ex.getCharacterOffset(), ex.getExpandedSystemId());
    }
    
    public Object getRelatedException() {
        return this.fException;
    }
    
    public void reset() {
        this.fSeverity = 1;
        this.fException = null;
    }
    
    public String getType() {
        return this.fType;
    }
    
    public Object getRelatedData() {
        return this.fRelatedData;
    }
}
