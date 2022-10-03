package com.sun.org.apache.xerces.internal.util;

final class XMLErrorCode
{
    private String fDomain;
    private String fKey;
    
    public XMLErrorCode(final String domain, final String key) {
        this.fDomain = domain;
        this.fKey = key;
    }
    
    public void setValues(final String domain, final String key) {
        this.fDomain = domain;
        this.fKey = key;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof XMLErrorCode)) {
            return false;
        }
        final XMLErrorCode err = (XMLErrorCode)obj;
        return this.fDomain.equals(err.fDomain) && this.fKey.equals(err.fKey);
    }
    
    @Override
    public int hashCode() {
        return this.fDomain.hashCode() + this.fKey.hashCode();
    }
}
