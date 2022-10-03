package org.apache.http.impl.client;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.HttpHost;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.util.Args;
import java.net.URL;
import java.net.InetAddress;
import java.net.PasswordAuthentication;
import java.net.Authenticator;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.AuthScope;
import java.util.Map;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.client.CredentialsProvider;

@Contract(threading = ThreadingBehavior.SAFE)
public class SystemDefaultCredentialsProvider implements CredentialsProvider
{
    private static final Map<String, String> SCHEME_MAP;
    private final BasicCredentialsProvider internal;
    
    private static String translateScheme(final String key) {
        if (key == null) {
            return null;
        }
        final String s = SystemDefaultCredentialsProvider.SCHEME_MAP.get(key);
        return (s != null) ? s : key;
    }
    
    public SystemDefaultCredentialsProvider() {
        this.internal = new BasicCredentialsProvider();
    }
    
    @Override
    public void setCredentials(final AuthScope authscope, final Credentials credentials) {
        this.internal.setCredentials(authscope, credentials);
    }
    
    private static PasswordAuthentication getSystemCreds(final String protocol, final AuthScope authscope, final Authenticator.RequestorType requestorType) {
        return Authenticator.requestPasswordAuthentication(authscope.getHost(), null, authscope.getPort(), protocol, null, translateScheme(authscope.getScheme()), null, requestorType);
    }
    
    @Override
    public Credentials getCredentials(final AuthScope authscope) {
        Args.notNull((Object)authscope, "Auth scope");
        final Credentials localcreds = this.internal.getCredentials(authscope);
        if (localcreds != null) {
            return localcreds;
        }
        final String host = authscope.getHost();
        if (host != null) {
            final HttpHost origin = authscope.getOrigin();
            final String protocol = (origin != null) ? origin.getSchemeName() : ((authscope.getPort() == 443) ? "https" : "http");
            PasswordAuthentication systemcreds = getSystemCreds(protocol, authscope, Authenticator.RequestorType.SERVER);
            if (systemcreds == null) {
                systemcreds = getSystemCreds(protocol, authscope, Authenticator.RequestorType.PROXY);
            }
            if (systemcreds == null) {
                systemcreds = getProxyCredentials("http", authscope);
                if (systemcreds == null) {
                    systemcreds = getProxyCredentials("https", authscope);
                }
            }
            if (systemcreds != null) {
                final String domain = System.getProperty("http.auth.ntlm.domain");
                if (domain != null) {
                    return new NTCredentials(systemcreds.getUserName(), new String(systemcreds.getPassword()), null, domain);
                }
                return "NTLM".equalsIgnoreCase(authscope.getScheme()) ? new NTCredentials(systemcreds.getUserName(), new String(systemcreds.getPassword()), null, null) : new UsernamePasswordCredentials(systemcreds.getUserName(), new String(systemcreds.getPassword()));
            }
        }
        return null;
    }
    
    private static PasswordAuthentication getProxyCredentials(final String protocol, final AuthScope authscope) {
        final String proxyHost = System.getProperty(protocol + ".proxyHost");
        if (proxyHost == null) {
            return null;
        }
        final String proxyPort = System.getProperty(protocol + ".proxyPort");
        if (proxyPort == null) {
            return null;
        }
        try {
            final AuthScope systemScope = new AuthScope(proxyHost, Integer.parseInt(proxyPort));
            if (authscope.match(systemScope) >= 0) {
                final String proxyUser = System.getProperty(protocol + ".proxyUser");
                if (proxyUser == null) {
                    return null;
                }
                final String proxyPassword = System.getProperty(protocol + ".proxyPassword");
                return new PasswordAuthentication(proxyUser, (proxyPassword != null) ? proxyPassword.toCharArray() : new char[0]);
            }
        }
        catch (final NumberFormatException ex) {}
        return null;
    }
    
    @Override
    public void clear() {
        this.internal.clear();
    }
    
    static {
        (SCHEME_MAP = new ConcurrentHashMap<String, String>()).put("Basic".toUpperCase(Locale.ROOT), "Basic");
        SystemDefaultCredentialsProvider.SCHEME_MAP.put("Digest".toUpperCase(Locale.ROOT), "Digest");
        SystemDefaultCredentialsProvider.SCHEME_MAP.put("NTLM".toUpperCase(Locale.ROOT), "NTLM");
        SystemDefaultCredentialsProvider.SCHEME_MAP.put("Negotiate".toUpperCase(Locale.ROOT), "SPNEGO");
        SystemDefaultCredentialsProvider.SCHEME_MAP.put("Kerberos".toUpperCase(Locale.ROOT), "Kerberos");
    }
}
