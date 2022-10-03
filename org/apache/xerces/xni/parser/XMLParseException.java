package org.apache.xerces.xni.parser;

import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XNIException;

public class XMLParseException extends XNIException
{
    static final long serialVersionUID = 1732959359448549967L;
    protected String fPublicId;
    protected String fLiteralSystemId;
    protected String fExpandedSystemId;
    protected String fBaseSystemId;
    protected int fLineNumber;
    protected int fColumnNumber;
    protected int fCharacterOffset;
    
    public XMLParseException(final XMLLocator xmlLocator, final String s) {
        super(s);
        this.fLineNumber = -1;
        this.fColumnNumber = -1;
        this.fCharacterOffset = -1;
        if (xmlLocator != null) {
            this.fPublicId = xmlLocator.getPublicId();
            this.fLiteralSystemId = xmlLocator.getLiteralSystemId();
            this.fExpandedSystemId = xmlLocator.getExpandedSystemId();
            this.fBaseSystemId = xmlLocator.getBaseSystemId();
            this.fLineNumber = xmlLocator.getLineNumber();
            this.fColumnNumber = xmlLocator.getColumnNumber();
            this.fCharacterOffset = xmlLocator.getCharacterOffset();
        }
    }
    
    public XMLParseException(final XMLLocator xmlLocator, final String s, final Exception ex) {
        super(s, ex);
        this.fLineNumber = -1;
        this.fColumnNumber = -1;
        this.fCharacterOffset = -1;
        if (xmlLocator != null) {
            this.fPublicId = xmlLocator.getPublicId();
            this.fLiteralSystemId = xmlLocator.getLiteralSystemId();
            this.fExpandedSystemId = xmlLocator.getExpandedSystemId();
            this.fBaseSystemId = xmlLocator.getBaseSystemId();
            this.fLineNumber = xmlLocator.getLineNumber();
            this.fColumnNumber = xmlLocator.getColumnNumber();
            this.fCharacterOffset = xmlLocator.getCharacterOffset();
        }
    }
    
    public String getPublicId() {
        return this.fPublicId;
    }
    
    public String getExpandedSystemId() {
        return this.fExpandedSystemId;
    }
    
    public String getLiteralSystemId() {
        return this.fLiteralSystemId;
    }
    
    public String getBaseSystemId() {
        return this.fBaseSystemId;
    }
    
    public int getLineNumber() {
        return this.fLineNumber;
    }
    
    public int getColumnNumber() {
        return this.fColumnNumber;
    }
    
    public int getCharacterOffset() {
        return this.fCharacterOffset;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.fPublicId != null) {
            sb.append(this.fPublicId);
        }
        sb.append(':');
        if (this.fLiteralSystemId != null) {
            sb.append(this.fLiteralSystemId);
        }
        sb.append(':');
        if (this.fExpandedSystemId != null) {
            sb.append(this.fExpandedSystemId);
        }
        sb.append(':');
        if (this.fBaseSystemId != null) {
            sb.append(this.fBaseSystemId);
        }
        sb.append(':');
        sb.append(this.fLineNumber);
        sb.append(':');
        sb.append(this.fColumnNumber);
        sb.append(':');
        sb.append(this.fCharacterOffset);
        sb.append(':');
        String s = this.getMessage();
        if (s == null) {
            final Exception exception = this.getException();
            if (exception != null) {
                s = exception.getMessage();
            }
        }
        if (s != null) {
            sb.append(s);
        }
        return sb.toString();
    }
}
