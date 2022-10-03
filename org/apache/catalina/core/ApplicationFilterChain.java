package org.apache.catalina.core;

import java.util.Set;
import java.security.Principal;
import javax.servlet.Filter;
import javax.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.catalina.security.SecurityUtil;
import javax.servlet.http.HttpServletRequest;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.io.IOException;
import javax.servlet.ServletException;
import java.security.PrivilegedExceptionAction;
import org.apache.catalina.Globals;
import org.apache.tomcat.util.res.StringManager;
import javax.servlet.Servlet;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.FilterChain;

public final class ApplicationFilterChain implements FilterChain
{
    private static final ThreadLocal<ServletRequest> lastServicedRequest;
    private static final ThreadLocal<ServletResponse> lastServicedResponse;
    public static final int INCREMENT = 10;
    private ApplicationFilterConfig[] filters;
    private int pos;
    private int n;
    private Servlet servlet;
    private boolean servletSupportsAsync;
    private static final StringManager sm;
    private static final Class<?>[] classType;
    private static final Class<?>[] classTypeUsedInService;
    
    public ApplicationFilterChain() {
        this.filters = new ApplicationFilterConfig[0];
        this.pos = 0;
        this.n = 0;
        this.servlet = null;
        this.servletSupportsAsync = false;
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response) throws IOException, ServletException {
        if (Globals.IS_SECURITY_ENABLED) {
            final ServletRequest req = request;
            final ServletResponse res = response;
            try {
                AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws ServletException, IOException {
                        ApplicationFilterChain.this.internalDoFilter(req, res);
                        return null;
                    }
                });
            }
            catch (final PrivilegedActionException pe) {
                final Exception e = pe.getException();
                if (e instanceof ServletException) {
                    throw (ServletException)e;
                }
                if (e instanceof IOException) {
                    throw (IOException)e;
                }
                if (e instanceof RuntimeException) {
                    throw (RuntimeException)e;
                }
                throw new ServletException(e.getMessage(), (Throwable)e);
            }
        }
        else {
            this.internalDoFilter(request, response);
        }
    }
    
    private void internalDoFilter(final ServletRequest request, final ServletResponse response) throws IOException, ServletException {
        if (this.pos < this.n) {
            final ApplicationFilterConfig filterConfig = this.filters[this.pos++];
            try {
                final Filter filter = filterConfig.getFilter();
                if (request.isAsyncSupported() && "false".equalsIgnoreCase(filterConfig.getFilterDef().getAsyncSupported())) {
                    request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", (Object)Boolean.FALSE);
                }
                if (Globals.IS_SECURITY_ENABLED) {
                    final ServletRequest req = request;
                    final ServletResponse res = response;
                    final Principal principal = ((HttpServletRequest)req).getUserPrincipal();
                    final Object[] args = { req, res, this };
                    SecurityUtil.doAsPrivilege("doFilter", filter, ApplicationFilterChain.classType, args, principal);
                }
                else {
                    filter.doFilter(request, response, (FilterChain)this);
                }
            }
            catch (final IOException | ServletException | RuntimeException e) {
                throw e;
            }
            catch (Throwable e2) {
                e2 = ExceptionUtils.unwrapInvocationTargetException(e2);
                ExceptionUtils.handleThrowable(e2);
                throw new ServletException(ApplicationFilterChain.sm.getString("filterChain.filter"), e2);
            }
            return;
        }
        try {
            if (ApplicationDispatcher.WRAP_SAME_OBJECT) {
                ApplicationFilterChain.lastServicedRequest.set(request);
                ApplicationFilterChain.lastServicedResponse.set(response);
            }
            if (request.isAsyncSupported() && !this.servletSupportsAsync) {
                request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", (Object)Boolean.FALSE);
            }
            if (request instanceof HttpServletRequest && response instanceof HttpServletResponse && Globals.IS_SECURITY_ENABLED) {
                final ServletRequest req2 = request;
                final ServletResponse res2 = response;
                final Principal principal2 = ((HttpServletRequest)req2).getUserPrincipal();
                final Object[] args2 = { req2, res2 };
                SecurityUtil.doAsPrivilege("service", this.servlet, ApplicationFilterChain.classTypeUsedInService, args2, principal2);
            }
            else {
                this.servlet.service(request, response);
            }
        }
        catch (final IOException | ServletException | RuntimeException e3) {
            throw e3;
        }
        catch (Throwable e4) {
            e4 = ExceptionUtils.unwrapInvocationTargetException(e4);
            ExceptionUtils.handleThrowable(e4);
            throw new ServletException(ApplicationFilterChain.sm.getString("filterChain.servlet"), e4);
        }
        finally {
            if (ApplicationDispatcher.WRAP_SAME_OBJECT) {
                ApplicationFilterChain.lastServicedRequest.set(null);
                ApplicationFilterChain.lastServicedResponse.set(null);
            }
        }
    }
    
    public static ServletRequest getLastServicedRequest() {
        return ApplicationFilterChain.lastServicedRequest.get();
    }
    
    public static ServletResponse getLastServicedResponse() {
        return ApplicationFilterChain.lastServicedResponse.get();
    }
    
    void addFilter(final ApplicationFilterConfig filterConfig) {
        for (final ApplicationFilterConfig filter : this.filters) {
            if (filter == filterConfig) {
                return;
            }
        }
        if (this.n == this.filters.length) {
            final ApplicationFilterConfig[] newFilters = new ApplicationFilterConfig[this.n + 10];
            System.arraycopy(this.filters, 0, newFilters, 0, this.n);
            this.filters = newFilters;
        }
        this.filters[this.n++] = filterConfig;
    }
    
    void release() {
        for (int i = 0; i < this.n; ++i) {
            this.filters[i] = null;
        }
        this.n = 0;
        this.pos = 0;
        this.servlet = null;
        this.servletSupportsAsync = false;
    }
    
    void reuse() {
        this.pos = 0;
    }
    
    void setServlet(final Servlet servlet) {
        this.servlet = servlet;
    }
    
    void setServletSupportsAsync(final boolean servletSupportsAsync) {
        this.servletSupportsAsync = servletSupportsAsync;
    }
    
    public void findNonAsyncFilters(final Set<String> result) {
        for (int i = 0; i < this.n; ++i) {
            final ApplicationFilterConfig filter = this.filters[i];
            if ("false".equalsIgnoreCase(filter.getFilterDef().getAsyncSupported())) {
                result.add(filter.getFilterClass());
            }
        }
    }
    
    static {
        if (ApplicationDispatcher.WRAP_SAME_OBJECT) {
            lastServicedRequest = new ThreadLocal<ServletRequest>();
            lastServicedResponse = new ThreadLocal<ServletResponse>();
        }
        else {
            lastServicedRequest = null;
            lastServicedResponse = null;
        }
        sm = StringManager.getManager((Class)ApplicationFilterChain.class);
        classType = new Class[] { ServletRequest.class, ServletResponse.class, FilterChain.class };
        classTypeUsedInService = new Class[] { ServletRequest.class, ServletResponse.class };
    }
}
