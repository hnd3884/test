package org.apache.http.impl.execchain;

import org.apache.http.HttpResponse;
import java.lang.reflect.UndeclaredThrowableException;
import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpExecutionAware;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.util.Args;
import org.apache.http.client.BackoffManager;
import org.apache.http.client.ConnectionBackoffStrategy;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class BackoffStrategyExec implements ClientExecChain
{
    private final ClientExecChain requestExecutor;
    private final ConnectionBackoffStrategy connectionBackoffStrategy;
    private final BackoffManager backoffManager;
    
    public BackoffStrategyExec(final ClientExecChain requestExecutor, final ConnectionBackoffStrategy connectionBackoffStrategy, final BackoffManager backoffManager) {
        Args.notNull((Object)requestExecutor, "HTTP client request executor");
        Args.notNull((Object)connectionBackoffStrategy, "Connection backoff strategy");
        Args.notNull((Object)backoffManager, "Backoff manager");
        this.requestExecutor = requestExecutor;
        this.connectionBackoffStrategy = connectionBackoffStrategy;
        this.backoffManager = backoffManager;
    }
    
    @Override
    public CloseableHttpResponse execute(final HttpRoute route, final HttpRequestWrapper request, final HttpClientContext context, final HttpExecutionAware execAware) throws IOException, HttpException {
        Args.notNull((Object)route, "HTTP route");
        Args.notNull((Object)request, "HTTP request");
        Args.notNull((Object)context, "HTTP context");
        CloseableHttpResponse out = null;
        try {
            out = this.requestExecutor.execute(route, request, context, execAware);
        }
        catch (final Exception ex) {
            if (out != null) {
                out.close();
            }
            if (this.connectionBackoffStrategy.shouldBackoff(ex)) {
                this.backoffManager.backOff(route);
            }
            if (ex instanceof RuntimeException) {
                throw (RuntimeException)ex;
            }
            if (ex instanceof HttpException) {
                throw (HttpException)ex;
            }
            if (ex instanceof IOException) {
                throw (IOException)ex;
            }
            throw new UndeclaredThrowableException(ex);
        }
        if (this.connectionBackoffStrategy.shouldBackoff((HttpResponse)out)) {
            this.backoffManager.backOff(route);
        }
        else {
            this.backoffManager.probe(route);
        }
        return out;
    }
}
