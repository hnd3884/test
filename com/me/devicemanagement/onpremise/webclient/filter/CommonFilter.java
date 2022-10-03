package com.me.devicemanagement.onpremise.webclient.filter;

import com.adventnet.sym.logging.LoggingThreadLocal;
import java.util.UUID;
import javax.servlet.ServletException;
import java.io.IOException;
import com.adventnet.iam.security.SecurityUtil;
import com.me.devicemanagement.framework.server.logger.seconelinelogger.OneLineLoggerThreadLocal;
import com.me.devicemanagement.framework.server.eventlog.EventLogThreadLocal;
import com.me.devicemanagement.framework.server.security.DMCookieUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.util.Properties;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import java.util.logging.Logger;
import javax.servlet.Filter;

public class CommonFilter implements Filter
{
    private static Logger logger;
    private static boolean isSecurityHeaderNeeded;
    private RequestDispatcher root;
    private FilterConfig config;
    private String forward;
    
    public CommonFilter() {
        this.root = null;
        this.config = null;
        this.forward = null;
    }
    
    public void destroy() {
        this.root = null;
    }
    
    public void init(final FilterConfig filterconfig) {
        this.config = filterconfig;
        this.forward = filterconfig.getInitParameter("forward");
        try {
            final Properties wsProps = WebServerUtil.getWebServerSettings();
            CommonFilter.isSecurityHeaderNeeded = (wsProps != null && wsProps.getProperty("add.security.headers") != null && wsProps.getProperty("add.security.headers").equalsIgnoreCase("true"));
        }
        catch (final Exception e) {
            CommonFilter.logger.log(Level.SEVERE, "Exception caught in CommonFilter#init() ", e);
        }
    }
    
    public void doFilter(final ServletRequest servletrequest, final ServletResponse servletresponse, final FilterChain filterchain) throws IOException, ServletException {
        try {
            this.setLoggingId(servletrequest, servletresponse, filterchain);
            this.setQuickLoadHeader(servletrequest, servletresponse);
            DMCookieUtil.setCookieAttributes((HttpServletRequest)servletrequest, (HttpServletResponse)servletresponse);
            EventLogThreadLocal.setSourceIpAddress(servletrequest.getRemoteAddr());
            EventLogThreadLocal.setSourceHostName(servletrequest.getRemoteHost());
            OneLineLoggerThreadLocal.setOnelineLoggerDetails(servletrequest);
            servletrequest.setAttribute("isCsrfEnabled", (Object)true);
            try {
                servletrequest.setAttribute("cookieName", (Object)SecurityUtil.getCSRFCookieName((HttpServletRequest)servletrequest));
                servletrequest.setAttribute("csrfParamName", (Object)SecurityUtil.getCSRFParamName((HttpServletRequest)servletrequest));
            }
            catch (final Exception e) {
                servletrequest.setAttribute("cookieName", (Object)"dccookcsr");
                servletrequest.setAttribute("csrfParamName", (Object)"dcparamcsr");
            }
            this.setSecurityHeader(servletrequest, servletresponse);
            filterchain.doFilter(servletrequest, servletresponse);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        finally {
            EventLogThreadLocal.clearEventThreadLocalDetails();
            this.clearLoggingId();
            OneLineLoggerThreadLocal.clearOnelineLoggerThreadLocalDetails();
        }
    }
    
    private void setSecurityHeader(final ServletRequest servletRequest, final ServletResponse servletResponse) {
        if (CommonFilter.isSecurityHeaderNeeded) {
            final HttpServletRequest request = (HttpServletRequest)servletRequest;
            final HttpServletResponse response = (HttpServletResponse)servletResponse;
            response.setHeader("X-XSS-Protection", "1; mode=block");
            response.setHeader("Strict-Transport-Security", "max-age=63072000; includeSubdomains;");
        }
    }
    
    private void setQuickLoadHeader(final ServletRequest servletRequest, final ServletResponse servletResponse) {
        final HttpServletResponse response = (HttpServletResponse)servletResponse;
        final HttpServletRequest request = (HttpServletRequest)servletRequest;
        final String quickloadt = request.getParameter("qlt");
        if (quickloadt != null) {
            response.setHeader("QuickLoadRequestTime", quickloadt);
        }
    }
    
    private void setLoggingId(final ServletRequest servletrequest, final ServletResponse servletresponse, final FilterChain filterchain) {
        LoggingThreadLocal.setLoggingId(String.valueOf(UUID.randomUUID()));
    }
    
    private void clearLoggingId() {
        LoggingThreadLocal.clearLoggingId();
    }
    
    static {
        CommonFilter.logger = Logger.getLogger(CommonFilter.class.getName());
        CommonFilter.isSecurityHeaderNeeded = false;
    }
}
