package org.apache.catalina.core;

import org.apache.coyote.Response;
import org.apache.juli.logging.LogFactory;
import org.apache.coyote.RequestInfo;
import org.apache.catalina.Valve;
import org.apache.catalina.Host;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.AsyncListener;
import javax.servlet.RequestDispatcher;
import org.apache.catalina.AsyncDispatcher;
import javax.servlet.ServletContext;
import org.apache.tomcat.util.buf.UDecoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Iterator;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.catalina.Globals;
import java.util.Collection;
import org.apache.coyote.ActionCode;
import java.util.ArrayList;
import org.apache.catalina.connector.Request;
import javax.servlet.AsyncEvent;
import org.apache.catalina.Context;
import java.util.List;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import org.apache.coyote.AsyncContextCallback;
import javax.servlet.AsyncContext;

public class AsyncContextImpl implements AsyncContext, AsyncContextCallback
{
    private static final Log log;
    protected static final StringManager sm;
    private final Object asyncContextLock;
    private volatile ServletRequest servletRequest;
    private volatile ServletResponse servletResponse;
    private final List<AsyncListenerWrapper> listeners;
    private boolean hasOriginalRequestAndResponse;
    private volatile Runnable dispatch;
    private Context context;
    private long timeout;
    private AsyncEvent event;
    private volatile Request request;
    
    public AsyncContextImpl(final Request request) {
        this.asyncContextLock = new Object();
        this.servletRequest = null;
        this.servletResponse = null;
        this.listeners = new ArrayList<AsyncListenerWrapper>();
        this.hasOriginalRequestAndResponse = true;
        this.dispatch = null;
        this.context = null;
        this.timeout = -1L;
        this.event = null;
        if (AsyncContextImpl.log.isDebugEnabled()) {
            this.logDebug("Constructor");
        }
        this.request = request;
    }
    
    public void complete() {
        if (AsyncContextImpl.log.isDebugEnabled()) {
            this.logDebug("complete   ");
        }
        this.check();
        this.request.getCoyoteRequest().action(ActionCode.ASYNC_COMPLETE, (Object)null);
    }
    
    public void fireOnComplete() {
        if (AsyncContextImpl.log.isDebugEnabled()) {
            AsyncContextImpl.log.debug((Object)AsyncContextImpl.sm.getString("asyncContextImpl.fireOnComplete"));
        }
        final List<AsyncListenerWrapper> listenersCopy = new ArrayList<AsyncListenerWrapper>(this.listeners);
        final ClassLoader oldCL = this.context.bind(Globals.IS_SECURITY_ENABLED, (ClassLoader)null);
        try {
            for (final AsyncListenerWrapper listener : listenersCopy) {
                try {
                    listener.fireOnComplete(this.event);
                }
                catch (final Throwable t) {
                    ExceptionUtils.handleThrowable(t);
                    AsyncContextImpl.log.warn((Object)AsyncContextImpl.sm.getString("asyncContextImpl.onCompleteError", new Object[] { listener.getClass().getName() }), t);
                }
            }
        }
        finally {
            this.context.fireRequestDestroyEvent((ServletRequest)this.request.getRequest());
            this.clearServletRequestResponse();
            this.context.unbind(Globals.IS_SECURITY_ENABLED, oldCL);
        }
    }
    
