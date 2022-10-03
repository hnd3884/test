package org.apache.commons.httpclient;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.httpclient.auth.CredentialsNotAvailableException;
import org.apache.commons.httpclient.auth.CredentialsProvider;
import org.apache.commons.httpclient.auth.MalformedChallengeException;
import java.util.Map;
import org.apache.commons.httpclient.auth.AuthChallengeException;
import org.apache.commons.httpclient.auth.AuthChallengeParser;
import java.util.HashSet;
import org.apache.commons.httpclient.auth.AuthScheme;
import org.apache.commons.httpclient.auth.AuthState;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.auth.AuthenticationException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Collection;
import org.apache.commons.httpclient.params.HttpParams;
import java.util.Set;
import org.apache.commons.httpclient.auth.AuthChallengeProcessor;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.logging.Log;

class HttpMethodDirector
{
    public static final String WWW_AUTH_CHALLENGE = "WWW-Authenticate";
    public static final String WWW_AUTH_RESP = "Authorization";
    public static final String PROXY_AUTH_CHALLENGE = "Proxy-Authenticate";
    public static final String PROXY_AUTH_RESP = "Proxy-Authorization";
    private static final Log LOG;
    private ConnectMethod connectMethod;
    private HttpState state;
    private HostConfiguration hostConfiguration;
    private HttpConnectionManager connectionManager;
    private HttpClientParams params;
    private HttpConnection conn;
    private boolean releaseConnection;
    private AuthChallengeProcessor authProcessor;
    private Set redirectLocations;
    
    public HttpMethodDirector(final HttpConnectionManager connectionManager, final HostConfiguration hostConfiguration, final HttpClientParams params, final HttpState state) {
        this.releaseConnection = false;
        this.authProcessor = null;
        this.redirectLocations = null;
        this.connectionManager = connectionManager;
        this.hostConfiguration = hostConfiguration;
        this.params = params;
        this.state = state;
        this.authProcessor = new AuthChallengeProcessor(this.params);
    }
    
    public void executeMethod(final HttpMethod method) throws IOException, HttpException {
        if (method == null) {
            throw new IllegalArgumentException("Method may not be null");
        }
        this.hostConfiguration.getParams().setDefaults(this.params);
        method.getParams().setDefaults(this.hostConfiguration.getParams());
        final Collection defaults = (Collection)this.hostConfiguration.getParams().getParameter("http.default-headers");
        if (defaults != null) {
            final Iterator i = defaults.iterator();
            while (i.hasNext()) {
                method.addRequestHeader(i.next());
            }
        }
        try {
            final int maxRedirects = this.params.getIntParameter("http.protocol.max-redirects", 100);
            int redirectCount = 0;
            while (true) {
                if (this.conn != null && !this.hostConfiguration.hostEquals(this.conn)) {
                    this.conn.setLocked(false);
                    this.conn.releaseConnection();
                    this.conn = null;
                }
                if (this.conn == null) {
                    (this.conn = this.connectionManager.getConnectionWithTimeout(this.hostConfiguration, this.params.getConnectionManagerTimeout())).setLocked(true);
                    if (this.params.isAuthenticationPreemptive() || this.state.isAuthenticationPreemptive()) {
                        HttpMethodDirector.LOG.debug((Object)"Preemptively sending default basic credentials");
                        method.getHostAuthState().setPreemptive();
                        method.getHostAuthState().setAuthAttempted(true);
                        if (this.conn.isProxied() && !this.conn.isSecure()) {
                            method.getProxyAuthState().setPreemptive();
                            method.getProxyAuthState().setAuthAttempted(true);
                        }
                    }
                }
                this.authenticate(method);
                this.executeWithRetry(method);
                if (this.connectMethod != null) {
                    this.fakeResponse(method);
                    break;
                }
                boolean retry = false;
                if (this.isRedirectNeeded(method) && this.processRedirectResponse(method)) {
                    retry = true;
                    if (++redirectCount >= maxRedirects) {
                        HttpMethodDirector.LOG.error((Object)"Narrowly avoided an infinite loop in execute");
                        throw new RedirectException("Maximum redirects (" + maxRedirects + ") exceeded");
                    }
                    if (HttpMethodDirector.LOG.isDebugEnabled()) {
                        HttpMethodDirector.LOG.debug((Object)("Execute redirect " + redirectCount + " of " + maxRedirects));
                    }
                }
                if (this.isAuthenticationNeeded(method) && this.processAuthenticationResponse(method)) {
                    HttpMethodDirector.LOG.debug((Object)"Retry authentication");
                    retry = true;
                }
                if (!retry) {
                    break;
                }
                if (method.getResponseBodyAsStream() == null) {
                    continue;
                }
                method.getResponseBodyAsStream().close();
            }
        }
        finally {
            if (this.conn != null) {
                this.conn.setLocked(false);
            }
            if ((this.releaseConnection || method.getResponseBodyAsStream() == null) && this.conn != null) {
                this.conn.releaseConnection();
            }
        }
    }
    
