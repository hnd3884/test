package org.apache.tomcat.util.net.openssl;

import java.util.NoSuchElementException;
import org.apache.tomcat.jni.SSLContext;
import javax.net.ssl.SSLSession;
import java.util.Enumeration;
import org.apache.tomcat.util.res.StringManager;
import javax.net.ssl.SSLSessionContext;

public class OpenSSLSessionContext implements SSLSessionContext
{
    private static final StringManager sm;
    private static final Enumeration<byte[]> EMPTY;
    private final OpenSSLSessionStats stats;
    private final OpenSSLContext context;
    private final long contextID;
    
    OpenSSLSessionContext(final OpenSSLContext context) {
        this.context = context;
        this.contextID = context.getSSLContextID();
        this.stats = new OpenSSLSessionStats(this.contextID);
    }
    
    @Override
    public SSLSession getSession(final byte[] bytes) {
        return null;
    }
    
    @Override
    public Enumeration<byte[]> getIds() {
        return OpenSSLSessionContext.EMPTY;
    }
    
    public void setTicketKeys(final byte[] keys) {
        if (keys == null) {
            throw new IllegalArgumentException(OpenSSLSessionContext.sm.getString("sessionContext.nullTicketKeys"));
        }
        SSLContext.setSessionTicketKeys(this.contextID, keys);
    }
    
    public void setSessionCacheEnabled(final boolean enabled) {
        final long mode = enabled ? 2L : 0L;
        SSLContext.setSessionCacheMode(this.contextID, mode);
    }
    
    public boolean isSessionCacheEnabled() {
        return SSLContext.getSessionCacheMode(this.contextID) == 2L;
    }
    
    public OpenSSLSessionStats stats() {
        return this.stats;
    }
    
    @Override
    public void setSessionTimeout(final int seconds) {
        if (seconds < 0) {
            throw new IllegalArgumentException();
        }
        SSLContext.setSessionCacheTimeout(this.contextID, (long)seconds);
    }
    
    @Override
    public int getSessionTimeout() {
        return (int)SSLContext.getSessionCacheTimeout(this.contextID);
    }
    
    @Override
    public void setSessionCacheSize(final int size) {
        if (size < 0) {
            throw new IllegalArgumentException();
        }
        SSLContext.setSessionCacheSize(this.contextID, (long)size);
    }
    
    @Override
    public int getSessionCacheSize() {
        return (int)SSLContext.getSessionCacheSize(this.contextID);
    }
    
    public boolean setSessionIdContext(final byte[] sidCtx) {
        return SSLContext.setSessionIdContext(this.contextID, sidCtx);
    }
    
    static {
        sm = StringManager.getManager((Class)OpenSSLSessionContext.class);
        EMPTY = new EmptyEnumeration();
    }
    
    private static final class EmptyEnumeration implements Enumeration<byte[]>
    {
        @Override
        public boolean hasMoreElements() {
            return false;
        }
        
        @Override
        public byte[] nextElement() {
            throw new NoSuchElementException();
        }
    }
}
