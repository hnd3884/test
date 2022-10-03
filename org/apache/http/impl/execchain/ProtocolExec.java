package org.apache.http.impl.execchain;

import org.apache.http.client.CredentialsProvider;
import org.apache.http.params.HttpParams;
import org.apache.http.HttpException;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpRequest;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.AuthScope;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpExecutionAware;
import org.apache.http.client.protocol.HttpClientContext;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.ProtocolException;
import org.apache.http.conn.routing.RouteInfo;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.util.Args;
import org.apache.commons.logging.LogFactory;
import org.apache.http.protocol.HttpProcessor;
import org.apache.commons.logging.Log;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class ProtocolExec implements ClientExecChain
{
    private final Log log;
    private final ClientExecChain requestExecutor;
    private final HttpProcessor httpProcessor;
    
    public ProtocolExec(final ClientExecChain requestExecutor, final HttpProcessor httpProcessor) {
        this.log = LogFactory.getLog((Class)this.getClass());
        Args.notNull((Object)requestExecutor, "HTTP client request executor");
        Args.notNull((Object)httpProcessor, "HTTP protocol processor");
        this.requestExecutor = requestExecutor;
        this.httpProcessor = httpProcessor;
    }
    
    void rewriteRequestURI(final HttpRequestWrapper request, final HttpRoute route, final boolean normalizeUri) throws ProtocolException {
        final URI uri = request.getURI();
        if (uri != null) {
            try {
                request.setURI(URIUtils.rewriteURIForRoute(uri, route, normalizeUri));
            }
            catch (final URISyntaxException ex) {
                throw new ProtocolException("Invalid URI: " + uri, (Throwable)ex);
            }
        }
    }
    
    @Override
    public CloseableHttpResponse execute(final HttpRoute route, final HttpRequestWrapper request, final HttpClientContext context, final HttpExecutionAware execAware) throws IOException, HttpException {
        Args.notNull((Object)route, "HTTP route");
        Args.notNull((Object)request, "HTTP request");
        Args.notNull((Object)context, "HTTP context");
        final HttpRequest original = request.getOriginal();
        URI uri = null;
        if (original instanceof HttpUriRequest) {
            uri = ((HttpUriRequest)original).getURI();
        }
        else {
            final String uriString = original.getRequestLine().getUri();
            try {
                uri = URI.create(uriString);
            }
            catch (final IllegalArgumentException ex) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)("Unable to parse '" + uriString + "' as a valid URI; " + "request URI and Host header may be inconsistent"), (Throwable)ex);
                }
            }
        }
        request.setURI(uri);
        this.rewriteRequestURI(request, route, context.getRequestConfig().isNormalizeUri());
        final HttpParams params = request.getParams();
        HttpHost virtualHost = (HttpHost)params.getParameter("http.virtual-host");
        if (virtualHost != null && virtualHost.getPort() == -1) {
            final int port = route.getTargetHost().getPort();
            if (port != -1) {
                virtualHost = new HttpHost(virtualHost.getHostName(), port, virtualHost.getSchemeName());
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Using virtual host" + virtualHost));
            }
        }
        HttpHost target = null;
        if (virtualHost != null) {
            target = virtualHost;
        }
        else if (uri != null && uri.isAbsolute() && uri.getHost() != null) {
            target = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        }
        if (target == null) {
            target = request.getTarget();
        }
        if (target == null) {
            target = route.getTargetHost();
        }
        if (uri != null) {
            final String userinfo = uri.getUserInfo();
            if (userinfo != null) {
                CredentialsProvider credsProvider = context.getCredentialsProvider();
                if (credsProvider == null) {
                    credsProvider = new BasicCredentialsProvider();
                    context.setCredentialsProvider(credsProvider);
                }
                credsProvider.setCredentials(new AuthScope(target), new UsernamePasswordCredentials(userinfo));
            }
        }
        context.setAttribute("http.target_host", (Object)target);
        context.setAttribute("http.route", (Object)route);
        context.setAttribute("http.request", (Object)request);
        this.httpProcessor.process((HttpRequest)request, (HttpContext)context);
        final CloseableHttpResponse response = this.requestExecutor.execute(route, request, context, execAware);
        try {
            context.setAttribute("http.response", (Object)response);
            this.httpProcessor.process((HttpResponse)response, (HttpContext)context);
            return response;
        }
        catch (final RuntimeException ex2) {
            response.close();
            throw ex2;
        }
        catch (final IOException ex3) {
            response.close();
            throw ex3;
        }
        catch (final HttpException ex4) {
            response.close();
            throw ex4;
        }
    }
}