    private void authenticate(final HttpMethod method) {
        try {
            if (this.conn.isProxied() && !this.conn.isSecure()) {
                this.authenticateProxy(method);
            }
            this.authenticateHost(method);
        }
        catch (final AuthenticationException e) {
            HttpMethodDirector.LOG.error((Object)e.getMessage(), (Throwable)e);
        }
    }
    
    private boolean cleanAuthHeaders(final HttpMethod method, final String name) {
        final Header[] authheaders = method.getRequestHeaders(name);
        boolean clean = true;
        for (int i = 0; i < authheaders.length; ++i) {
            final Header authheader = authheaders[i];
            if (authheader.isAutogenerated()) {
                method.removeRequestHeader(authheader);
            }
            else {
                clean = false;
            }
        }
        return clean;
    }
    
    private void authenticateHost(final HttpMethod method) throws AuthenticationException {
        if (!this.cleanAuthHeaders(method, "Authorization")) {
            return;
        }
        final AuthState authstate = method.getHostAuthState();
        final AuthScheme authscheme = authstate.getAuthScheme();
        if (authscheme == null) {
            return;
        }
        if (authstate.isAuthRequested() || !authscheme.isConnectionBased()) {
            String host = method.getParams().getVirtualHost();
            if (host == null) {
                host = this.conn.getHost();
            }
            final int port = this.conn.getPort();
            final AuthScope authscope = new AuthScope(host, port, authscheme.getRealm(), authscheme.getSchemeName());
            if (HttpMethodDirector.LOG.isDebugEnabled()) {
                HttpMethodDirector.LOG.debug((Object)("Authenticating with " + authscope));
            }
            final Credentials credentials = this.state.getCredentials(authscope);
            if (credentials != null) {
                final String authstring = authscheme.authenticate(credentials, method);
                if (authstring != null) {
                    method.addRequestHeader(new Header("Authorization", authstring, true));
                }
            }
            else if (HttpMethodDirector.LOG.isWarnEnabled()) {
                HttpMethodDirector.LOG.warn((Object)("Required credentials not available for " + authscope));
                if (method.getHostAuthState().isPreemptive()) {
                    HttpMethodDirector.LOG.warn((Object)"Preemptive authentication requested but no default credentials available");
                }
            }
        }
    }
    
    private void authenticateProxy(final HttpMethod method) throws AuthenticationException {
        if (!this.cleanAuthHeaders(method, "Proxy-Authorization")) {
            return;
        }
        final AuthState authstate = method.getProxyAuthState();
        final AuthScheme authscheme = authstate.getAuthScheme();
        if (authscheme == null) {
            return;
        }
        if (authstate.isAuthRequested() || !authscheme.isConnectionBased()) {
            final AuthScope authscope = new AuthScope(this.conn.getProxyHost(), this.conn.getProxyPort(), authscheme.getRealm(), authscheme.getSchemeName());
            if (HttpMethodDirector.LOG.isDebugEnabled()) {
                HttpMethodDirector.LOG.debug((Object)("Authenticating with " + authscope));
            }
            final Credentials credentials = this.state.getProxyCredentials(authscope);
            if (credentials != null) {
                final String authstring = authscheme.authenticate(credentials, method);
                if (authstring != null) {
                    method.addRequestHeader(new Header("Proxy-Authorization", authstring, true));
                }
            }
            else if (HttpMethodDirector.LOG.isWarnEnabled()) {
                HttpMethodDirector.LOG.warn((Object)("Required proxy credentials not available for " + authscope));
                if (method.getProxyAuthState().isPreemptive()) {
                    HttpMethodDirector.LOG.warn((Object)"Preemptive authentication requested but no default proxy credentials available");
                }
            }
        }
    }
    
