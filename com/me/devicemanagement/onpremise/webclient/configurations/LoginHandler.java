package com.me.devicemanagement.onpremise.webclient.configurations;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public interface LoginHandler
{
    public static final String HTTP = "http";
    public static final String HTTPS = "https";
    
    boolean productSpecificHandling(final HttpServletRequest p0, final HttpServletResponse p1, final ServletConfig p2);
    
    default String constructURL(final HttpServletRequest request, final String protocol, final String hostName, final String port, final String servletPath, final boolean isQueryParamRequired) {
        final StringBuilder builder = new StringBuilder();
        builder.append(protocol).append("://").append(hostName).append(":").append(port).append(request.getContextPath());
        builder.append(servletPath);
        if (isQueryParamRequired) {
            final String queryParam = request.getQueryString();
            if (queryParam != null && !queryParam.isEmpty()) {
                builder.append("?").append(queryParam);
            }
        }
        return builder.toString();
    }
}
