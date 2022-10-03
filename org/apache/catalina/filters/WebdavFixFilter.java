package org.apache.catalina.filters;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import org.apache.tomcat.util.res.StringManager;
import javax.servlet.Filter;

public class WebdavFixFilter implements Filter
{
    protected static final StringManager sm;
    private static final String UA_MINIDIR_START = "Microsoft-WebDAV-MiniRedir";
    private static final String UA_MINIDIR_5_1_2600 = "Microsoft-WebDAV-MiniRedir/5.1.2600";
    private static final String UA_MINIDIR_5_2_3790 = "Microsoft-WebDAV-MiniRedir/5.2.3790";
    
    public void init(final FilterConfig filterConfig) throws ServletException {
    }
    
    public void destroy() {
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }
        final HttpServletRequest httpRequest = (HttpServletRequest)request;
        final HttpServletResponse httpResponse = (HttpServletResponse)response;
        final String ua = httpRequest.getHeader("User-Agent");
        if (ua == null || ua.length() == 0 || !ua.startsWith("Microsoft-WebDAV-MiniRedir")) {
            chain.doFilter(request, response);
        }
        else if (ua.startsWith("Microsoft-WebDAV-MiniRedir/5.1.2600")) {
            httpResponse.sendRedirect(this.buildRedirect(httpRequest));
        }
        else if (ua.startsWith("Microsoft-WebDAV-MiniRedir/5.2.3790")) {
            if (!httpRequest.getContextPath().isEmpty()) {
                request.getServletContext().log(WebdavFixFilter.sm.getString("webDavFilter.xpRootContext"));
            }
            request.getServletContext().log(WebdavFixFilter.sm.getString("webDavFilter.xpProblem"));
            chain.doFilter(request, response);
        }
        else {
            httpResponse.sendRedirect(this.buildRedirect(httpRequest));
        }
    }
    
    private String buildRedirect(final HttpServletRequest request) {
        final StringBuilder location = new StringBuilder(request.getRequestURL().length());
        location.append(request.getScheme());
        location.append("://");
        location.append(request.getServerName());
        location.append(':');
        location.append(request.getServerPort());
        location.append(request.getRequestURI());
        return location.toString();
    }
    
    static {
        sm = StringManager.getManager((Class)WebdavFixFilter.class);
    }
}