    private void applyConnectionParams(final HttpMethod method) throws IOException {
        int timeout = 0;
        Object param = method.getParams().getParameter("http.socket.timeout");
        if (param == null) {
            param = this.conn.getParams().getParameter("http.socket.timeout");
        }
        if (param != null) {
            timeout = (int)param;
        }
        this.conn.setSocketTimeout(timeout);
    }
    
    private void executeWithRetry(final HttpMethod method) throws IOException, HttpException {
        int execCount = 0;
        try {
            while (true) {
                ++execCount;
                try {
                    if (HttpMethodDirector.LOG.isTraceEnabled()) {
                        HttpMethodDirector.LOG.trace((Object)("Attempt number " + execCount + " to process request"));
                    }
                    if (this.conn.getParams().isStaleCheckingEnabled()) {
                        this.conn.closeIfStale();
                    }
                    if (!this.conn.isOpen()) {
                        this.conn.open();
                        if (this.conn.isProxied() && this.conn.isSecure() && !(method instanceof ConnectMethod) && !this.executeConnect()) {
                            return;
                        }
                    }
                    this.applyConnectionParams(method);
                    method.execute(this.state, this.conn);
                }
                catch (final HttpException e) {
                    throw e;
                }
                catch (final IOException e2) {
                    HttpMethodDirector.LOG.debug((Object)"Closing the connection.");
                    this.conn.close();
                    if (method instanceof HttpMethodBase) {
                        final MethodRetryHandler handler = ((HttpMethodBase)method).getMethodRetryHandler();
                        if (handler != null && !handler.retryMethod(method, this.conn, new HttpRecoverableException(e2.getMessage()), execCount, method.isRequestSent())) {
                            HttpMethodDirector.LOG.debug((Object)"Method retry handler returned false. Automatic recovery will not be attempted");
                            throw e2;
                        }
                    }
                    HttpMethodRetryHandler handler2 = (HttpMethodRetryHandler)method.getParams().getParameter("http.method.retry-handler");
                    if (handler2 == null) {
                        handler2 = new DefaultHttpMethodRetryHandler();
                    }
                    if (!handler2.retryMethod(method, e2, execCount)) {
                        HttpMethodDirector.LOG.debug((Object)"Method retry handler returned false. Automatic recovery will not be attempted");
                        throw e2;
                    }
                    if (HttpMethodDirector.LOG.isInfoEnabled()) {
                        HttpMethodDirector.LOG.info((Object)("I/O exception (" + e2.getClass().getName() + ") caught when processing request: " + e2.getMessage()));
                    }
                    if (HttpMethodDirector.LOG.isDebugEnabled()) {
                        HttpMethodDirector.LOG.debug((Object)e2.getMessage(), (Throwable)e2);
                    }
                    HttpMethodDirector.LOG.info((Object)"Retrying request");
                    continue;
                }
                break;
            }
        }
        catch (final IOException e2) {
            if (this.conn.isOpen()) {
                HttpMethodDirector.LOG.debug((Object)"Closing the connection.");
                this.conn.close();
            }
            this.releaseConnection = true;
            throw e2;
        }
        catch (final RuntimeException e3) {
            if (this.conn.isOpen()) {
                HttpMethodDirector.LOG.debug((Object)"Closing the connection.");
                this.conn.close();
            }
            this.releaseConnection = true;
            throw e3;
        }
    }
    
