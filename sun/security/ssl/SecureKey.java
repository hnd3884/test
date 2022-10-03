package sun.security.ssl;

class SecureKey
{
    private static final Object nullObject;
    private final Object appKey;
    private final Object securityCtx;
    
    static Object getCurrentSecurityContext() {
        final SecurityManager securityManager = System.getSecurityManager();
        Object o = null;
        if (securityManager != null) {
            o = securityManager.getSecurityContext();
        }
        if (o == null) {
            o = SecureKey.nullObject;
        }
        return o;
    }
    
    SecureKey(final Object appKey) {
        this.appKey = appKey;
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
