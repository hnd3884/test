package com.fasterxml.jackson.dataformat.xml.util;

public class XmlInfo
{
    protected final String _namespace;
    protected final boolean _isAttribute;
    protected final boolean _isText;
    protected final boolean _isCData;
    
    public XmlInfo(final Boolean isAttribute, final String ns, final Boolean isText, final Boolean isCData) {
        this._isAttribute = (isAttribute != null && isAttribute);
        this._namespace = ((ns == null) ? "" : ns);
        this._isText = (isText != null && isText);
        this._isCData = (isCData != null && isCData);
    }
    
    public String getNamespace() {
        return this._namespace;
    }
    
    public boolean isAttribute() {
        return this._isAttribute;
    }
    
    public boolean isText() {
        return this._isText;
    }
    
    public boolean isCData() {
        return this._isCData;
    }
}