    private boolean executeConnect() throws IOException, HttpException {
        this.connectMethod = new ConnectMethod(this.hostConfiguration);
        this.connectMethod.getParams().setDefaults(this.hostConfiguration.getParams());
        int code;
        while (true) {
            if (!this.conn.isOpen()) {
                this.conn.open();
            }
            if (this.params.isAuthenticationPreemptive() || this.state.isAuthenticationPreemptive()) {
                HttpMethodDirector.LOG.debug((Object)"Preemptively sending default basic credentials");
                this.connectMethod.getProxyAuthState().setPreemptive();
                this.connectMethod.getProxyAuthState().setAuthAttempted(true);
            }
            try {
                this.authenticateProxy(this.connectMethod);
            }
            catch (final AuthenticationException e) {
                HttpMethodDirector.LOG.error((Object)e.getMessage(), (Throwable)e);
            }
            this.applyConnectionParams(this.connectMethod);
            this.connectMethod.execute(this.state, this.conn);
            code = this.connectMethod.getStatusCode();
            boolean retry = false;
            final AuthState authstate = this.connectMethod.getProxyAuthState();
            authstate.setAuthRequested(code == 407);
            if (authstate.isAuthRequested() && this.processAuthenticationResponse(this.connectMethod)) {
                retry = true;
            }
            if (!retry) {
                break;
            }
            if (this.connectMethod.getResponseBodyAsStream() == null) {
                continue;
            }
            this.connectMethod.getResponseBodyAsStream().close();
        }
        if (code >= 200 && code < 300) {
            this.conn.tunnelCreated();
            this.connectMethod = null;
            return true;
        }
        this.conn.close();
        return false;
    }
    
    private void fakeResponse(final HttpMethod method) throws IOException, HttpException {
        HttpMethodDirector.LOG.debug((Object)"CONNECT failed, fake the response for the original method");
        if (method instanceof HttpMethodBase) {
            ((HttpMethodBase)method).fakeResponse(this.connectMethod.getStatusLine(), this.connectMethod.getResponseHeaderGroup(), this.connectMethod.getResponseBodyAsStream());
            method.getProxyAuthState().setAuthScheme(this.connectMethod.getProxyAuthState().getAuthScheme());
            this.connectMethod = null;
        }
        else {
            this.releaseConnection = true;
            HttpMethodDirector.LOG.warn((Object)"Unable to fake response on method as it is not derived from HttpMethodBase.");
        }
    }
    
    private boolean processRedirectResponse(final HttpMethod method) throws RedirectException {
        final Header locationHeader = method.getResponseHeader("location");
        if (locationHeader == null) {
            HttpMethodDirector.LOG.error((Object)("Received redirect response " + method.getStatusCode() + " but no location header"));
            return false;
        }
        final String location = locationHeader.getValue();
        if (HttpMethodDirector.LOG.isDebugEnabled()) {
            HttpMethodDirector.LOG.debug((Object)("Redirect requested to location '" + location + "'"));
        }
        URI redirectUri = null;
        URI currentUri = null;
        try {
            currentUri = new URI(this.conn.getProtocol().getScheme(), null, this.conn.getHost(), this.conn.getPort(), method.getPath());
            final String charset = method.getParams().getUriCharset();
            redirectUri = new URI(location, true, charset);
            if (redirectUri.isRelativeURI()) {
                if (this.params.isParameterTrue("http.protocol.reject-relative-redirect")) {
                    HttpMethodDirector.LOG.warn((Object)("Relative redirect location '" + location + "' not allowed"));
                    return false;
                }
                HttpMethodDirector.LOG.debug((Object)"Redirect URI is not absolute - parsing as relative");
                redirectUri = new URI(currentUri, redirectUri);
            }
            else {
                method.getParams().setDefaults(this.params);
            }
            method.setURI(redirectUri);
            this.hostConfiguration.setHost(redirectUri);
        }
        catch (final URIException ex) {
            throw new InvalidRedirectLocationException("Invalid redirect location: " + location, location, ex);
        }
        if (this.params.isParameterFalse("http.protocol.allow-circular-redirects")) {
            if (this.redirectLocations == null) {
                this.redirectLocations = new HashSet();
            }
            this.redirectLocations.add(currentUri);
            try {
                if (redirectUri.hasQuery()) {
                    redirectUri.setQuery(null);
                }
            }
            catch (final URIException e) {
                return false;
            }
            if (this.redirectLocations.contains(redirectUri)) {
                throw new CircularRedirectException("Circular redirect to '" + redirectUri + "'");
            }
        }
        if (HttpMethodDirector.LOG.isDebugEnabled()) {
            HttpMethodDirector.LOG.debug((Object)("Redirecting from '" + currentUri.getEscapedURI() + "' to '" + redirectUri.getEscapedURI()));
        }
        method.getHostAuthState().invalidate();
        return true;
    }
    
