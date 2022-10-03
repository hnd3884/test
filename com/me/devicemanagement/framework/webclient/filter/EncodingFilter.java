package com.me.devicemanagement.framework.webclient.filter;

import java.io.IOException;
import java.util.regex.Matcher;
import com.me.devicemanagement.framework.server.util.SecurityUtil;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import java.util.logging.Level;
import javax.servlet.FilterConfig;
import java.util.logging.Logger;
import javax.servlet.Filter;

public class EncodingFilter implements Filter
{
    String[] encodingExcludeUrls;
    private static final Logger LOGGER;
    
    public EncodingFilter() {
        this.encodingExcludeUrls = null;
    }
    
    public void init(final FilterConfig filterConfig) throws ServletException {
        EncodingFilter.LOGGER.log(Level.FINE, "Inside EncodingFilter init()");
        final String initEncodingExcludeUrls = filterConfig.getInitParameter("encodingExcludeUrls");
        if (initEncodingExcludeUrls != null) {
            this.encodingExcludeUrls = initEncodingExcludeUrls.split(",");
        }
    }
    
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;
        servletRequest.setCharacterEncoding("UTF-8");
        boolean isExcludeCharacterEncoding = false;
        if (this.encodingExcludeUrls != null) {
            for (final String excludeUrl : this.encodingExcludeUrls) {
                final Pattern pattern = Pattern.compile(excludeUrl);
                final Matcher matcher = pattern.matcher(SecurityUtil.getNormalizedRequestURI(httpServletRequest));
                if (matcher.matches()) {
                    EncodingFilter.LOGGER.log(Level.INFO, "Response Encoding excluded for pattern: {0}", new Object[] { excludeUrl });
                    isExcludeCharacterEncoding = true;
                    break;
                }
            }
        }
        if (!isExcludeCharacterEncoding) {
            servletResponse.setCharacterEncoding("UTF-8");
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
    
    public void destroy() {
    }
    
    static {
        LOGGER = Logger.getLogger(EncodingFilter.class.getName());
    }
}
