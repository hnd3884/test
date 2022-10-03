package org.apache.http.impl.client;

import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.CircularRedirectException;
import java.net.URISyntaxException;
import org.apache.http.util.Asserts;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.protocol.HttpClientContext;
import java.net.URI;
import org.apache.http.ProtocolException;
import org.apache.http.Header;
import org.apache.http.util.Args;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpResponse;
import org.apache.http.HttpRequest;
import java.util.Arrays;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.client.RedirectStrategy;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class DefaultRedirectStrategy implements RedirectStrategy
{
    private final Log log;
    public static final int SC_PERMANENT_REDIRECT = 308;
    @Deprecated
    public static final String REDIRECT_LOCATIONS = "http.protocol.redirect-locations";
    public static final DefaultRedirectStrategy INSTANCE;
    private final String[] redirectMethods;
    
    public DefaultRedirectStrategy() {
        this(new String[] { "GET", "HEAD" });
    }
    
    public DefaultRedirectStrategy(final String[] redirectMethods) {
        this.log = LogFactory.getLog((Class)this.getClass());
        final String[] tmp = redirectMethods.clone();
        Arrays.sort(tmp);
        this.redirectMethods = tmp;
    }
    
    @Override
    public boolean isRedirected(final HttpRequest request, final HttpResponse response, final HttpContext context) throws ProtocolException {
        Args.notNull((Object)request, "HTTP request");
        Args.notNull((Object)response, "HTTP response");
        final int statusCode = response.getStatusLine().getStatusCode();
        final String method = request.getRequestLine().getMethod();
        final Header locationHeader = response.getFirstHeader("location");
        switch (statusCode) {
            case 302: {
                return this.isRedirectable(method) && locationHeader != null;
            }
            case 301:
            case 307:
            case 308: {
                return this.isRedirectable(method);
            }
            case 303: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public URI getLocationURI(final HttpRequest request, final HttpResponse response, final HttpContext context) throws ProtocolException {
        Args.notNull((Object)request, "HTTP request");
        Args.notNull((Object)response, "HTTP response");
        Args.notNull((Object)context, "HTTP context");
        final HttpClientContext clientContext = HttpClientContext.adapt(context);
        final Header locationHeader = response.getFirstHeader("location");
        if (locationHeader == null) {
            throw new ProtocolException("Received redirect response " + response.getStatusLine() + " but no location header");
        }
        final String location = locationHeader.getValue();
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Redirect requested to location '" + location + "'"));
        }
        final RequestConfig config = clientContext.getRequestConfig();
        URI uri = this.createLocationURI(location);
        try {
            if (config.isNormalizeUri()) {
                uri = URIUtils.normalizeSyntax(uri);
            }
            if (!uri.isAbsolute()) {
                if (!config.isRelativeRedirectsAllowed()) {
                    throw new ProtocolException("Relative redirect location '" + uri + "' not allowed");
                }
                final HttpHost target = clientContext.getTargetHost();
                Asserts.notNull((Object)target, "Target host");
                final URI requestURI = new URI(request.getRequestLine().getUri());
                final URI absoluteRequestURI = URIUtils.rewriteURI(requestURI, target, config.isNormalizeUri() ? URIUtils.NORMALIZE : URIUtils.NO_FLAGS);
                uri = URIUtils.resolve(absoluteRequestURI, uri);
            }
        }
        catch (final URISyntaxException ex) {
            throw new ProtocolException(ex.getMessage(), (Throwable)ex);
        }
        RedirectLocations redirectLocations = (RedirectLocations)clientContext.getAttribute("http.protocol.redirect-locations");
        if (redirectLocations == null) {
            redirectLocations = new RedirectLocations();
            context.setAttribute("http.protocol.redirect-locations", (Object)redirectLocations);
        }
        if (!config.isCircularRedirectsAllowed() && redirectLocations.contains(uri)) {
            throw new CircularRedirectException("Circular redirect to '" + uri + "'");
        }
        redirectLocations.add(uri);
        return uri;
    }
    
    protected URI createLocationURI(final String location) throws ProtocolException {
        try {
            return new URI(location);
        }
        catch (final URISyntaxException ex) {
            throw new ProtocolException("Invalid redirect URI: " + location, (Throwable)ex);
        }
    }
    
    protected boolean isRedirectable(final String method) {
        return Arrays.binarySearch(this.redirectMethods, method) >= 0;
    }
    
    @Override
    public HttpUriRequest getRedirect(final HttpRequest request, final HttpResponse response, final HttpContext context) throws ProtocolException {
        final URI uri = this.getLocationURI(request, response, context);
        final String method = request.getRequestLine().getMethod();
        if (method.equalsIgnoreCase("HEAD")) {
            return new HttpHead(uri);
        }
        if (method.equalsIgnoreCase("GET")) {
            return new HttpGet(uri);
        }
        final int status = response.getStatusLine().getStatusCode();
        return (status == 307 || status == 308) ? RequestBuilder.copy(request).setUri(uri).build() : new HttpGet(uri);
    }
    
    static {
        INSTANCE = new DefaultRedirectStrategy();
    }
}