    private boolean processAuthenticationResponse(final HttpMethod method) {
        HttpMethodDirector.LOG.trace((Object)"enter HttpMethodBase.processAuthenticationResponse(HttpState, HttpConnection)");
        try {
            switch (method.getStatusCode()) {
                case 401: {
                    return this.processWWWAuthChallenge(method);
                }
                case 407: {
                    return this.processProxyAuthChallenge(method);
                }
                default: {
                    return false;
                }
            }
        }
        catch (final Exception e) {
            if (HttpMethodDirector.LOG.isErrorEnabled()) {
                HttpMethodDirector.LOG.error((Object)e.getMessage(), (Throwable)e);
            }
            return false;
        }
    }
    
    private boolean processWWWAuthChallenge(final HttpMethod method) throws MalformedChallengeException, AuthenticationException {
        final AuthState authstate = method.getHostAuthState();
        final Map challenges = AuthChallengeParser.parseChallenges(method.getResponseHeaders("WWW-Authenticate"));
        if (challenges.isEmpty()) {
            HttpMethodDirector.LOG.debug((Object)"Authentication challenge(s) not found");
            return false;
        }
        AuthScheme authscheme = null;
        try {
            authscheme = this.authProcessor.processChallenge(authstate, challenges);
        }
        catch (final AuthChallengeException e) {
            if (HttpMethodDirector.LOG.isWarnEnabled()) {
                HttpMethodDirector.LOG.warn((Object)e.getMessage());
            }
        }
        if (authscheme == null) {
            return false;
        }
        String host = method.getParams().getVirtualHost();
        if (host == null) {
            host = this.conn.getHost();
        }
        final int port = this.conn.getPort();
        final AuthScope authscope = new AuthScope(host, port, authscheme.getRealm(), authscheme.getSchemeName());
        if (HttpMethodDirector.LOG.isDebugEnabled()) {
            HttpMethodDirector.LOG.debug((Object)("Authentication scope: " + authscope));
        }
        if (authstate.isAuthAttempted() && authscheme.isComplete()) {
            final Credentials credentials = this.promptForCredentials(authscheme, method.getParams(), authscope);
            if (credentials == null) {
                if (HttpMethodDirector.LOG.isInfoEnabled()) {
                    HttpMethodDirector.LOG.info((Object)("Failure authenticating with " + authscope));
                }
                return false;
            }
            return true;
        }
        else {
            authstate.setAuthAttempted(true);
            Credentials credentials = this.state.getCredentials(authscope);
            if (credentials == null) {
                credentials = this.promptForCredentials(authscheme, method.getParams(), authscope);
            }
            if (credentials == null) {
                if (HttpMethodDirector.LOG.isInfoEnabled()) {
                    HttpMethodDirector.LOG.info((Object)("No credentials available for " + authscope));
                }
                return false;
            }
            return true;
        }
    }
    
    private boolean processProxyAuthChallenge(final HttpMethod method) throws MalformedChallengeException, AuthenticationException {
        final AuthState authstate = method.getProxyAuthState();
        final Map proxyChallenges = AuthChallengeParser.parseChallenges(method.getResponseHeaders("Proxy-Authenticate"));
        if (proxyChallenges.isEmpty()) {
            HttpMethodDirector.LOG.debug((Object)"Proxy authentication challenge(s) not found");
            return false;
        }
        AuthScheme authscheme = null;
        try {
            authscheme = this.authProcessor.processChallenge(authstate, proxyChallenges);
        }
        catch (final AuthChallengeException e) {
            if (HttpMethodDirector.LOG.isWarnEnabled()) {
                HttpMethodDirector.LOG.warn((Object)e.getMessage());
            }
        }
        if (authscheme == null) {
            return false;
        }
        final AuthScope authscope = new AuthScope(this.conn.getProxyHost(), this.conn.getProxyPort(), authscheme.getRealm(), authscheme.getSchemeName());
        if (HttpMethodDirector.LOG.isDebugEnabled()) {
            HttpMethodDirector.LOG.debug((Object)("Proxy authentication scope: " + authscope));
        }
        if (authstate.isAuthAttempted() && authscheme.isComplete()) {
            final Credentials credentials = this.promptForProxyCredentials(authscheme, method.getParams(), authscope);
            if (credentials == null) {
                if (HttpMethodDirector.LOG.isInfoEnabled()) {
                    HttpMethodDirector.LOG.info((Object)("Failure authenticating with " + authscope));
                }
                return false;
            }
            return true;
        }
        else {
            authstate.setAuthAttempted(true);
            Credentials credentials = this.state.getProxyCredentials(authscope);
            if (credentials == null) {
                credentials = this.promptForProxyCredentials(authscheme, method.getParams(), authscope);
            }
            if (credentials == null) {
                if (HttpMethodDirector.LOG.isInfoEnabled()) {
                    HttpMethodDirector.LOG.info((Object)("No credentials available for " + authscope));
                }
                return false;
            }
            return true;
        }
    }
    
