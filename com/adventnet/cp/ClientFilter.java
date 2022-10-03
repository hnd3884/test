package com.adventnet.cp;

import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.util.logging.Level;
import javax.servlet.FilterConfig;
import java.util.logging.Logger;
import javax.servlet.Filter;

public class ClientFilter implements Filter
{
    private static Logger logger;
    
    protected Integer initialValue() {
        return -1;
    }
    
    public void init(final FilterConfig filterConfig) {
        ClientFilter.logger.log(Level.FINEST, "Client Filter initialized");
    }
    
    public static Integer getThreadLocalDB() {
        return MultiDSUtil.getThreadLocal();
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        MultiDSUtil.setThreadLocal();
        chain.doFilter(request, response);
        MultiDSUtil.removeThreadLocal();
    }
    
    public void destroy() {
    }
    
    static {
        ClientFilter.logger = Logger.getLogger(ClientFilter.class.getName());
    }
}
