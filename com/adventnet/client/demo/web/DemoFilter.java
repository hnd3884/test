package com.adventnet.client.demo.web;

import java.io.IOException;
import org.apache.struts.action.ActionForward;
import java.util.Enumeration;
import java.util.Map;
import com.adventnet.client.view.web.WebViewAPI;
import javax.servlet.http.HttpServletResponse;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import javax.servlet.Filter;

public class DemoFilter implements Filter
{
    FilterConfig filterConfig;
    
    public DemoFilter() {
        this.filterConfig = null;
    }
    
    public void init(final FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws ServletException, IOException {
        final String filterViewOnAction = this.getForwardPath((HttpServletRequest)request);
        final Enumeration<String> enume = this.filterConfig.getInitParameterNames();
        if (enume.hasMoreElements()) {
            final String str = enume.nextElement();
            final String value = this.filterConfig.getInitParameter(str);
            final StringTokenizer token = new StringTokenizer(value, ",");
            while (token.hasMoreTokens()) {
                final String paramValue = "/" + token.nextToken();
                if (paramValue.equals(filterViewOnAction)) {
                    try {
                        final ActionForward actionForward = WebViewAPI.sendResponse((HttpServletRequest)request, (HttpServletResponse)response, false, "Requested Operation is disabled for Online Demo.", null);
                        return;
                    }
                    catch (final Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        chain.doFilter(request, response);
    }
    
    private String getForwardPath(final HttpServletRequest request) {
        final String path = request.getContextPath() + "/STATE_ID/";
        String forwardPath = request.getRequestURI();
        if (!forwardPath.startsWith(path)) {
            return forwardPath;
        }
        final int index = forwardPath.indexOf(47, path.length());
        if (index > 0) {
            forwardPath = forwardPath.substring(index);
        }
        return forwardPath;
    }
    
    public void destroy() {
    }
}
