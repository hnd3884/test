package org.glassfish.jersey.servlet;

import javax.servlet.ServletContext;
import java.util.Enumeration;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;

public final class WebServletConfig implements WebConfig
{
    private final ServletContainer servlet;
    
    public WebServletConfig(final ServletContainer servlet) {
        this.servlet = servlet;
    }
    
    @Override
    public ConfigType getConfigType() {
        return ConfigType.ServletConfig;
    }
    
    @Override
    public ServletConfig getServletConfig() {
        return this.servlet.getServletConfig();
    }
    
    @Override
    public FilterConfig getFilterConfig() {
        return null;
    }
    
    @Override
    public String getName() {
        return this.servlet.getServletName();
    }
    
    @Override
    public String getInitParameter(final String name) {
        return this.servlet.getInitParameter(name);
    }
    
    @Override
    public Enumeration getInitParameterNames() {
        return this.servlet.getInitParameterNames();
    }
    
    @Override
    public ServletContext getServletContext() {
        return this.servlet.getServletContext();
    }
}
