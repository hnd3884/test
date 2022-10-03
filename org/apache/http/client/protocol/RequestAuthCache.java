package org.apache.http.client.protocol;

import org.apache.http.auth.Credentials;
import org.apache.http.auth.AuthScope;
import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthState;
import org.apache.http.conn.routing.RouteInfo;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.AuthCache;
import org.apache.http.auth.AuthProtocolState;
import org.apache.http.HttpHost;
import org.apache.http.util.Args;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpRequest;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.HttpRequestInterceptor;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class RequestAuthCache implements HttpRequestInterceptor
{
    private final Log log;
    
    public RequestAuthCache() {
        this.log = LogFactory.getLog((Class)this.getClass());
    }
    
    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        Args.notNull((Object)request, "HTTP request");
        Args.notNull((Object)context, "HTTP context");
        final HttpClientContext clientContext = HttpClientContext.adapt(context);
        final AuthCache authCache = clientContext.getAuthCache();
        if (authCache == null) {
            this.log.debug((Object)"Auth cache not set in the context");
            return;
        }
        final CredentialsProvider credsProvider = clientContext.getCredentialsProvider();
        if (credsProvider == null) {
            this.log.debug((Object)"Credentials provider not set in the context");
            return;
        }
        final RouteInfo route = clientContext.getHttpRoute();
        if (route == null) {
            this.log.debug((Object)"Route info not set in the context");
            return;
        }
        HttpHost target = clientContext.getTargetHost();
        if (target == null) {
            this.log.debug((Object)"Target host not set in the context");
            return;
        }
        if (target.getPort() < 0) {
            target = new HttpHost(target.getHostName(), route.getTargetHost().getPort(), target.getSchemeName());
        }
        final AuthState targetState = clientContext.getTargetAuthState();
        if (targetState != null && targetState.getState() == AuthProtocolState.UNCHALLENGED) {
            final AuthScheme authScheme = authCache.get(target);
            if (authScheme != null) {
                this.doPreemptiveAuth(target, authScheme, targetState, credsProvider);
            }
        }
        final HttpHost proxy = route.getProxyHost();
        final AuthState proxyState = clientContext.getProxyAuthState();
        if (proxy != null && proxyState != null && proxyState.getState() == AuthProtocolState.UNCHALLENGED) {
            final AuthScheme authScheme2 = authCache.get(proxy);
            if (authScheme2 != null) {
                this.doPreemptiveAuth(proxy, authScheme2, proxyState, credsProvider);
            }
        }
    }
    
    private void doPreemptiveAuth(final HttpHost host, final AuthScheme authScheme, final AuthState authState, final CredentialsProvider credsProvider) {
        final String schemeName = authScheme.getSchemeName();
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Re-using cached '" + schemeName + "' auth scheme for " + host));
        }
        final AuthScope authScope = new AuthScope(host, AuthScope.ANY_REALM, schemeName);
        final Credentials creds = credsProvider.getCredentials(authScope);
        if (creds != null) {
            authState.update(authScheme, creds);
        }
        else {
            this.log.debug((Object)"No credentials for preemptive authentication");
        }
    }
}