    public boolean timeout() {
        final AtomicBoolean result = new AtomicBoolean();
        this.request.getCoyoteRequest().action(ActionCode.ASYNC_TIMEOUT, (Object)result);
        final Context context = this.context;
        if (result.get()) {
            if (AsyncContextImpl.log.isDebugEnabled()) {
                AsyncContextImpl.log.debug((Object)AsyncContextImpl.sm.getString("asyncContextImpl.fireOnTimeout"));
            }
            final ClassLoader oldCL = context.bind(false, (ClassLoader)null);
            try {
                final List<AsyncListenerWrapper> listenersCopy = new ArrayList<AsyncListenerWrapper>(this.listeners);
                for (final AsyncListenerWrapper listener : listenersCopy) {
                    try {
                        listener.fireOnTimeout(this.event);
                    }
                    catch (final Throwable t) {
                        ExceptionUtils.handleThrowable(t);
                        AsyncContextImpl.log.warn((Object)AsyncContextImpl.sm.getString("asyncContextImpl.onTimeoutError", new Object[] { listener.getClass().getName() }), t);
                    }
                }
                this.request.getCoyoteRequest().action(ActionCode.ASYNC_IS_TIMINGOUT, (Object)result);
            }
            finally {
                context.unbind(false, oldCL);
            }
        }
        return !result.get();
    }
    
    public void dispatch() {
        this.check();
        final ServletRequest servletRequest = this.getRequest();
        String path;
        String cpath;
        if (servletRequest instanceof HttpServletRequest) {
            final HttpServletRequest sr = (HttpServletRequest)servletRequest;
            path = sr.getRequestURI();
            cpath = sr.getContextPath();
        }
        else {
            path = this.request.getRequestURI();
            cpath = this.request.getContextPath();
        }
        if (cpath.length() > 1) {
            path = path.substring(cpath.length());
        }
        if (!this.context.getDispatchersUseEncodedPaths()) {
            path = UDecoder.URLDecode(path, StandardCharsets.UTF_8);
        }
        this.dispatch(path);
    }
    
    public void dispatch(final String path) {
        this.check();
        this.dispatch(this.getRequest().getServletContext(), path);
    }
    
    public void dispatch(final ServletContext servletContext, final String path) {
        synchronized (this.asyncContextLock) {
            if (AsyncContextImpl.log.isDebugEnabled()) {
                this.logDebug("dispatch   ");
            }
            this.check();
            if (this.dispatch != null) {
                throw new IllegalStateException(AsyncContextImpl.sm.getString("asyncContextImpl.dispatchingStarted"));
            }
            if (this.request.getAttribute("javax.servlet.async.request_uri") == null) {
                this.request.setAttribute("javax.servlet.async.request_uri", this.request.getRequestURI());
                this.request.setAttribute("javax.servlet.async.context_path", this.request.getContextPath());
                this.request.setAttribute("javax.servlet.async.servlet_path", this.request.getServletPath());
                this.request.setAttribute("javax.servlet.async.path_info", this.request.getPathInfo());
                this.request.setAttribute("javax.servlet.async.query_string", this.request.getQueryString());
            }
            final RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher(path);
            if (!(requestDispatcher instanceof AsyncDispatcher)) {
                throw new UnsupportedOperationException(AsyncContextImpl.sm.getString("asyncContextImpl.noAsyncDispatcher"));
            }
            final AsyncDispatcher applicationDispatcher = (AsyncDispatcher)requestDispatcher;
            final ServletRequest servletRequest = this.getRequest();
            final ServletResponse servletResponse = this.getResponse();
            this.dispatch = new AsyncRunnable(this.request, applicationDispatcher, servletRequest, servletResponse);
            this.request.getCoyoteRequest().action(ActionCode.ASYNC_DISPATCH, (Object)null);
            this.clearServletRequestResponse();
        }
    }
    
    public ServletRequest getRequest() {
        this.check();
        if (this.servletRequest == null) {
            throw new IllegalStateException(AsyncContextImpl.sm.getString("asyncContextImpl.request.ise"));
        }
        return this.servletRequest;
    }
    
    public ServletResponse getResponse() {
        this.check();
        if (this.servletResponse == null) {
            throw new IllegalStateException(AsyncContextImpl.sm.getString("asyncContextImpl.response.ise"));
        }
        return this.servletResponse;
    }
    
