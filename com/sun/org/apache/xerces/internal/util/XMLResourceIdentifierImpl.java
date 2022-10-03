package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;

public class XMLResourceIdentifierImpl implements XMLResourceIdentifier
{
    protected String fPublicId;
    protected String fLiteralSystemId;
    protected String fBaseSystemId;
    protected String fExpandedSystemId;
    protected String fNamespace;
    
    public XMLResourceIdentifierImpl() {
    }
    
    public XMLResourceIdentifierImpl(final String publicId, final String literalSystemId, final String baseSystemId, final String expandedSystemId) {
        this.setValues(publicId, literalSystemId, baseSystemId, expandedSystemId, null);
    }
    
    public XMLResourceIdentifierImpl(final String publicId, final String literalSystemId, final String baseSystemId, final String expandedSystemId, final String namespace) {
        this.setValues(publicId, literalSystemId, baseSystemId, expandedSystemId, namespace);
    }
    
    public void setValues(final String publicId, final String literalSystemId, final String baseSystemId, final String expandedSystemId) {
        this.setValues(publicId, literalSystemId, baseSystemId, expandedSystemId, null);
    }
    
    public void setValues(final String publicId, final String literalSystemId, final String baseSystemId, final String expandedSystemId, final String namespace) {
        this.fPublicId = publicId;
        this.fLiteralSystemId = literalSystemId;
        this.fBaseSystemId = baseSystemId;
        this.fExpandedSystemId = expandedSystemId;
        this.fNamespace = namespace;
    }
    
    public void clear() {
        this.fPublicId = null;
        this.fLiteralSystemId = null;
        this.fBaseSystemId = null;
        this.fExpandedSystemId = null;
        this.fNamespace = null;
    }
    
    @Override
    public void setPublicId(final String publicId) {
        this.fPublicId = publicId;
    }
    
    @Override
    public void setLiteralSystemId(final String literalSystemId) {
        this.fLiteralSystemId = literalSystemId;
    }
    
    @Override
    public void setBaseSystemId(final String baseSystemId) {
        this.fBaseSystemId = baseSystemId;
    }
    
    @Override
    public void setExpandedSystemId(final String expandedSystemId) {
        this.fExpandedSystemId = expandedSystemId;
    }
    
    @Override
    public void setNamespace(final String namespace) {
        this.fNamespace = namespace;
    }
    
    @Override
    public String getPublicId() {
        return this.fPublicId;
    }
    
    @Override
    public String getLiteralSystemId() {
        return this.fLiteralSystemId;
    }
    
    @Override
    public String getBaseSystemId() {
        return this.fBaseSystemId;
    }
    
    @Override
    public String getExpandedSystemId() {
        return this.fExpandedSystemId;
    }
    
    @Override
    public String getNamespace() {
        return this.fNamespace;
    }
    
    @Override
    public int hashCode() {
        int code = 0;
        if (this.fPublicId != null) {
            code += this.fPublicId.hashCode();
        }
        if (this.fLiteralSystemId != null) {
            code += this.fLiteralSystemId.hashCode();
        }
        if (this.fBaseSystemId != null) {
            code += this.fBaseSystemId.hashCode();
        }
        if (this.fExpandedSystemId != null) {
            code += this.fExpandedSystemId.hashCode();
        }
        if (this.fNamespace != null) {
            code += this.fNamespace.hashCode();
        }
        return code;
    }
    
    @Override
    public String toString() {
        final StringBuffer str = new StringBuffer();
        if (this.fPublicId != null) {
            str.append(this.fPublicId);
        }
        str.append(':');
        if (this.fLiteralSystemId != null) {
            str.append(this.fLiteralSystemId);
        }
        str.append(':');
        if (this.fBaseSystemId != null) {
            str.append(this.fBaseSystemId);
        }
        str.append(':');
        if (this.fExpandedSystemId != null) {
            str.append(this.fExpandedSystemId);
        }
        str.append(':');
        if (this.fNamespace != null) {
            str.append(this.fNamespace);
        }
        return str.toString();
    }
}
