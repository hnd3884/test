package org.apache.http.impl.execchain;

import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.HttpHost;
import org.apache.http.conn.routing.RouteInfo;
import org.apache.http.conn.routing.RouteTracker;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpClientConnection;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectionRequest;
import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.impl.conn.ConnectionShutdownException;
import java.io.InterruptedIOException;
import org.apache.http.client.NonRepeatableRequestException;
import org.apache.http.auth.AuthProtocolState;
import org.apache.http.util.EntityUtils;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpRequest;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.http.concurrent.Cancellable;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.auth.AuthState;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpExecutionAware;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.conn.routing.BasicRouteDirector;
import org.apache.http.util.Args;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.routing.HttpRouteDirector;
import org.apache.http.client.UserTokenHandler;
import org.apache.http.impl.auth.HttpAuthenticator;
import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.commons.logging.Log;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class MainClientExec implements ClientExecChain
{
    private final Log log;
    private final HttpRequestExecutor requestExecutor;
    private final HttpClientConnectionManager connManager;
    private final ConnectionReuseStrategy reuseStrategy;
    private final ConnectionKeepAliveStrategy keepAliveStrategy;
    private final HttpProcessor proxyHttpProcessor;
    private final AuthenticationStrategy targetAuthStrategy;
    private final AuthenticationStrategy proxyAuthStrategy;
    private final HttpAuthenticator authenticator;
    private final UserTokenHandler userTokenHandler;
    private final HttpRouteDirector routeDirector;
    
    public MainClientExec(final HttpRequestExecutor requestExecutor, final HttpClientConnectionManager connManager, final ConnectionReuseStrategy reuseStrategy, final ConnectionKeepAliveStrategy keepAliveStrategy, final HttpProcessor proxyHttpProcessor, final AuthenticationStrategy targetAuthStrategy, final AuthenticationStrategy proxyAuthStrategy, final UserTokenHandler userTokenHandler) {
        this.log = LogFactory.getLog((Class)this.getClass());
        Args.notNull((Object)requestExecutor, "HTTP request executor");
        Args.notNull((Object)connManager, "Client connection manager");
        Args.notNull((Object)reuseStrategy, "Connection reuse strategy");
        Args.notNull((Object)keepAliveStrategy, "Connection keep alive strategy");
        Args.notNull((Object)proxyHttpProcessor, "Proxy HTTP processor");
        Args.notNull((Object)targetAuthStrategy, "Target authentication strategy");
        Args.notNull((Object)proxyAuthStrategy, "Proxy authentication strategy");
        Args.notNull((Object)userTokenHandler, "User token handler");
        this.authenticator = new HttpAuthenticator();
        this.routeDirector = new BasicRouteDirector();
        this.requestExecutor = requestExecutor;
        this.connManager = connManager;
        this.reuseStrategy = reuseStrategy;
        this.keepAliveStrategy = keepAliveStrategy;
        this.proxyHttpProcessor = proxyHttpProcessor;
        this.targetAuthStrategy = targetAuthStrategy;
        this.proxyAuthStrategy = proxyAuthStrategy;
        this.userTokenHandler = userTokenHandler;
    }
    
    public MainClientExec(final HttpRequestExecutor requestExecutor, final HttpClientConnectionManager connManager, final ConnectionReuseStrategy reuseStrategy, final ConnectionKeepAliveStrategy keepAliveStrategy, final AuthenticationStrategy targetAuthStrategy, final AuthenticationStrategy proxyAuthStrategy, final UserTokenHandler userTokenHandler) {
        this(requestExecutor, connManager, reuseStrategy, keepAliveStrategy, (HttpProcessor)new ImmutableHttpProcessor(new HttpRequestInterceptor[] { (HttpRequestInterceptor)new RequestTargetHost() }), targetAuthStrategy, proxyAuthStrategy, userTokenHandler);
    }
    
    @Override
    public CloseableHttpResponse execute(final HttpRoute route, final HttpRequestWrapper request, final HttpClientContext context, final HttpExecutionAware execAware) throws IOException, HttpException {
        Args.notNull((Object)route, "HTTP route");
        Args.notNull((Object)request, "HTTP request");
        Args.notNull((Object)context, "HTTP context");
        AuthState targetAuthState = context.getTargetAuthState();
        if (targetAuthState == null) {
            targetAuthState = new AuthState();
            context.setAttribute("http.auth.target-scope", (Object)targetAuthState);
        }
        AuthState proxyAuthState = context.getProxyAuthState();
        if (proxyAuthState == null) {
            proxyAuthState = new AuthState();
            context.setAttribute("http.auth.proxy-scope", (Object)proxyAuthState);
        }
        if (request instanceof HttpEntityEnclosingRequest) {
            RequestEntityProxy.enhance((HttpEntityEnclosingRequest)request);
        }
        Object userToken = context.getUserToken();
        final ConnectionRequest connRequest = this.connManager.requestConnection(route, userToken);
        if (execAware != null) {
            if (execAware.isAborted()) {
                connRequest.cancel();
                throw new RequestAbortedException("Request aborted");
            }
            execAware.setCancellable((Cancellable)connRequest);
        }
        final RequestConfig config = context.getRequestConfig();
        HttpClientConnection managedConn;
        try {
            final int timeout = config.getConnectionRequestTimeout();
            managedConn = connRequest.get((timeout > 0) ? ((long)timeout) : 0L, TimeUnit.MILLISECONDS);
        }
        catch (final InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw new RequestAbortedException("Request aborted", interrupted);
        }
        catch (final ExecutionException ex) {
            Throwable cause = ex.getCause();
            if (cause == null) {
                cause = ex;
            }
            throw new RequestAbortedException("Request execution failed", cause);
        }
        context.setAttribute("http.connection", (Object)managedConn);
        if (config.isStaleConnectionCheckEnabled() && managedConn.isOpen()) {
            this.log.debug((Object)"Stale connection check");
            if (managedConn.isStale()) {
                this.log.debug((Object)"Stale connection detected");
                managedConn.close();
            }
        }
        final ConnectionHolder connHolder = new ConnectionHolder(this.log, this.connManager, managedConn);
        try {
            if (execAware != null) {
                execAware.setCancellable((Cancellable)connHolder);
            }
            int execCount = 1;
            while (execCount <= 1 || RequestEntityProxy.isRepeatable((HttpRequest)request)) {
                if (execAware != null && execAware.isAborted()) {
                    throw new RequestAbortedException("Request aborted");
                }
                HttpResponse response = null;
                Label_1031: {
                    if (!managedConn.isOpen()) {
                        this.log.debug((Object)("Opening connection " + route));
                        try {
                            this.establishRoute(proxyAuthState, managedConn, route, (HttpRequest)request, context);
                        }
                        catch (final TunnelRefusedException ex2) {
                            if (this.log.isDebugEnabled()) {
                                this.log.debug((Object)ex2.getMessage());
                            }
                            response = ex2.getResponse();
                            break Label_1031;
                        }
                    }
                    final int timeout2 = config.getSocketTimeout();
                    if (timeout2 >= 0) {
                        managedConn.setSocketTimeout(timeout2);
                    }
                    if (execAware != null && execAware.isAborted()) {
                        throw new RequestAbortedException("Request aborted");
                    }
                    if (this.log.isDebugEnabled()) {
                        this.log.debug((Object)("Executing request " + request.getRequestLine()));
                    }
                    if (!request.containsHeader("Authorization")) {
                        if (this.log.isDebugEnabled()) {
                            this.log.debug((Object)("Target auth state: " + targetAuthState.getState()));
                        }
                        this.authenticator.generateAuthResponse((HttpRequest)request, targetAuthState, (HttpContext)context);
                    }
                    if (!request.containsHeader("Proxy-Authorization") && !route.isTunnelled()) {
                        if (this.log.isDebugEnabled()) {
                            this.log.debug((Object)("Proxy auth state: " + proxyAuthState.getState()));
                        }
                        this.authenticator.generateAuthResponse((HttpRequest)request, proxyAuthState, (HttpContext)context);
                    }
                    context.setAttribute("http.request", (Object)request);
                    response = this.requestExecutor.execute((HttpRequest)request, managedConn, (HttpContext)context);
                    if (this.reuseStrategy.keepAlive(response, (HttpContext)context)) {
                        final long duration = this.keepAliveStrategy.getKeepAliveDuration(response, (HttpContext)context);
                        if (this.log.isDebugEnabled()) {
                            String s;
                            if (duration > 0L) {
                                s = "for " + duration + " " + TimeUnit.MILLISECONDS;
                            }
                            else {
                                s = "indefinitely";
                            }
                            this.log.debug((Object)("Connection can be kept alive " + s));
                        }
                        connHolder.setValidFor(duration, TimeUnit.MILLISECONDS);
                        connHolder.markReusable();
                    }
                    else {
                        connHolder.markNonReusable();
                    }
                    if (this.needAuthentication(targetAuthState, proxyAuthState, route, response, context)) {
                        final HttpEntity entity = response.getEntity();
                        if (connHolder.isReusable()) {
                            EntityUtils.consume(entity);
                        }
                        else {
                            managedConn.close();
                            if (proxyAuthState.getState() == AuthProtocolState.SUCCESS && proxyAuthState.isConnectionBased()) {
                                this.log.debug((Object)"Resetting proxy auth state");
                                proxyAuthState.reset();
                            }
                            if (targetAuthState.getState() == AuthProtocolState.SUCCESS && targetAuthState.isConnectionBased()) {
                                this.log.debug((Object)"Resetting target auth state");
                                targetAuthState.reset();
                            }
                        }
                        final HttpRequest original = request.getOriginal();
                        if (!original.containsHeader("Authorization")) {
                            request.removeHeaders("Authorization");
                        }
                        if (!original.containsHeader("Proxy-Authorization")) {
                            request.removeHeaders("Proxy-Authorization");
                        }
                        ++execCount;
                        continue;
                    }
                }
                if (userToken == null) {
                    userToken = this.userTokenHandler.getUserToken((HttpContext)context);
                    context.setAttribute("http.user-token", userToken);
                }
                if (userToken != null) {
                    connHolder.setState(userToken);
                }
                final HttpEntity entity2 = response.getEntity();
                if (entity2 == null || !entity2.isStreaming()) {
                    connHolder.releaseConnection();
                    return new HttpResponseProxy(response, null);
                }
                return new HttpResponseProxy(response, connHolder);
            }
            throw new NonRepeatableRequestException("Cannot retry request with a non-repeatable request entity.");
        }
        catch (final ConnectionShutdownException ex3) {
            final InterruptedIOException ioex = new InterruptedIOException("Connection has been shut down");
            ioex.initCause(ex3);
            throw ioex;
        }
        catch (final HttpException ex4) {
            connHolder.abortConnection();
            throw ex4;
        }
        catch (final IOException ex5) {
            connHolder.abortConnection();
            if (proxyAuthState.isConnectionBased()) {
                proxyAuthState.reset();
            }
            if (targetAuthState.isConnectionBased()) {
                targetAuthState.reset();
            }
            throw ex5;
        }
        catch (final RuntimeException ex6) {
            connHolder.abortConnection();
            if (proxyAuthState.isConnectionBased()) {
                proxyAuthState.reset();
            }
            if (targetAuthState.isConnectionBased()) {
                targetAuthState.reset();
            }
            throw ex6;
        }
        catch (final Error error) {
            this.connManager.shutdown();
            throw error;
        }
    }
    
    void establishRoute(final AuthState proxyAuthState, final HttpClientConnection managedConn, final HttpRoute route, final HttpRequest request, final HttpClientContext context) throws HttpException, IOException {
        final RequestConfig config = context.getRequestConfig();
        final int timeout = config.getConnectTimeout();
        final RouteTracker tracker = new RouteTracker(route);
        int step;
        do {
            final HttpRoute fact = tracker.toRoute();
            step = this.routeDirector.nextStep(route, fact);
            switch (step) {
                case 1: {
                    this.connManager.connect(managedConn, route, (timeout > 0) ? timeout : 0, (HttpContext)context);
                    tracker.connectTarget(route.isSecure());
                    continue;
                }
                case 2: {
                    this.connManager.connect(managedConn, route, (timeout > 0) ? timeout : 0, (HttpContext)context);
                    final HttpHost proxy = route.getProxyHost();
                    tracker.connectProxy(proxy, route.isSecure() && !route.isTunnelled());
                    continue;
                }
                case 3: {
                    final boolean secure = this.createTunnelToTarget(proxyAuthState, managedConn, route, request, context);
                    this.log.debug((Object)"Tunnel to target created.");
                    tracker.tunnelTarget(secure);
                    continue;
                }
                case 4: {
                    final int hop = fact.getHopCount() - 1;
                    final boolean secure2 = this.createTunnelToProxy(route, hop, context);
                    this.log.debug((Object)"Tunnel to proxy created.");
                    tracker.tunnelProxy(route.getHopTarget(hop), secure2);
                    continue;
                }
                case 5: {
                    this.connManager.upgrade(managedConn, route, (HttpContext)context);
                    tracker.layerProtocol(route.isSecure());
                    continue;
                }
                case -1: {
                    throw new HttpException("Unable to establish route: planned = " + route + "; current = " + fact);
                }
                case 0: {
                    this.connManager.routeComplete(managedConn, route, (HttpContext)context);
                    continue;
                }
                default: {
                    throw new IllegalStateException("Unknown step indicator " + step + " from RouteDirector.");
                }
            }
        } while (step > 0);
    }
    
    private boolean createTunnelToTarget(final AuthState proxyAuthState, final HttpClientConnection managedConn, final HttpRoute route, final HttpRequest request, final HttpClientContext context) throws HttpException, IOException {
        final RequestConfig config = context.getRequestConfig();
        final int timeout = config.getConnectTimeout();
        final HttpHost target = route.getTargetHost();
        final HttpHost proxy = route.getProxyHost();
        HttpResponse response = null;
        final String authority = target.toHostString();
        final HttpRequest connect = (HttpRequest)new BasicHttpRequest("CONNECT", authority, request.getProtocolVersion());
        this.requestExecutor.preProcess(connect, this.proxyHttpProcessor, (HttpContext)context);
        while (response == null) {
            if (!managedConn.isOpen()) {
                this.connManager.connect(managedConn, route, (timeout > 0) ? timeout : 0, (HttpContext)context);
            }
            connect.removeHeaders("Proxy-Authorization");
            this.authenticator.generateAuthResponse(connect, proxyAuthState, (HttpContext)context);
            response = this.requestExecutor.execute(connect, managedConn, (HttpContext)context);
            this.requestExecutor.postProcess(response, this.proxyHttpProcessor, (HttpContext)context);
            final int status = response.getStatusLine().getStatusCode();
            if (status < 200) {
                throw new HttpException("Unexpected response to CONNECT request: " + response.getStatusLine());
            }
            if (!config.isAuthenticationEnabled() || !this.authenticator.isAuthenticationRequested(proxy, response, this.proxyAuthStrategy, proxyAuthState, (HttpContext)context) || !this.authenticator.handleAuthChallenge(proxy, response, this.proxyAuthStrategy, proxyAuthState, (HttpContext)context)) {
                continue;
            }
            if (this.reuseStrategy.keepAlive(response, (HttpContext)context)) {
                this.log.debug((Object)"Connection kept alive");
                final HttpEntity entity = response.getEntity();
                EntityUtils.consume(entity);
            }
            else {
                managedConn.close();
            }
            response = null;
        }
        final int status = response.getStatusLine().getStatusCode();
        if (status > 299) {
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                response.setEntity((HttpEntity)new BufferedHttpEntity(entity));
            }
            managedConn.close();
            throw new TunnelRefusedException("CONNECT refused by proxy: " + response.getStatusLine(), response);
        }
        return false;
    }
    
    private boolean createTunnelToProxy(final HttpRoute route, final int hop, final HttpClientContext context) throws HttpException {
        throw new HttpException("Proxy chains are not supported.");
    }
    
    private boolean needAuthentication(final AuthState targetAuthState, final AuthState proxyAuthState, final HttpRoute route, final HttpResponse response, final HttpClientContext context) {
        final RequestConfig config = context.getRequestConfig();
        if (config.isAuthenticationEnabled()) {
            HttpHost target = context.getTargetHost();
            if (target == null) {
                target = route.getTargetHost();
            }
            if (target.getPort() < 0) {
                target = new HttpHost(target.getHostName(), route.getTargetHost().getPort(), target.getSchemeName());
            }
            final boolean targetAuthRequested = this.authenticator.isAuthenticationRequested(target, response, this.targetAuthStrategy, targetAuthState, (HttpContext)context);
            HttpHost proxy = route.getProxyHost();
            if (proxy == null) {
                proxy = route.getTargetHost();
            }
            final boolean proxyAuthRequested = this.authenticator.isAuthenticationRequested(proxy, response, this.proxyAuthStrategy, proxyAuthState, (HttpContext)context);
            if (targetAuthRequested) {
                return this.authenticator.handleAuthChallenge(target, response, this.targetAuthStrategy, targetAuthState, (HttpContext)context);
            }
            if (proxyAuthRequested) {
                return this.authenticator.handleAuthChallenge(proxy, response, this.proxyAuthStrategy, proxyAuthState, (HttpContext)context);
            }
        }
        return false;
    }
}