    public void start(final Runnable run) {
        if (AsyncContextImpl.log.isDebugEnabled()) {
            this.logDebug("start      ");
        }
        this.check();
        final Runnable wrapper = new RunnableWrapper(run, this.context, this.request.getCoyoteRequest());
        this.request.getCoyoteRequest().action(ActionCode.ASYNC_RUN, (Object)wrapper);
    }
    
    public void addListener(final AsyncListener listener) {
        this.check();
        final AsyncListenerWrapper wrapper = new AsyncListenerWrapper();
        wrapper.setListener(listener);
        this.listeners.add(wrapper);
    }
    
    public void addListener(final AsyncListener listener, final ServletRequest servletRequest, final ServletResponse servletResponse) {
        this.check();
        final AsyncListenerWrapper wrapper = new AsyncListenerWrapper();
        wrapper.setListener(listener);
        wrapper.setServletRequest(servletRequest);
        wrapper.setServletResponse(servletResponse);
        this.listeners.add(wrapper);
    }
    
    public <T extends AsyncListener> T createListener(final Class<T> clazz) throws ServletException {
        this.check();
        T listener = null;
        try {
            listener = (T)this.context.getInstanceManager().newInstance(clazz.getName(), clazz.getClassLoader());
        }
        catch (final ReflectiveOperationException | NamingException e) {
            final ServletException se = new ServletException((Throwable)e);
            throw se;
        }
        catch (final Exception e) {
            ExceptionUtils.handleThrowable(e.getCause());
            final ServletException se = new ServletException((Throwable)e);
            throw se;
        }
        return listener;
    }
    
    public void recycle() {
        if (AsyncContextImpl.log.isDebugEnabled()) {
            this.logDebug("recycle    ");
        }
        this.context = null;
        this.dispatch = null;
        this.event = null;
        this.hasOriginalRequestAndResponse = true;
        this.listeners.clear();
        this.request = null;
        this.clearServletRequestResponse();
        this.timeout = -1L;
    }
    
    private void clearServletRequestResponse() {
        this.servletRequest = null;
        this.servletResponse = null;
    }
    
    public boolean isStarted() {
        final AtomicBoolean result = new AtomicBoolean(false);
        this.request.getCoyoteRequest().action(ActionCode.ASYNC_IS_STARTED, (Object)result);
        return result.get();
    }
    
    public void setStarted(final Context context, final ServletRequest request, final ServletResponse response, final boolean originalRequestResponse) {
        synchronized (this.asyncContextLock) {
            this.request.getCoyoteRequest().action(ActionCode.ASYNC_START, (Object)this);
            (this.context = context).incrementInProgressAsyncCount();
            this.servletRequest = request;
            this.servletResponse = response;
            this.hasOriginalRequestAndResponse = originalRequestResponse;
            this.event = new AsyncEvent((AsyncContext)this, request, response);
            final List<AsyncListenerWrapper> listenersCopy = new ArrayList<AsyncListenerWrapper>(this.listeners);
            this.listeners.clear();
            if (AsyncContextImpl.log.isDebugEnabled()) {
                AsyncContextImpl.log.debug((Object)AsyncContextImpl.sm.getString("asyncContextImpl.fireOnStartAsync"));
            }
            for (final AsyncListenerWrapper listener : listenersCopy) {
                try {
                    listener.fireOnStartAsync(this.event);
                }
                catch (final Throwable t) {
                    ExceptionUtils.handleThrowable(t);
                    AsyncContextImpl.log.warn((Object)AsyncContextImpl.sm.getString("asyncContextImpl.onStartAsyncError", new Object[] { listener.getClass().getName() }), t);
                }
            }
        }
    }
    
    public boolean hasOriginalRequestAndResponse() {
        this.check();
        return this.hasOriginalRequestAndResponse;
    }
    
