package org.apache.catalina.core;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.Container;
import org.apache.tomcat.util.buf.MessageBytes;
import javax.servlet.Servlet;
import java.io.IOException;
import org.apache.coyote.CloseNowException;
import org.apache.catalina.connector.ClientAbortException;
import javax.servlet.ServletResponse;
import org.apache.tomcat.util.log.SystemLogHandler;
import org.apache.catalina.Wrapper;
import javax.servlet.ServletRequest;
import javax.servlet.DispatcherType;
import org.apache.tomcat.util.ExceptionUtils;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Response;
import org.apache.catalina.connector.Request;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.tomcat.util.res.StringManager;
import org.apache.catalina.valves.ValveBase;

final class StandardWrapperValve extends ValveBase
{
    private static final StringManager sm;
    private volatile long processingTime;
    private volatile long maxTime;
    private volatile long minTime;
    private final AtomicInteger requestCount;
    private final AtomicInteger errorCount;
    
    public StandardWrapperValve() {
        super(true);
        this.minTime = Long.MAX_VALUE;
        this.requestCount = new AtomicInteger(0);
        this.errorCount = new AtomicInteger(0);
    }
    
    @Override
    public final void invoke(final Request request, final Response response) throws IOException, ServletException {
        boolean unavailable = false;
        Throwable throwable = null;
        final long t1 = System.currentTimeMillis();
        this.requestCount.incrementAndGet();
        final StandardWrapper wrapper = (StandardWrapper)this.getContainer();
        Servlet servlet = null;
        final Context context = (Context)wrapper.getParent();
        if (!context.getState().isAvailable()) {
            response.sendError(503, StandardWrapperValve.sm.getString("standardContext.isUnavailable"));
            unavailable = true;
        }
        if (!unavailable && wrapper.isUnavailable()) {
            this.container.getLogger().info((Object)StandardWrapperValve.sm.getString("standardWrapper.isUnavailable", new Object[] { wrapper.getName() }));
            final long available = wrapper.getAvailable();
            if (available > 0L && available < Long.MAX_VALUE) {
                response.setDateHeader("Retry-After", available);
                response.sendError(503, StandardWrapperValve.sm.getString("standardWrapper.isUnavailable", new Object[] { wrapper.getName() }));
            }
            else if (available == Long.MAX_VALUE) {
                response.sendError(404, StandardWrapperValve.sm.getString("standardWrapper.notFound", new Object[] { wrapper.getName() }));
            }
            unavailable = true;
        }
        try {
            if (!unavailable) {
                servlet = wrapper.allocate();
            }
        }
        catch (final UnavailableException e) {
            this.container.getLogger().error((Object)StandardWrapperValve.sm.getString("standardWrapper.allocateException", new Object[] { wrapper.getName() }), (Throwable)e);
            final long available2 = wrapper.getAvailable();
            if (available2 > 0L && available2 < Long.MAX_VALUE) {
                response.setDateHeader("Retry-After", available2);
                response.sendError(503, StandardWrapperValve.sm.getString("standardWrapper.isUnavailable", new Object[] { wrapper.getName() }));
            }
            else if (available2 == Long.MAX_VALUE) {
                response.sendError(404, StandardWrapperValve.sm.getString("standardWrapper.notFound", new Object[] { wrapper.getName() }));
            }
        }
        catch (final ServletException e2) {
            this.container.getLogger().error((Object)StandardWrapperValve.sm.getString("standardWrapper.allocateException", new Object[] { wrapper.getName() }), StandardWrapper.getRootCause(e2));
            throwable = (Throwable)e2;
            this.exception(request, response, (Throwable)e2);
        }
        catch (final Throwable e3) {
            ExceptionUtils.handleThrowable(e3);
            this.container.getLogger().error((Object)StandardWrapperValve.sm.getString("standardWrapper.allocateException", new Object[] { wrapper.getName() }), e3);
            throwable = e3;
            this.exception(request, response, e3);
            servlet = null;
        }
        final MessageBytes requestPathMB = request.getRequestPathMB();
        DispatcherType dispatcherType = DispatcherType.REQUEST;
        if (request.getDispatcherType() == DispatcherType.ASYNC) {
            dispatcherType = DispatcherType.ASYNC;
        }
        request.setAttribute("org.apache.catalina.core.DISPATCHER_TYPE", dispatcherType);
        request.setAttribute("org.apache.catalina.core.DISPATCHER_REQUEST_PATH", requestPathMB);
        final ApplicationFilterChain filterChain = ApplicationFilterFactory.createFilterChain((ServletRequest)request, wrapper, servlet);
        final Container container = this.container;
        try {
            if (servlet != null && filterChain != null) {
                if (context.getSwallowOutput()) {
                    try {
                        SystemLogHandler.startCapture();
                        if (request.isAsyncDispatching()) {
                            request.getAsyncContextInternal().doInternalDispatch();
                        }
                        else {
                            filterChain.doFilter((ServletRequest)request.getRequest(), (ServletResponse)response.getResponse());
                        }
                    }
                    finally {
                        final String log = SystemLogHandler.stopCapture();
                        if (log != null && log.length() > 0) {
                            context.getLogger().info((Object)log);
                        }
                    }
                }
                else if (request.isAsyncDispatching()) {
                    request.getAsyncContextInternal().doInternalDispatch();
                }
                else {
                    filterChain.doFilter((ServletRequest)request.getRequest(), (ServletResponse)response.getResponse());
                }
            }
        }
        catch (final ClientAbortException | CloseNowException e4) {
            if (container.getLogger().isDebugEnabled()) {
                container.getLogger().debug((Object)StandardWrapperValve.sm.getString("standardWrapper.serviceException", new Object[] { wrapper.getName(), context.getName() }), (Throwable)e4);
            }
            throwable = e4;
            this.exception(request, response, e4);
        }
        catch (final IOException e4) {
            container.getLogger().error((Object)StandardWrapperValve.sm.getString("standardWrapper.serviceException", new Object[] { wrapper.getName(), context.getName() }), (Throwable)e4);
            throwable = e4;
            this.exception(request, response, e4);
        }
        catch (final UnavailableException e5) {
            container.getLogger().error((Object)StandardWrapperValve.sm.getString("standardWrapper.serviceException", new Object[] { wrapper.getName(), context.getName() }), (Throwable)e5);
            wrapper.unavailable(e5);
            final long available3 = wrapper.getAvailable();
            if (available3 > 0L && available3 < Long.MAX_VALUE) {
                response.setDateHeader("Retry-After", available3);
                response.sendError(503, StandardWrapperValve.sm.getString("standardWrapper.isUnavailable", new Object[] { wrapper.getName() }));
            }
            else if (available3 == Long.MAX_VALUE) {
                response.sendError(404, StandardWrapperValve.sm.getString("standardWrapper.notFound", new Object[] { wrapper.getName() }));
            }
        }
        catch (final ServletException e6) {
            final Throwable rootCause = StandardWrapper.getRootCause(e6);
            if (!(rootCause instanceof ClientAbortException)) {
                container.getLogger().error((Object)StandardWrapperValve.sm.getString("standardWrapper.serviceExceptionRoot", new Object[] { wrapper.getName(), context.getName(), e6.getMessage() }), rootCause);
            }
            throwable = (Throwable)e6;
            this.exception(request, response, (Throwable)e6);
        }
        catch (final Throwable e7) {
            ExceptionUtils.handleThrowable(e7);
            container.getLogger().error((Object)StandardWrapperValve.sm.getString("standardWrapper.serviceException", new Object[] { wrapper.getName(), context.getName() }), e7);
            throwable = e7;
            this.exception(request, response, e7);
        }
        finally {
            if (filterChain != null) {
                filterChain.release();
            }
            try {
                if (servlet != null) {
                    wrapper.deallocate(servlet);
                }
            }
            catch (final Throwable e8) {
                ExceptionUtils.handleThrowable(e8);
                container.getLogger().error((Object)StandardWrapperValve.sm.getString("standardWrapper.deallocateException", new Object[] { wrapper.getName() }), e8);
                if (throwable == null) {
                    throwable = e8;
                    this.exception(request, response, e8);
                }
            }
            try {
                if (servlet != null && wrapper.getAvailable() == Long.MAX_VALUE) {
                    wrapper.unload();
                }
            }
            catch (final Throwable e8) {
                ExceptionUtils.handleThrowable(e8);
                container.getLogger().error((Object)StandardWrapperValve.sm.getString("standardWrapper.unloadException", new Object[] { wrapper.getName() }), e8);
                if (throwable == null) {
                    this.exception(request, response, e8);
                }
            }
            final long t2 = System.currentTimeMillis();
            final long time = t2 - t1;
            this.processingTime += time;
            if (time > this.maxTime) {
                this.maxTime = time;
            }
            if (time < this.minTime) {
                this.minTime = time;
            }
        }
    }
    
    private void exception(final Request request, final Response response, final Throwable exception) {
        request.setAttribute("javax.servlet.error.exception", exception);
        response.setStatus(500);
        response.setError();
    }
    
    public long getProcessingTime() {
        return this.processingTime;
    }
    
    public long getMaxTime() {
        return this.maxTime;
    }
    
    public long getMinTime() {
        return this.minTime;
    }
    
    public int getRequestCount() {
        return this.requestCount.get();
    }
    
    public int getErrorCount() {
        return this.errorCount.get();
    }
    
    public void incrementErrorCount() {
        this.errorCount.incrementAndGet();
    }
    
    @Override
    protected void initInternal() throws LifecycleException {
    }
    
    static {
        sm = StringManager.getManager((Class)StandardWrapperValve.class);
    }
}