    private boolean isRedirectNeeded(final HttpMethod method) {
        switch (method.getStatusCode()) {
            case 301:
            case 302:
            case 303:
            case 307: {
                HttpMethodDirector.LOG.debug((Object)"Redirect required");
                return method.getFollowRedirects();
            }
            default: {
                return false;
            }
        }
    }
    
    private boolean isAuthenticationNeeded(final HttpMethod method) {
        method.getHostAuthState().setAuthRequested(method.getStatusCode() == 401);
        method.getProxyAuthState().setAuthRequested(method.getStatusCode() == 407);
        if (!method.getHostAuthState().isAuthRequested() && !method.getProxyAuthState().isAuthRequested()) {
            return false;
        }
        HttpMethodDirector.LOG.debug((Object)"Authorization required");
        if (method.getDoAuthentication()) {
            return true;
        }
        HttpMethodDirector.LOG.info((Object)"Authentication requested but doAuthentication is disabled");
        return false;
    }
    
    private Credentials promptForCredentials(final AuthScheme authScheme, final HttpParams params, final AuthScope authscope) {
        HttpMethodDirector.LOG.debug((Object)"Credentials required");
        Credentials creds = null;
        final CredentialsProvider credProvider = (CredentialsProvider)params.getParameter("http.authentication.credential-provider");
        if (credProvider != null) {
            try {
                creds = credProvider.getCredentials(authScheme, authscope.getHost(), authscope.getPort(), false);
            }
            catch (final CredentialsNotAvailableException e) {
                HttpMethodDirector.LOG.warn((Object)e.getMessage());
            }
            if (creds != null) {
                this.state.setCredentials(authscope, creds);
                if (HttpMethodDirector.LOG.isDebugEnabled()) {
                    HttpMethodDirector.LOG.debug((Object)(authscope + " new credentials given"));
                }
            }
        }
        else {
            HttpMethodDirector.LOG.debug((Object)"Credentials provider not available");
        }
        return creds;
    }
    
    private Credentials promptForProxyCredentials(final AuthScheme authScheme, final HttpParams params, final AuthScope authscope) {
        HttpMethodDirector.LOG.debug((Object)"Proxy credentials required");
        Credentials creds = null;
        final CredentialsProvider credProvider = (CredentialsProvider)params.getParameter("http.authentication.credential-provider");
        if (credProvider != null) {
            try {
                creds = credProvider.getCredentials(authScheme, authscope.getHost(), authscope.getPort(), true);
            }
            catch (final CredentialsNotAvailableException e) {
                HttpMethodDirector.LOG.warn((Object)e.getMessage());
            }
            if (creds != null) {
                this.state.setProxyCredentials(authscope, creds);
                if (HttpMethodDirector.LOG.isDebugEnabled()) {
                    HttpMethodDirector.LOG.debug((Object)(authscope + " new credentials given"));
                }
            }
        }
        else {
            HttpMethodDirector.LOG.debug((Object)"Proxy credentials provider not available");
        }
        return creds;
    }
    
    public HostConfiguration getHostConfiguration() {
        return this.hostConfiguration;
    }
    
    public HttpState getState() {
        return this.state;
    }
    
    public HttpConnectionManager getConnectionManager() {
        return this.connectionManager;
    }
    
    public HttpParams getParams() {
        return this.params;
    }
    
    static {
        LOG = LogFactory.getLog(HttpMethodDirector.class);
    }
}
