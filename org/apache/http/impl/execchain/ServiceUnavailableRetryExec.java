package org.apache.http.impl.execchain;

import org.apache.http.HttpException;
import java.io.IOException;
import org.apache.http.Header;
import java.io.InterruptedIOException;
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
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.commons.logging.Log;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class ServiceUnavailableRetryExec implements ClientExecChain
{
    private final Log log;
    private final ClientExecChain requestExecutor;
    private final ServiceUnavailableRetryStrategy retryStrategy;
    
    public ServiceUnavailableRetryExec(final ClientExecChain requestExecutor, final ServiceUnavailableRetryStrategy retryStrategy) {
        this.log = LogFactory.getLog((Class)this.getClass());
        Args.notNull((Object)requestExecutor, "HTTP request executor");
        Args.notNull((Object)retryStrategy, "Retry strategy");
        this.requestExecutor = requestExecutor;
        this.retryStrategy = retryStrategy;
    }
    
    @Override
    public CloseableHttpResponse execute(final HttpRoute route, final HttpRequestWrapper request, final HttpClientContext context, final HttpExecutionAware execAware) throws IOException, HttpException {
        final Header[] origheaders = request.getAllHeaders();
        int c = 1;
        while (true) {
            final CloseableHttpResponse response = this.requestExecutor.execute(route, request, context, execAware);
            try {
                if (!this.retryStrategy.retryRequest((HttpResponse)response, c, (HttpContext)context) || !RequestEntityProxy.isRepeatable((HttpRequest)request)) {
                    return response;
                }
                response.close();
                final long nextInterval = this.retryStrategy.getRetryInterval();
                if (nextInterval > 0L) {
                    try {
                        this.log.trace((Object)("Wait for " + nextInterval));
                        Thread.sleep(nextInterval);
                    }
                    catch (final InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new InterruptedIOException();
                    }
                }
                request.setHeaders(origheaders);
            }
            catch (final RuntimeException ex) {
                response.close();
                throw ex;
            }
            ++c;
        }
    }
}