    protected void doInternalDispatch() throws ServletException, IOException {
        if (AsyncContextImpl.log.isDebugEnabled()) {
            this.logDebug("intDispatch");
        }
        try {
            final Runnable runnable = this.dispatch;
            this.dispatch = null;
            runnable.run();
            if (!this.request.isAsync()) {
                this.fireOnComplete();
            }
        }
        catch (final RuntimeException x) {
            if (x.getCause() instanceof ServletException) {
                throw (ServletException)x.getCause();
            }
            if (x.getCause() instanceof IOException) {
                throw (IOException)x.getCause();
            }
            throw new ServletException((Throwable)x);
        }
    }
    
    public long getTimeout() {
        this.check();
        return this.timeout;
    }
    
    public void setTimeout(final long timeout) {
        this.check();
        this.timeout = timeout;
        this.request.getCoyoteRequest().action(ActionCode.ASYNC_SETTIMEOUT, (Object)timeout);
    }
    
    public boolean isAvailable() {
        final Context context = this.context;
        return context != null && context.getState().isAvailable();
    }
    
    public void setErrorState(final Throwable t, final boolean fireOnError) {
        if (t != null) {
            this.request.setAttribute("javax.servlet.error.exception", t);
        }
        this.request.getCoyoteRequest().action(ActionCode.ASYNC_ERROR, (Object)null);
        if (fireOnError) {
            if (AsyncContextImpl.log.isDebugEnabled()) {
                AsyncContextImpl.log.debug((Object)AsyncContextImpl.sm.getString("asyncContextImpl.fireOnError"));
            }
            final AsyncEvent errorEvent = new AsyncEvent(this.event.getAsyncContext(), this.event.getSuppliedRequest(), this.event.getSuppliedResponse(), t);
            final List<AsyncListenerWrapper> listenersCopy = new ArrayList<AsyncListenerWrapper>(this.listeners);
            for (final AsyncListenerWrapper listener : listenersCopy) {
                try {
                    listener.fireOnError(errorEvent);
                }
                catch (final Throwable t2) {
                    ExceptionUtils.handleThrowable(t2);
                    AsyncContextImpl.log.warn((Object)AsyncContextImpl.sm.getString("asyncContextImpl.onErrorError", new Object[] { listener.getClass().getName() }), t2);
                }
            }
        }
        final AtomicBoolean result = new AtomicBoolean();
        this.request.getCoyoteRequest().action(ActionCode.ASYNC_IS_ERROR, (Object)result);
        if (result.get()) {
            final ServletResponse servletResponse = this.servletResponse;
            if (servletResponse instanceof HttpServletResponse) {
                ((HttpServletResponse)servletResponse).setStatus(500);
            }
            final Host host = (Host)this.context.getParent();
            final Valve stdHostValve = host.getPipeline().getBasic();
            if (stdHostValve instanceof StandardHostValve) {
                ((StandardHostValve)stdHostValve).throwable(this.request, this.request.getResponse(), t);
            }
            this.request.getCoyoteRequest().action(ActionCode.ASYNC_IS_ERROR, (Object)result);
            if (result.get()) {
                this.complete();
            }
        }
    }
    
    public void incrementInProgressAsyncCount() {
        this.context.incrementInProgressAsyncCount();
    }
    
    public void decrementInProgressAsyncCount() {
        this.context.decrementInProgressAsyncCount();
    }
    
