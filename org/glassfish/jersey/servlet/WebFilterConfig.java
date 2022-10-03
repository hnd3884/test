package org.glassfish.jersey.servlet;

import javax.servlet.ServletContext;
import java.util.Enumeration;
import javax.servlet.ServletConfig;
import javax.servlet.FilterConfig;

public final class WebFilterConfig implements WebConfig
{
    private final FilterConfig filterConfig;
    
    public WebFilterConfig(final FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }
    
    @Override
    public ConfigType getConfigType() {
        return ConfigType.FilterConfig;
    }
    
    @Override
    public ServletConfig getServletConfig() {
        return null;
    }
    
    @Override
    public FilterConfig getFilterConfig() {
        return this.filterConfig;
    }
    
    @Override
    public String getName() {
        return this.filterConfig.getFilterName();
    }
    
    @Override
    public String getInitParameter(final String name) {
        return this.filterConfig.getInitParameter(name);
    }
    
    @Override
    public Enumeration getInitParameterNames() {
        return this.filterConfig.getInitParameterNames();
    }
    
    @Override
    public ServletContext getServletContext() {
        return this.filterConfig.getServletContext();
    }
}
