package org.glassfish.jersey.servlet;

import javax.servlet.ServletContext;
import java.util.Enumeration;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;

public interface WebConfig
{
    ConfigType getConfigType();
    
    ServletConfig getServletConfig();
    
    FilterConfig getFilterConfig();
    
    String getName();
    
    String getInitParameter(final String p0);
    
    Enumeration getInitParameterNames();
    
    ServletContext getServletContext();
    
    public enum ConfigType
    {
        ServletConfig, 
        FilterConfig;
    }
}
