package com.fasterxml.jackson.dataformat.xml.deser;

class ElementWrapper
{
    protected final ElementWrapper _parent;
    protected final String _wrapperName;
    protected final String _wrapperNamespace;
    
    private ElementWrapper(final ElementWrapper parent) {
        this._parent = parent;
        this._wrapperName = null;
        this._wrapperNamespace = "";
    }
    
    private ElementWrapper(final ElementWrapper parent, final String wrapperLocalName, final String wrapperNamespace) {
        this._parent = parent;
        this._wrapperName = wrapperLocalName;
        this._wrapperNamespace = ((wrapperNamespace == null) ? "" : wrapperNamespace);
    }
    
    public static ElementWrapper matchingWrapper(final ElementWrapper parent, final String wrapperLocalName, final String wrapperNamespace) {
        return new ElementWrapper(parent, wrapperLocalName, wrapperNamespace);
    }
    
    public ElementWrapper intermediateWrapper() {
        return new ElementWrapper(this, null, null);
    }
    
    public boolean isMatching() {
        return this._wrapperName != null;
    }
    
    public String getWrapperLocalName() {
        return this._wrapperName;
    }
    
    public String getWrapperNamespace() {
        return this._wrapperNamespace;
    }
    
    public ElementWrapper getParent() {
        return this._parent;
    }
    
    public boolean matchesWrapper(final String localName, String ns) {
        if (this._wrapperName == null) {
            return true;
        }
        if (ns == null) {
            ns = "";
        }
        return this._wrapperName.equals(localName) && this._wrapperNamespace.equals(ns);
    }
    
    @Override
    public String toString() {
        if (this._parent == null) {
            return "Wrapper: ROOT, matching: " + this._wrapperName;
        }
        if (this._wrapperName == null) {
            return "Wrapper: empty";
        }
        return "Wrapper: branch, matching: " + this._wrapperName;
    }
}
