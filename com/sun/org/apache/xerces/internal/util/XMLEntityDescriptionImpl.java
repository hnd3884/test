package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.impl.XMLEntityDescription;

public class XMLEntityDescriptionImpl extends XMLResourceIdentifierImpl implements XMLEntityDescription
{
    protected String fEntityName;
    
    public XMLEntityDescriptionImpl() {
    }
    
    public XMLEntityDescriptionImpl(final String entityName, final String publicId, final String literalSystemId, final String baseSystemId, final String expandedSystemId) {
        this.setDescription(entityName, publicId, literalSystemId, baseSystemId, expandedSystemId);
    }
    
    public XMLEntityDescriptionImpl(final String entityName, final String publicId, final String literalSystemId, final String baseSystemId, final String expandedSystemId, final String namespace) {
        this.setDescription(entityName, publicId, literalSystemId, baseSystemId, expandedSystemId, namespace);
    }
    
    @Override
    public void setEntityName(final String name) {
        this.fEntityName = name;
    }
    
    @Override
    public String getEntityName() {
        return this.fEntityName;
    }
    
    public void setDescription(final String entityName, final String publicId, final String literalSystemId, final String baseSystemId, final String expandedSystemId) {
        this.setDescription(entityName, publicId, literalSystemId, baseSystemId, expandedSystemId, null);
    }
    
    public void setDescription(final String entityName, final String publicId, final String literalSystemId, final String baseSystemId, final String expandedSystemId, final String namespace) {
        this.fEntityName = entityName;
        this.setValues(publicId, literalSystemId, baseSystemId, expandedSystemId, namespace);
    }
    
    @Override
    public void clear() {
        super.clear();
        this.fEntityName = null;
    }
    
    @Override
    public int hashCode() {
        int code = super.hashCode();
        if (this.fEntityName != null) {
            code += this.fEntityName.hashCode();
        }
        return code;
    }
    
    @Override
    public String toString() {
        final StringBuffer str = new StringBuffer();
        if (this.fEntityName != null) {
            str.append(this.fEntityName);
        }
        str.append(':');
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
