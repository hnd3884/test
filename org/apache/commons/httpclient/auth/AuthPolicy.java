package org.apache.commons.httpclient.auth;

import org.apache.commons.logging.LogFactory;
import java.util.List;
import org.apache.commons.logging.Log;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class AuthPolicy
{
    private static final HashMap SCHEMES;
    private static final ArrayList SCHEME_LIST;
    public static final String AUTH_SCHEME_PRIORITY = "http.auth.scheme-priority";
    public static final String NTLM = "NTLM";
    public static final String DIGEST = "Digest";
    public static final String BASIC = "Basic";
    protected static final Log LOG;
    
    public static synchronized void registerAuthScheme(final String id, final Class clazz) {
        if (id == null) {
            throw new IllegalArgumentException("Id may not be null");
        }
        if (clazz == null) {
            throw new IllegalArgumentException("Authentication scheme class may not be null");
        }
        AuthPolicy.SCHEMES.put(id.toLowerCase(), clazz);
        AuthPolicy.SCHEME_LIST.add(id.toLowerCase());
    }
    
    public static synchronized void unregisterAuthScheme(final String id) {
        if (id == null) {
            throw new IllegalArgumentException("Id may not be null");
        }
        AuthPolicy.SCHEMES.remove(id.toLowerCase());
        AuthPolicy.SCHEME_LIST.remove(id.toLowerCase());
    }
    
    public static synchronized AuthScheme getAuthScheme(final String id) throws IllegalStateException {
        if (id == null) {
            throw new IllegalArgumentException("Id may not be null");
        }
        final Class clazz = AuthPolicy.SCHEMES.get(id.toLowerCase());
        if (clazz != null) {
            try {
                return clazz.newInstance();
            }
            catch (final Exception e) {
                AuthPolicy.LOG.error((Object)("Error initializing authentication scheme: " + id), (Throwable)e);
                throw new IllegalStateException(id + " authentication scheme implemented by " + clazz.getName() + " could not be initialized");
            }
        }
        throw new IllegalStateException("Unsupported authentication scheme " + id);
    }
    
    public static synchronized List getDefaultAuthPrefs() {
        return (List)AuthPolicy.SCHEME_LIST.clone();
    }
    
    static {
        SCHEMES = new HashMap();
        SCHEME_LIST = new ArrayList();
        registerAuthScheme("NTLM", NTLMScheme.class);
        registerAuthScheme("Digest", DigestScheme.class);
        registerAuthScheme("Basic", BasicScheme.class);
        LOG = LogFactory.getLog(AuthPolicy.class);
    }
}
