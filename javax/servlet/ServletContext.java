package javax.servlet;

import javax.servlet.descriptor.JspConfigDescriptor;
import java.util.EventListener;
import java.util.Map;
import java.util.Enumeration;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

public interface ServletContext
{
    public static final String TEMPDIR = "javax.servlet.context.tempdir";
    public static final String ORDERED_LIBS = "javax.servlet.context.orderedLibs";
    
    String getContextPath();
    
    ServletContext getContext(final String p0);
    
    int getMajorVersion();
    
    int getMinorVersion();
    
    int getEffectiveMajorVersion();
    
    int getEffectiveMinorVersion();
    
    String getMimeType(final String p0);
    
    Set<String> getResourcePaths(final String p0);
    
    URL getResource(final String p0) throws MalformedURLException;
    
    InputStream getResourceAsStream(final String p0);
    
    RequestDispatcher getRequestDispatcher(final String p0);
    
    RequestDispatcher getNamedDispatcher(final String p0);
    
    @Deprecated
    Servlet getServlet(final String p0) throws ServletException;
    
    @Deprecated
    Enumeration<Servlet> getServlets();
    
    @Deprecated
    Enumeration<String> getServletNames();
    
    void log(final String p0);
    
    @Deprecated
    void log(final Exception p0, final String p1);
    
    void log(final String p0, final Throwable p1);
    
    String getRealPath(final String p0);
    
    String getServerInfo();
    
    String getInitParameter(final String p0);
    
    Enumeration<String> getInitParameterNames();
    
    boolean setInitParameter(final String p0, final String p1);
    
    Object getAttribute(final String p0);
    
    Enumeration<String> getAttributeNames();
    
    void setAttribute(final String p0, final Object p1);
    
    void removeAttribute(final String p0);
    
    String getServletContextName();
    
    ServletRegistration.Dynamic addServlet(final String p0, final String p1);
    
    ServletRegistration.Dynamic addServlet(final String p0, final Servlet p1);
    
    ServletRegistration.Dynamic addServlet(final String p0, final Class<? extends Servlet> p1);
    
     <T extends Servlet> T createServlet(final Class<T> p0) throws ServletException;
    
    ServletRegistration getServletRegistration(final String p0);
    
    Map<String, ? extends ServletRegistration> getServletRegistrations();
    
    FilterRegistration.Dynamic addFilter(final String p0, final String p1);
    
    FilterRegistration.Dynamic addFilter(final String p0, final Filter p1);
    
    FilterRegistration.Dynamic addFilter(final String p0, final Class<? extends Filter> p1);
    
     <T extends Filter> T createFilter(final Class<T> p0) throws ServletException;
    
    FilterRegistration getFilterRegistration(final String p0);
    
    Map<String, ? extends FilterRegistration> getFilterRegistrations();
    
    SessionCookieConfig getSessionCookieConfig();
    
    void setSessionTrackingModes(final Set<SessionTrackingMode> p0);
    
    Set<SessionTrackingMode> getDefaultSessionTrackingModes();
    
    Set<SessionTrackingMode> getEffectiveSessionTrackingModes();
    
    void addListener(final String p0);
    
     <T extends EventListener> void addListener(final T p0);
    
    void addListener(final Class<? extends EventListener> p0);
    
     <T extends EventListener> T createListener(final Class<T> p0) throws ServletException;
    
    JspConfigDescriptor getJspConfigDescriptor();
    
    ClassLoader getClassLoader();
    
    void declareRoles(final String... p0);
    
    String getVirtualServerName();
}
