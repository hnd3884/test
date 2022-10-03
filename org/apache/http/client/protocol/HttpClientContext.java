package org.apache.http.client.protocol;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.auth.AuthState;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.config.Lookup;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.client.CookieStore;
import java.net.URI;
import java.util.List;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.RouteInfo;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;

public class HttpClientContext extends HttpCoreContext
{
    public static final String HTTP_ROUTE = "http.route";
    public static final String REDIRECT_LOCATIONS = "http.protocol.redirect-locations";
    public static final String COOKIESPEC_REGISTRY = "http.cookiespec-registry";
    public static final String COOKIE_SPEC = "http.cookie-spec";
    public static final String COOKIE_ORIGIN = "http.cookie-origin";
    public static final String COOKIE_STORE = "http.cookie-store";
    public static final String CREDS_PROVIDER = "http.auth.credentials-provider";
    public static final String AUTH_CACHE = "http.auth.auth-cache";
    public static final String TARGET_AUTH_STATE = "http.auth.target-scope";
    public static final String PROXY_AUTH_STATE = "http.auth.proxy-scope";
    public static final String USER_TOKEN = "http.user-token";
    public static final String AUTHSCHEME_REGISTRY = "http.authscheme-registry";
    public static final String REQUEST_CONFIG = "http.request-config";
    
    public static HttpClientContext adapt(final HttpContext context) {
        return (context instanceof HttpClientContext) ? context : new HttpClientContext(context);
    }
    
    public static HttpClientContext create() {
        return new HttpClientContext((HttpContext)new BasicHttpContext());
    }
    
    public HttpClientContext(final HttpContext context) {
        super(context);
    }
    
    public HttpClientContext() {
    }
    
    public RouteInfo getHttpRoute() {
        return (RouteInfo)this.getAttribute("http.route", (Class)HttpRoute.class);
    }
    
    public List<URI> getRedirectLocations() {
        return (List)this.getAttribute("http.protocol.redirect-locations", (Class)List.class);
    }
    
    public CookieStore getCookieStore() {
        return (CookieStore)this.getAttribute("http.cookie-store", (Class)CookieStore.class);
    }
    
    public void setCookieStore(final CookieStore cookieStore) {
        this.setAttribute("http.cookie-store", (Object)cookieStore);
    }
    
    public CookieSpec getCookieSpec() {
        return (CookieSpec)this.getAttribute("http.cookie-spec", (Class)CookieSpec.class);
    }
    
    public CookieOrigin getCookieOrigin() {
        return (CookieOrigin)this.getAttribute("http.cookie-origin", (Class)CookieOrigin.class);
    }
    
    private <T> Lookup<T> getLookup(final String name, final Class<T> clazz) {
        return (Lookup<T>)this.getAttribute(name, (Class)Lookup.class);
    }
    
    public Lookup<CookieSpecProvider> getCookieSpecRegistry() {
        return this.getLookup("http.cookiespec-registry", CookieSpecProvider.class);
    }
    
    public void setCookieSpecRegistry(final Lookup<CookieSpecProvider> lookup) {
        this.setAttribute("http.cookiespec-registry", (Object)lookup);
    }
    
    public Lookup<AuthSchemeProvider> getAuthSchemeRegistry() {
        return this.getLookup("http.authscheme-registry", AuthSchemeProvider.class);
    }
    
    public void setAuthSchemeRegistry(final Lookup<AuthSchemeProvider> lookup) {
        this.setAttribute("http.authscheme-registry", (Object)lookup);
    }
    
    public CredentialsProvider getCredentialsProvider() {
        return (CredentialsProvider)this.getAttribute("http.auth.credentials-provider", (Class)CredentialsProvider.class);
    }
    
    public void setCredentialsProvider(final CredentialsProvider credentialsProvider) {
        this.setAttribute("http.auth.credentials-provider", (Object)credentialsProvider);
    }
    
    public AuthCache getAuthCache() {
        return (AuthCache)this.getAttribute("http.auth.auth-cache", (Class)AuthCache.class);
    }
    
    public void setAuthCache(final AuthCache authCache) {
        this.setAttribute("http.auth.auth-cache", (Object)authCache);
    }
    
    public AuthState getTargetAuthState() {
        return (AuthState)this.getAttribute("http.auth.target-scope", (Class)AuthState.class);
    }
    
    public AuthState getProxyAuthState() {
        return (AuthState)this.getAttribute("http.auth.proxy-scope", (Class)AuthState.class);
    }
    
    public <T> T getUserToken(final Class<T> clazz) {
        return (T)this.getAttribute("http.user-token", (Class)clazz);
    }
    
    public Object getUserToken() {
        return this.getAttribute("http.user-token");
    }
    
    public void setUserToken(final Object obj) {
        this.setAttribute("http.user-token", obj);
    }
    
    public RequestConfig getRequestConfig() {
        final RequestConfig config = (RequestConfig)this.getAttribute("http.request-config", (Class)RequestConfig.class);
        return (config != null) ? config : RequestConfig.DEFAULT;
    }
    
    public void setRequestConfig(final RequestConfig config) {
        this.setAttribute("http.request-config", (Object)config);
    }
}
