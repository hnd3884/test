package com.adventnet.cache.dataobject;

public class CASObject
{
    private long casUnique;
    private Object cachedObject;
    
    public CASObject(final long casUnique, final Object cachedObject) {
        this.casUnique = 0L;
        this.cachedObject = null;
        this.casUnique = casUnique;
        this.cachedObject = cachedObject;
    }
    
    public Object getCachedObject() {
        return this.cachedObject;
    }
    
    public void setCachedObject(final Object cachedObject) {
        this.cachedObject = cachedObject;
    }
    
    public long getCasUnique() {
        return this.casUnique;
    }
    
    public void setCasUnique(final long casUnique) {
        this.casUnique = casUnique;
    }
}
