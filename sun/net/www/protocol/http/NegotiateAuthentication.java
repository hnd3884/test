package sun.net.www.protocol.http;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.io.IOException;
import java.util.Base64;
import sun.net.www.HeaderParser;
import java.net.URL;
import java.net.Authenticator;
import java.util.HashMap;

class NegotiateAuthentication extends AuthenticationInfo
{
    private static final long serialVersionUID = 100L;
    private final HttpCallerInfo hci;
    static HashMap<String, Boolean> supported;
    static ThreadLocal<HashMap<String, Negotiator>> cache;
    private static final boolean cacheSPNEGO;
    private Negotiator negotiator;
    
    public NegotiateAuthentication(final HttpCallerInfo hci) {
        super((Authenticator.RequestorType.PROXY == hci.authType) ? 'p' : 's', hci.scheme.equalsIgnoreCase("Negotiate") ? AuthScheme.NEGOTIATE : AuthScheme.KERBEROS, hci.url, "");
        this.negotiator = null;
        this.hci = hci;
    }
    
    @Override
    public boolean supportsPreemptiveAuthorization() {
        return false;
    }
    
    public static synchronized boolean isSupported(final HttpCallerInfo httpCallerInfo) {
        if (NegotiateAuthentication.supported == null) {
            NegotiateAuthentication.supported = new HashMap<String, Boolean>();
        }
        final String lowerCase = httpCallerInfo.host.toLowerCase();
        if (NegotiateAuthentication.supported.containsKey(lowerCase)) {
            return NegotiateAuthentication.supported.get(lowerCase);
        }
        final Negotiator negotiator = Negotiator.getNegotiator(httpCallerInfo);
        if (negotiator != null) {
            NegotiateAuthentication.supported.put(lowerCase, true);
            if (NegotiateAuthentication.cache == null) {
                NegotiateAuthentication.cache = new ThreadLocal<HashMap<String, Negotiator>>() {
                    @Override
                    protected HashMap<String, Negotiator> initialValue() {
                        return new HashMap<String, Negotiator>();
                    }
                };
            }
            NegotiateAuthentication.cache.get().put(lowerCase, negotiator);
            return true;
        }
        NegotiateAuthentication.supported.put(lowerCase, false);
        return false;
    }
    
    private static synchronized HashMap<String, Negotiator> getCache() {
        if (NegotiateAuthentication.cache == null) {
            return null;
        }
        return NegotiateAuthentication.cache.get();
    }
    
    @Override
    protected boolean useAuthCache() {
        return super.useAuthCache() && NegotiateAuthentication.cacheSPNEGO;
    }
    
    @Override
    public String getHeaderValue(final URL url, final String s) {
        throw new RuntimeException("getHeaderValue not supported");
    }
    
    @Override
    public boolean isAuthorizationStale(final String s) {
        return false;
    }
    
    @Override
    public synchronized boolean setHeaders(final HttpURLConnection httpURLConnection, final HeaderParser headerParser, final String s) {
        try {
            byte[] decode = null;
            final String[] split = s.split("\\s+");
            if (split.length > 1) {
                decode = Base64.getDecoder().decode(split[1]);
            }
            httpURLConnection.setAuthenticationProperty(this.getHeaderName(), this.hci.scheme + " " + Base64.getEncoder().encodeToString((decode == null) ? this.firstToken() : this.nextToken(decode)));
            return true;
        }
        catch (final IOException ex) {
            return false;
        }
    }
    
    private byte[] firstToken() throws IOException {
        this.negotiator = null;
        final HashMap<String, Negotiator> cache = getCache();
        if (cache != null) {
            this.negotiator = cache.get(this.getHost());
            if (this.negotiator != null) {
                cache.remove(this.getHost());
            }
        }
        if (this.negotiator == null) {
            this.negotiator = Negotiator.getNegotiator(this.hci);
            if (this.negotiator == null) {
                throw new IOException("Cannot initialize Negotiator");
            }
        }
        return this.negotiator.firstToken();
    }
    
    private byte[] nextToken(final byte[] array) throws IOException {
        return this.negotiator.nextToken(array);
    }
    
    static {
        NegotiateAuthentication.supported = null;
        NegotiateAuthentication.cache = null;
        cacheSPNEGO = Boolean.parseBoolean(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jdk.spnego.cache", "true")));
    }
}
