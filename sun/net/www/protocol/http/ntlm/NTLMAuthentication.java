package sun.net.www.protocol.http.ntlm;

import sun.net.NetProperties;
import sun.security.action.GetPropertyAction;
import java.io.IOException;
import sun.net.www.HeaderParser;
import sun.net.www.protocol.http.HttpURLConnection;
import sun.net.www.protocol.http.AuthScheme;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.security.AccessController;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.security.PrivilegedAction;
import sun.net.www.protocol.http.AuthenticationInfo;

public class NTLMAuthentication extends AuthenticationInfo
{
    private static final long serialVersionUID = 100L;
    private static final NTLMAuthenticationCallback NTLMAuthCallback;
    private String hostname;
    private static String defaultDomain;
    private static final boolean ntlmCache;
    private static final TransparentAuth authMode;
    String username;
    String ntdomain;
    String password;
    private static final boolean isTrustedSiteAvailable;
    
    private void init0() {
        this.hostname = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                String upperCase;
                try {
                    upperCase = InetAddress.getLocalHost().getHostName().toUpperCase();
                }
                catch (final UnknownHostException ex) {
                    upperCase = "localhost";
                }
                return upperCase;
            }
        });
        final int index = this.hostname.indexOf(46);
        if (index != -1) {
            this.hostname = this.hostname.substring(0, index);
        }
    }
    
    public NTLMAuthentication(final boolean b, final URL url, final PasswordAuthentication passwordAuthentication) {
        super(b ? 'p' : 's', AuthScheme.NTLM, url, "");
        this.init(passwordAuthentication);
    }
    
    private void init(final PasswordAuthentication pw) {
        this.pw = pw;
        if (pw != null) {
            final String userName = pw.getUserName();
            final int index = userName.indexOf(92);
            if (index == -1) {
                this.username = userName;
                this.ntdomain = NTLMAuthentication.defaultDomain;
            }
            else {
                this.ntdomain = userName.substring(0, index).toUpperCase();
                this.username = userName.substring(index + 1);
            }
            this.password = new String(pw.getPassword());
        }
        else {
            this.username = null;
            this.ntdomain = null;
            this.password = null;
        }
        this.init0();
    }
    
    public NTLMAuthentication(final boolean b, final String s, final int n, final PasswordAuthentication passwordAuthentication) {
        super(b ? 'p' : 's', AuthScheme.NTLM, s, n, "");
        this.init(passwordAuthentication);
    }
    
    @Override
    protected boolean useAuthCache() {
        return NTLMAuthentication.ntlmCache && super.useAuthCache();
    }
    
    @Override
    public boolean supportsPreemptiveAuthorization() {
        return false;
    }
    
    public static boolean supportsTransparentAuth() {
        return true;
    }
    
    public static boolean isTrustedSite(final URL url) {
        if (NTLMAuthentication.NTLMAuthCallback != null) {
            return NTLMAuthentication.NTLMAuthCallback.isTrustedSite(url);
        }
        switch (NTLMAuthentication.authMode) {
            case TRUSTED_HOSTS: {
                return isTrustedSite(url.toString());
            }
            case ALL_HOSTS: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private static native boolean isTrustedSiteAvailable();
    
    private static boolean isTrustedSite(final String s) {
        return NTLMAuthentication.isTrustedSiteAvailable && isTrustedSite0(s);
    }
    
    private static native boolean isTrustedSite0(final String p0);
    
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
            NTLMAuthSequence ntlmAuthSequence = (NTLMAuthSequence)httpURLConnection.authObj();
            if (ntlmAuthSequence == null) {
                ntlmAuthSequence = new NTLMAuthSequence(this.username, this.password, this.ntdomain);
                httpURLConnection.authObj(ntlmAuthSequence);
            }
            httpURLConnection.setAuthenticationProperty(this.getHeaderName(), "NTLM " + ntlmAuthSequence.getAuthHeader((s.length() > 6) ? s.substring(5) : null));
            if (ntlmAuthSequence.isComplete()) {
                httpURLConnection.authObj(null);
            }
            return true;
        }
        catch (final IOException ex) {
            httpURLConnection.authObj(null);
            return false;
        }
    }
    
    static {
        NTLMAuthCallback = NTLMAuthenticationCallback.getNTLMAuthenticationCallback();
        NTLMAuthentication.defaultDomain = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("http.auth.ntlm.domain", "domain"));
        ntlmCache = Boolean.parseBoolean(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jdk.ntlm.cache", "true")));
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                return NetProperties.get("jdk.http.ntlm.transparentAuth");
            }
        });
        if ("trustedHosts".equalsIgnoreCase(s)) {
            authMode = TransparentAuth.TRUSTED_HOSTS;
        }
        else if ("allHosts".equalsIgnoreCase(s)) {
            authMode = TransparentAuth.ALL_HOSTS;
        }
        else {
            authMode = TransparentAuth.DISABLED;
        }
        isTrustedSiteAvailable = isTrustedSiteAvailable();
    }
    
    enum TransparentAuth
    {
        DISABLED, 
        TRUSTED_HOSTS, 
        ALL_HOSTS;
    }
}
