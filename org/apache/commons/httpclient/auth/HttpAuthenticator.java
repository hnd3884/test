package org.apache.commons.httpclient.auth;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpMethod;
import java.util.Map;
import java.util.HashMap;
import org.apache.commons.httpclient.Header;
import org.apache.commons.logging.Log;

public final class HttpAuthenticator
{
    private static final Log LOG;
    public static final String WWW_AUTH = "WWW-Authenticate";
    public static final String WWW_AUTH_RESP = "Authorization";
    public static final String PROXY_AUTH = "Proxy-Authenticate";
    public static final String PROXY_AUTH_RESP = "Proxy-Authorization";
    
    public static AuthScheme selectAuthScheme(final Header[] challenges) throws MalformedChallengeException {
        HttpAuthenticator.LOG.trace((Object)"enter HttpAuthenticator.selectAuthScheme(Header[])");
        if (challenges == null) {
            throw new IllegalArgumentException("Array of challenges may not be null");
        }
        if (challenges.length == 0) {
            throw new IllegalArgumentException("Array of challenges may not be empty");
        }
        String challenge = null;
        final Map challengemap = new HashMap(challenges.length);
        for (int i = 0; i < challenges.length; ++i) {
            challenge = challenges[i].getValue();
            final String s = AuthChallengeParser.extractScheme(challenge);
            challengemap.put(s, challenge);
        }
        challenge = challengemap.get("ntlm");
        if (challenge != null) {
            return new NTLMScheme(challenge);
        }
        challenge = challengemap.get("digest");
        if (challenge != null) {
            return new DigestScheme(challenge);
        }
        challenge = challengemap.get("basic");
        if (challenge != null) {
            return new BasicScheme(challenge);
        }
        throw new UnsupportedOperationException("Authentication scheme(s) not supported: " + challengemap.toString());
    }
    
    private static boolean doAuthenticateDefault(final HttpMethod method, final HttpConnection conn, final HttpState state, final boolean proxy) throws AuthenticationException {
        if (method == null) {
            throw new IllegalArgumentException("HTTP method may not be null");
        }
        if (state == null) {
            throw new IllegalArgumentException("HTTP state may not be null");
        }
        String host = null;
        if (conn != null) {
            host = (proxy ? conn.getProxyHost() : conn.getHost());
        }
        final Credentials credentials = proxy ? state.getProxyCredentials(null, host) : state.getCredentials(null, host);
        if (credentials == null) {
            return false;
        }
        if (!(credentials instanceof UsernamePasswordCredentials)) {
            throw new InvalidCredentialsException("Credentials cannot be used for basic authentication: " + credentials.toString());
        }
        final String auth = BasicScheme.authenticate((UsernamePasswordCredentials)credentials, method.getParams().getCredentialCharset());
        if (auth != null) {
            final String s = proxy ? "Proxy-Authorization" : "Authorization";
            final Header header = new Header(s, auth, true);
            method.addRequestHeader(header);
            return true;
        }
        return false;
    }
    
    public static boolean authenticateDefault(final HttpMethod method, final HttpConnection conn, final HttpState state) throws AuthenticationException {
        HttpAuthenticator.LOG.trace((Object)"enter HttpAuthenticator.authenticateDefault(HttpMethod, HttpConnection, HttpState)");
        return doAuthenticateDefault(method, conn, state, false);
    }
    
    public static boolean authenticateProxyDefault(final HttpMethod method, final HttpConnection conn, final HttpState state) throws AuthenticationException {
        HttpAuthenticator.LOG.trace((Object)"enter HttpAuthenticator.authenticateProxyDefault(HttpMethod, HttpState)");
        return doAuthenticateDefault(method, conn, state, true);
    }
    
    private static boolean doAuthenticate(final AuthScheme authscheme, final HttpMethod method, final HttpConnection conn, final HttpState state, final boolean proxy) throws AuthenticationException {
        if (authscheme == null) {
            throw new IllegalArgumentException("Authentication scheme may not be null");
        }
        if (method == null) {
            throw new IllegalArgumentException("HTTP method may not be null");
        }
        if (state == null) {
            throw new IllegalArgumentException("HTTP state may not be null");
        }
        String host = null;
        if (conn != null) {
            if (proxy) {
                host = conn.getProxyHost();
            }
            else {
                host = method.getParams().getVirtualHost();
                if (host == null) {
                    host = conn.getHost();
                }
            }
        }
        final String realm = authscheme.getRealm();
        if (HttpAuthenticator.LOG.isDebugEnabled()) {
            final StringBuffer buffer = new StringBuffer();
            buffer.append("Using credentials for ");
            if (realm == null) {
                buffer.append("default");
            }
            else {
                buffer.append('\'');
                buffer.append(realm);
                buffer.append('\'');
            }
            buffer.append(" authentication realm at ");
            buffer.append(host);
            HttpAuthenticator.LOG.debug((Object)buffer.toString());
        }
        final Credentials credentials = proxy ? state.getProxyCredentials(realm, host) : state.getCredentials(realm, host);
        if (credentials == null) {
            final StringBuffer buffer2 = new StringBuffer();
            buffer2.append("No credentials available for the ");
            if (realm == null) {
                buffer2.append("default");
            }
            else {
                buffer2.append('\'');
                buffer2.append(realm);
                buffer2.append('\'');
            }
            buffer2.append(" authentication realm at ");
            buffer2.append(host);
            throw new CredentialsNotAvailableException(buffer2.toString());
        }
        final String auth = authscheme.authenticate(credentials, method);
        if (auth != null) {
            final String s = proxy ? "Proxy-Authorization" : "Authorization";
            final Header header = new Header(s, auth, true);
            method.addRequestHeader(header);
            return true;
        }
        return false;
    }
    
    public static boolean authenticate(final AuthScheme authscheme, final HttpMethod method, final HttpConnection conn, final HttpState state) throws AuthenticationException {
        HttpAuthenticator.LOG.trace((Object)"enter HttpAuthenticator.authenticate(AuthScheme, HttpMethod, HttpConnection, HttpState)");
        return doAuthenticate(authscheme, method, conn, state, false);
    }
    
    public static boolean authenticateProxy(final AuthScheme authscheme, final HttpMethod method, final HttpConnection conn, final HttpState state) throws AuthenticationException {
        HttpAuthenticator.LOG.trace((Object)"enter HttpAuthenticator.authenticateProxy(AuthScheme, HttpMethod, HttpState)");
        return doAuthenticate(authscheme, method, conn, state, true);
    }
    
    static {
        LOG = LogFactory.getLog(HttpAuthenticator.class);
    }
}
