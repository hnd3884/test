package org.apache.xerces.util;

import org.apache.xerces.impl.XMLEntityDescription;

public class XMLEntityDescriptionImpl extends XMLResourceIdentifierImpl implements XMLEntityDescription
{
    protected String fEntityName;
    
    public XMLEntityDescriptionImpl() {
    }
    
    public XMLEntityDescriptionImpl(final String s, final String s2, final String s3, final String s4, final String s5) {
        this.setDescription(s, s2, s3, s4, s5);
    }
    
    public XMLEntityDescriptionImpl(final String s, final String s2, final String s3, final String s4, final String s5, final String s6) {
        this.setDescription(s, s2, s3, s4, s5, s6);
    }
    
    public void setEntityName(final String fEntityName) {
        this.fEntityName = fEntityName;
    }
    
    public String getEntityName() {
        return this.fEntityName;
    }
    
    public void setDescription(final String s, final String s2, final String s3, final String s4, final String s5) {
        this.setDescription(s, s2, s3, s4, s5, null);
    }
    
    public void setDescription(final String fEntityName, final String s, final String s2, final String s3, final String s4, final String s5) {
        this.fEntityName = fEntityName;
        this.setValues(s, s2, s3, s4, s5);
    }
    
    public void clear() {
        super.clear();
        this.fEntityName = null;
    }
    
    public int hashCode() {
        int hashCode = super.hashCode();
        if (this.fEntityName != null) {
            hashCode += this.fEntityName.hashCode();
        }
        return hashCode;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.fEntityName != null) {
            sb.append(this.fEntityName);
        }
        sb.append(':');
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
