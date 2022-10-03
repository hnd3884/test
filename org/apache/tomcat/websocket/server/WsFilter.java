package org.apache.tomcat.websocket.server;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import javax.servlet.Filter;

public class WsFilter implements Filter
{
    private WsServerContainer sc;
    
    public void init(final FilterConfig filterConfig) throws ServletException {
        this.sc = (WsServerContainer)filterConfig.getServletContext().getAttribute("javax.websocket.server.ServerContainer");
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        if (!this.sc.areEndpointsRegistered() || !UpgradeUtil.isWebSocketUpgradeRequest(request, response)) {
            chain.doFilter(request, response);
            return;
        }
        final HttpServletRequest req = (HttpServletRequest)request;
        final HttpServletResponse resp = (HttpServletResponse)response;
        final String pathInfo = req.getPathInfo();
        String path;
        if (pathInfo == null) {
            path = req.getServletPath();
        }
        else {
            path = req.getServletPath() + pathInfo;
        }
        final WsMappingResult mappingResult = this.sc.findMapping(path);
        if (mappingResult == null) {
            chain.doFilter(request, response);
            return;
        }
        UpgradeUtil.doUpgrade(this.sc, req, resp, mappingResult.getConfig(), mappingResult.getPathParams());
    }
    
    public void destroy() {
    }
}