    private void logDebug(final String method) {
        final StringBuilder uri = new StringBuilder();
        String rHashCode;
        String crHashCode;
        String rpHashCode;
        String stage;
        if (this.request == null) {
            rHashCode = "null";
            crHashCode = "null";
            rpHashCode = "null";
            stage = "-";
            uri.append("N/A");
        }
        else {
            rHashCode = Integer.toHexString(this.request.hashCode());
            final org.apache.coyote.Request coyoteRequest = this.request.getCoyoteRequest();
            if (coyoteRequest == null) {
                crHashCode = "null";
                rpHashCode = "null";
                stage = "-";
            }
            else {
                crHashCode = Integer.toHexString(coyoteRequest.hashCode());
                final RequestInfo rp = coyoteRequest.getRequestProcessor();
                if (rp == null) {
                    rpHashCode = "null";
                    stage = "-";
                }
                else {
                    rpHashCode = Integer.toHexString(rp.hashCode());
                    stage = Integer.toString(rp.getStage());
                }
            }
            uri.append(this.request.getRequestURI());
            if (this.request.getQueryString() != null) {
                uri.append('?');
                uri.append(this.request.getQueryString());
            }
        }
        String threadName = Thread.currentThread().getName();
        final int len = threadName.length();
        if (len > 20) {
            threadName = threadName.substring(len - 20, len);
        }
        final String msg = String.format("Req: %1$8s  CReq: %2$8s  RP: %3$8s  Stage: %4$s  Thread: %5$20s  State: %6$20s  Method: %7$11s  URI: %8$s", rHashCode, crHashCode, rpHashCode, stage, threadName, "N/A", method, uri);
        if (AsyncContextImpl.log.isTraceEnabled()) {
            AsyncContextImpl.log.trace((Object)msg, (Throwable)new DebugException());
        }
        else {
            AsyncContextImpl.log.debug((Object)msg);
        }
    }
    
    private void check() {
        if (this.request == null) {
            throw new IllegalStateException(AsyncContextImpl.sm.getString("asyncContextImpl.requestEnded"));
        }
    }
    
    static {
        log = LogFactory.getLog((Class)AsyncContextImpl.class);
        sm = StringManager.getManager((Class)AsyncContextImpl.class);
    }
    
    private static class DebugException extends Exception
    {
        private static final long serialVersionUID = 1L;
    }
    
    private static class RunnableWrapper implements Runnable
    {
        private final Runnable wrapped;
        private final Context context;
        private final org.apache.coyote.Request coyoteRequest;
        
        public RunnableWrapper(final Runnable wrapped, final Context ctxt, final org.apache.coyote.Request coyoteRequest) {
            this.wrapped = wrapped;
            this.context = ctxt;
            this.coyoteRequest = coyoteRequest;
        }
        
        @Override
        public void run() {
            final ClassLoader oldCL = this.context.bind(Globals.IS_SECURITY_ENABLED, (ClassLoader)null);
            try {
                this.wrapped.run();
            }
            catch (final Throwable t) {
                ExceptionUtils.handleThrowable(t);
                this.context.getLogger().error((Object)AsyncContextImpl.sm.getString("asyncContextImpl.asyncRunnableError"), t);
                this.coyoteRequest.setAttribute("javax.servlet.error.exception", (Object)t);
                final Response coyoteResponse = this.coyoteRequest.getResponse();
                coyoteResponse.setStatus(500);
                coyoteResponse.setError();
            }
            finally {
                this.context.unbind(Globals.IS_SECURITY_ENABLED, oldCL);
            }
            this.coyoteRequest.action(ActionCode.DISPATCH_EXECUTE, (Object)null);
        }
    }
    
    private static class AsyncRunnable implements Runnable
    {
        private final AsyncDispatcher applicationDispatcher;
        private final Request request;
        private final ServletRequest servletRequest;
        private final ServletResponse servletResponse;
        
        public AsyncRunnable(final Request request, final AsyncDispatcher applicationDispatcher, final ServletRequest servletRequest, final ServletResponse servletResponse) {
            this.request = request;
            this.applicationDispatcher = applicationDispatcher;
            this.servletRequest = servletRequest;
            this.servletResponse = servletResponse;
        }
        
        @Override
        public void run() {
            this.request.getCoyoteRequest().action(ActionCode.ASYNC_DISPATCHED, (Object)null);
            try {
                this.applicationDispatcher.dispatch(this.servletRequest, this.servletResponse);
            }
            catch (final Exception e) {
                throw new RuntimeException(AsyncContextImpl.sm.getString("asyncContextImpl.asyncDispatchError"), e);
            }
        }
    }
}
