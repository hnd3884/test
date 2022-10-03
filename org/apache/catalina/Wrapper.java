package org.apache.catalina;

import javax.servlet.MultipartConfigElement;
import javax.servlet.UnavailableException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

public interface Wrapper extends Container
{
    public static final String ADD_MAPPING_EVENT = "addMapping";
    public static final String REMOVE_MAPPING_EVENT = "removeMapping";
    
    long getAvailable();
    
    void setAvailable(final long p0);
    
    int getLoadOnStartup();
    
    void setLoadOnStartup(final int p0);
    
    String getRunAs();
    
    void setRunAs(final String p0);
    
    String getServletClass();
    
    void setServletClass(final String p0);
    
    String[] getServletMethods() throws ServletException;
    
    boolean isUnavailable();
    
    Servlet getServlet();
    
    void setServlet(final Servlet p0);
    
    void addInitParameter(final String p0, final String p1);
    
    void addMapping(final String p0);
    
    void addSecurityReference(final String p0, final String p1);
    
    Servlet allocate() throws ServletException;
    
    void deallocate(final Servlet p0) throws ServletException;
    
    String findInitParameter(final String p0);
    
    String[] findInitParameters();
    
    String[] findMappings();
    
    String findSecurityReference(final String p0);
    
    String[] findSecurityReferences();
    
    void incrementErrorCount();
    
    void load() throws ServletException;
    
    void removeInitParameter(final String p0);
    
    void removeMapping(final String p0);
    
    void removeSecurityReference(final String p0);
    
    void unavailable(final UnavailableException p0);
    
    void unload() throws ServletException;
    
    MultipartConfigElement getMultipartConfigElement();
    
    void setMultipartConfigElement(final MultipartConfigElement p0);
    
    boolean isAsyncSupported();
    
    void setAsyncSupported(final boolean p0);
    
    boolean isEnabled();
    
    void setEnabled(final boolean p0);
    
    @Deprecated
    void setServletSecurityAnnotationScanRequired(final boolean p0);
    
    @Deprecated
    void servletSecurityAnnotationScan() throws ServletException;
    
    boolean isOverridable();
    
    void setOverridable(final boolean p0);
}
