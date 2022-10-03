package org.openjsse.sun.security.ssl;

class SecureKey
{
    private static final Object nullObject;
    private final Object appKey;
    private final Object securityCtx;
    
    static Object getCurrentSecurityContext() {
        final SecurityManager sm = System.getSecurityManager();
        Object context = null;
        if (sm != null) {
            context = sm.getSecurityContext();
        }
        if (context == null) {
            context = SecureKey.nullObject;
        }
        return context;
    }
    
    SecureKey(final Object key) {
        this.appKey = key;
        this.securityCtx = getCurrentSecurityContext();
    }
    
    Object getAppKey() {
        return this.appKey;
    }
    
    Object getSecurityContext() {
        return this.securityCtx;
    }
    
    @Override
    public int hashCode() {
        return this.appKey.hashCode() ^ this.securityCtx.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof SecureKey && ((SecureKey)o).appKey.equals(this.appKey) && ((SecureKey)o).securityCtx.equals(this.securityCtx);
    }
    
    static {
        nullObject = new Object();
    }
}
