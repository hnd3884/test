package org.apache.xerces.util;

import org.apache.xerces.xni.XMLResourceIdentifier;

public class XMLResourceIdentifierImpl implements XMLResourceIdentifier
{
    protected String fPublicId;
    protected String fLiteralSystemId;
    protected String fBaseSystemId;
    protected String fExpandedSystemId;
    protected String fNamespace;
    
    public XMLResourceIdentifierImpl() {
    }
    
    public XMLResourceIdentifierImpl(final String s, final String s2, final String s3, final String s4) {
        this.setValues(s, s2, s3, s4, null);
    }
    
    public XMLResourceIdentifierImpl(final String s, final String s2, final String s3, final String s4, final String s5) {
        this.setValues(s, s2, s3, s4, s5);
    }
    
    public void setValues(final String s, final String s2, final String s3, final String s4) {
        this.setValues(s, s2, s3, s4, null);
    }
    
    public void setValues(final String fPublicId, final String fLiteralSystemId, final String fBaseSystemId, final String fExpandedSystemId, final String fNamespace) {
        this.fPublicId = fPublicId;
        this.fLiteralSystemId = fLiteralSystemId;
        this.fBaseSystemId = fBaseSystemId;
        this.fExpandedSystemId = fExpandedSystemId;
        this.fNamespace = fNamespace;
    }
    
    public void clear() {
        this.fPublicId = null;
        this.fLiteralSystemId = null;
        this.fBaseSystemId = null;
        this.fExpandedSystemId = null;
        this.fNamespace = null;
    }
    
    public void setPublicId(final String fPublicId) {
        this.fPublicId = fPublicId;
    }
    
    public void setLiteralSystemId(final String fLiteralSystemId) {
        this.fLiteralSystemId = fLiteralSystemId;
    }
    
    public void setBaseSystemId(final String fBaseSystemId) {
        this.fBaseSystemId = fBaseSystemId;
    }
    
    public void setExpandedSystemId(final String fExpandedSystemId) {
        this.fExpandedSystemId = fExpandedSystemId;
    }
    
    public void setNamespace(final String fNamespace) {
        this.fNamespace = fNamespace;
    }
    
    public String getPublicId() {
        return this.fPublicId;
    }
    
    public String getLiteralSystemId() {
        return this.fLiteralSystemId;
    }
    
    public String getBaseSystemId() {
        return this.fBaseSystemId;
    }
    
    public String getExpandedSystemId() {
        return this.fExpandedSystemId;
    }
    
    public String getNamespace() {
        return this.fNamespace;
    }
    
    public int hashCode() {
        int n = 0;
        if (this.fPublicId != null) {
            n += this.fPublicId.hashCode();
        }
        if (this.fLiteralSystemId != null) {
            n += this.fLiteralSystemId.hashCode();
        }
        if (this.fBaseSystemId != null) {
            n += this.fBaseSystemId.hashCode();
        }
        if (this.fExpandedSystemId != null) {
            n += this.fExpandedSystemId.hashCode();
        }
        if (this.fNamespace != null) {
            n += this.fNamespace.hashCode();
        }
        return n;
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
        if (this.fBaseSystemId != null) {
            sb.append(this.fBaseSystemId);
        }
        sb.append(':');
        if (this.fExpandedSystemId != null) {
            sb.append(this.fExpandedSystemId);
        }
        sb.append(':');
        if (this.fNamespace != null) {
            sb.append(this.fNamespace);
        }
        return sb.toString();
    }
}
