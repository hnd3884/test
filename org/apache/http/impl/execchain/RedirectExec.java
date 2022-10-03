package org.apache.http.impl.execchain;

import org.apache.http.auth.AuthState;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import java.net.URI;
import java.util.List;
import org.apache.http.HttpException;
import java.io.IOException;
import org.apache.http.util.EntityUtils;
import org.apache.http.ProtocolException;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.RedirectException;
import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpExecutionAware;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.util.Args;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.client.RedirectStrategy;
import org.apache.commons.logging.Log;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class RedirectExec implements ClientExecChain
{
    private final Log log;
    private final ClientExecChain requestExecutor;
    private final RedirectStrategy redirectStrategy;
    private final HttpRoutePlanner routePlanner;
    
    public RedirectExec(final ClientExecChain requestExecutor, final HttpRoutePlanner routePlanner, final RedirectStrategy redirectStrategy) {
        this.log = LogFactory.getLog((Class)this.getClass());
        Args.notNull((Object)requestExecutor, "HTTP client request executor");
        Args.notNull((Object)routePlanner, "HTTP route planner");
        Args.notNull((Object)redirectStrategy, "HTTP redirect strategy");
        this.requestExecutor = requestExecutor;
        this.routePlanner = routePlanner;
        this.redirectStrategy = redirectStrategy;
    }
    
    @Override
    public CloseableHttpResponse execute(final HttpRoute route, final HttpRequestWrapper request, final HttpClientContext context, final HttpExecutionAware execAware) throws IOException, HttpException {
        Args.notNull((Object)route, "HTTP route");
        Args.notNull((Object)request, "HTTP request");
        Args.notNull((Object)context, "HTTP context");
        final List<URI> redirectLocations = context.getRedirectLocations();
        if (redirectLocations != null) {
            redirectLocations.clear();
        }
        final RequestConfig config = context.getRequestConfig();
        final int maxRedirects = (config.getMaxRedirects() > 0) ? config.getMaxRedirects() : 50;
        HttpRoute currentRoute = route;
        HttpRequestWrapper currentRequest = request;
        int redirectCount = 0;
        while (true) {
            final CloseableHttpResponse response = this.requestExecutor.execute(currentRoute, currentRequest, context, execAware);
            try {
                if (!config.isRedirectsEnabled() || !this.redirectStrategy.isRedirected(currentRequest.getOriginal(), (HttpResponse)response, (HttpContext)context)) {
                    return response;
                }
                if (!RequestEntityProxy.isRepeatable((HttpRequest)currentRequest)) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug((Object)"Cannot redirect non-repeatable request");
                    }
                    return response;
                }
                if (redirectCount >= maxRedirects) {
                    throw new RedirectException("Maximum redirects (" + maxRedirects + ") exceeded");
                }
                ++redirectCount;
                final HttpRequest redirect = (HttpRequest)this.redirectStrategy.getRedirect(currentRequest.getOriginal(), (HttpResponse)response, (HttpContext)context);
                if (!redirect.headerIterator().hasNext()) {
                    final HttpRequest original = request.getOriginal();
                    redirect.setHeaders(original.getAllHeaders());
                }
                currentRequest = HttpRequestWrapper.wrap(redirect);
                if (currentRequest instanceof HttpEntityEnclosingRequest) {
                    RequestEntityProxy.enhance((HttpEntityEnclosingRequest)currentRequest);
                }
                final URI uri = currentRequest.getURI();
                final HttpHost newTarget = URIUtils.extractHost(uri);
                if (newTarget == null) {
                    throw new ProtocolException("Redirect URI does not specify a valid host name: " + uri);
                }
                if (!currentRoute.getTargetHost().equals((Object)newTarget)) {
                    final AuthState targetAuthState = context.getTargetAuthState();
                    if (targetAuthState != null) {
                        this.log.debug((Object)"Resetting target auth state");
                        targetAuthState.reset();
                    }
                    final AuthState proxyAuthState = context.getProxyAuthState();
                    if (proxyAuthState != null && proxyAuthState.isConnectionBased()) {
                        this.log.debug((Object)"Resetting proxy auth state");
                        proxyAuthState.reset();
                    }
                }
                currentRoute = this.routePlanner.determineRoute(newTarget, (HttpRequest)currentRequest, (HttpContext)context);
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)("Redirecting to '" + uri + "' via " + currentRoute));
                }
                EntityUtils.consume(response.getEntity());
                response.close();
            }
            catch (final RuntimeException ex) {
                response.close();
                throw ex;
            }
            catch (final IOException ex2) {
                response.close();
                throw ex2;
            }
            catch (final HttpException ex3) {
                try {
                    EntityUtils.consume(response.getEntity());
                }
                catch (final IOException ioex) {
                    this.log.debug((Object)"I/O error while releasing connection", (Throwable)ioex);
                }
                finally {
                    response.close();
                }
                throw ex3;
            }
        }
    }
}
