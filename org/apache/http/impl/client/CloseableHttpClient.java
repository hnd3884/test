package org.apache.http.impl.client;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import java.net.URI;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.util.Args;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.ClientProtocolException;
import java.io.IOException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpRequest;
import org.apache.http.HttpHost;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import java.io.Closeable;
import org.apache.http.client.HttpClient;

@Contract(threading = ThreadingBehavior.SAFE)
public abstract class CloseableHttpClient implements HttpClient, Closeable
{
    private final Log log;
    
    public CloseableHttpClient() {
        this.log = LogFactory.getLog((Class)this.getClass());
    }
    
    protected abstract CloseableHttpResponse doExecute(final HttpHost p0, final HttpRequest p1, final HttpContext p2) throws IOException, ClientProtocolException;
    
    public CloseableHttpResponse execute(final HttpHost target, final HttpRequest request, final HttpContext context) throws IOException, ClientProtocolException {
        return this.doExecute(target, request, context);
    }
    
    public CloseableHttpResponse execute(final HttpUriRequest request, final HttpContext context) throws IOException, ClientProtocolException {
        Args.notNull((Object)request, "HTTP request");
        return this.doExecute(determineTarget(request), (HttpRequest)request, context);
    }
    
    private static HttpHost determineTarget(final HttpUriRequest request) throws ClientProtocolException {
        HttpHost target = null;
        final URI requestURI = request.getURI();
        if (requestURI.isAbsolute()) {
            target = URIUtils.extractHost(requestURI);
            if (target == null) {
                throw new ClientProtocolException("URI does not specify a valid host name: " + requestURI);
            }
        }
        return target;
    }
    
    public CloseableHttpResponse execute(final HttpUriRequest request) throws IOException, ClientProtocolException {
        return this.execute(request, (HttpContext)null);
    }
    
    public CloseableHttpResponse execute(final HttpHost target, final HttpRequest request) throws IOException, ClientProtocolException {
        return this.doExecute(target, request, null);
    }
    
    @Override
    public <T> T execute(final HttpUriRequest request, final ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        return this.execute(request, responseHandler, (HttpContext)null);
    }
    
    @Override
    public <T> T execute(final HttpUriRequest request, final ResponseHandler<? extends T> responseHandler, final HttpContext context) throws IOException, ClientProtocolException {
        final HttpHost target = determineTarget(request);
        return this.execute(target, (HttpRequest)request, responseHandler, context);
    }
    
    @Override
    public <T> T execute(final HttpHost target, final HttpRequest request, final ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        return this.execute(target, request, responseHandler, (HttpContext)null);
    }
    
    @Override
    public <T> T execute(final HttpHost target, final HttpRequest request, final ResponseHandler<? extends T> responseHandler, final HttpContext context) throws IOException, ClientProtocolException {
        Args.notNull((Object)responseHandler, "Response handler");
        final CloseableHttpResponse response = this.execute(target, request, context);
        try {
            final T result = (T)responseHandler.handleResponse((HttpResponse)response);
            final HttpEntity entity = response.getEntity();
            EntityUtils.consume(entity);
            return result;
        }
        catch (final ClientProtocolException t) {
            final HttpEntity entity = response.getEntity();
            try {
                EntityUtils.consume(entity);
            }
            catch (final Exception t2) {
                this.log.warn((Object)"Error consuming content after an exception.", (Throwable)t2);
            }
            throw t;
        }
        finally {
            response.close();
        }
    }
}
