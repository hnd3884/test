package org.apache.jasper.servlet;

import java.util.EventListener;
import javax.servlet.Filter;
import javax.servlet.SessionCookieConfig;
import java.util.EnumSet;
import javax.servlet.SessionTrackingMode;
import javax.servlet.ServletRegistration;
import javax.servlet.FilterRegistration;
import java.util.Vector;
import javax.servlet.ServletException;
import javax.servlet.Servlet;
import java.util.HashSet;
import java.io.InputStream;
import java.net.MalformedURLException;
import org.apache.jasper.runtime.ExceptionUtils;
import java.io.File;
import javax.servlet.RequestDispatcher;
import java.util.Collections;
import java.util.Enumeration;
import org.apache.tomcat.JarScannerCallback;
import org.apache.tomcat.JarScanType;
import org.apache.tomcat.util.descriptor.web.FragmentJarScannerCallback;
import org.apache.tomcat.JarScanFilter;
import org.apache.tomcat.util.scan.StandardJarScanFilter;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.apache.tomcat.Jar;
import java.util.Iterator;
import org.apache.tomcat.util.scan.JarFactory;
import java.util.LinkedHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.io.IOException;
import org.apache.jasper.compiler.Localizer;
import org.apache.tomcat.util.descriptor.web.WebXmlParser;
import org.apache.jasper.JasperException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.descriptor.JspConfigDescriptor;
import java.util.List;
import org.apache.tomcat.util.descriptor.web.WebXml;
import java.net.URL;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentMap;
import java.util.Map;
import javax.servlet.ServletContext;

public class JspCServletContext implements ServletContext
{
    private final Map<String, Object> myAttributes;
    private final ConcurrentMap<String, String> myParameters;
    private final PrintWriter myLogWriter;
    private final URL myResourceBaseURL;
    private WebXml webXml;
    private List<URL> resourceJARs;
    private JspConfigDescriptor jspConfigDescriptor;
    private final ClassLoader loader;
    
    public JspCServletContext(final PrintWriter aLogWriter, final URL aResourceBaseURL, final ClassLoader classLoader, final boolean validate, final boolean blockExternal) throws JasperException {
        this.myParameters = new ConcurrentHashMap<String, String>();
        this.myAttributes = new HashMap<String, Object>();
        this.myParameters.put("org.apache.jasper.XML_BLOCK_EXTERNAL", String.valueOf(blockExternal));
        this.myLogWriter = aLogWriter;
        this.myResourceBaseURL = aResourceBaseURL;
        this.loader = classLoader;
        this.webXml = this.buildMergedWebXml(validate, blockExternal);
        this.jspConfigDescriptor = this.webXml.getJspConfigDescriptor();
    }
    
    private WebXml buildMergedWebXml(final boolean validate, final boolean blockExternal) throws JasperException {
        final WebXml webXml = new WebXml();
        final WebXmlParser webXmlParser = new WebXmlParser(validate, validate, blockExternal);
        webXmlParser.setClassLoader(this.getClass().getClassLoader());
        try {
            final URL url = this.getResource("/WEB-INF/web.xml");
            if (!webXmlParser.parseWebXml(url, webXml, false)) {
                throw new JasperException(Localizer.getMessage("jspc.error.invalidWebXml"));
            }
        }
        catch (final IOException e) {
            throw new JasperException(e);
        }
        if (webXml.isMetadataComplete()) {
            return webXml;
        }
        final Set<String> absoluteOrdering = webXml.getAbsoluteOrdering();
        if (absoluteOrdering != null && absoluteOrdering.isEmpty()) {
            return webXml;
        }
        final Map<String, WebXml> fragments = this.scanForFragments(webXmlParser);
        final Set<WebXml> orderedFragments = WebXml.orderWebFragments(webXml, (Map)fragments, (ServletContext)this);
        this.resourceJARs = this.scanForResourceJARs(orderedFragments, fragments.values());
        webXml.merge((Set)orderedFragments);
        return webXml;
    }
    
    private List<URL> scanForResourceJARs(final Set<WebXml> orderedFragments, final Collection<WebXml> fragments) throws JasperException {
        final List<URL> resourceJars = new ArrayList<URL>();
        final Set<WebXml> resourceFragments = new LinkedHashSet<WebXml>(orderedFragments);
        for (final WebXml fragment : fragments) {
            if (!resourceFragments.contains(fragment)) {
                resourceFragments.add(fragment);
            }
        }
        for (final WebXml resourceFragment : resourceFragments) {
            try (final Jar jar = JarFactory.newInstance(resourceFragment.getURL())) {
                if (jar.exists("META-INF/resources/")) {
                    resourceJars.add(resourceFragment.getURL());
                }
                jar.close();
            }
            catch (final IOException ioe) {
                throw new JasperException(ioe);
            }
        }
        return resourceJars;
    }
    
    private Map<String, WebXml> scanForFragments(final WebXmlParser webXmlParser) throws JasperException {
        final StandardJarScanner scanner = new StandardJarScanner();
        scanner.setScanClassPath(false);
        scanner.setJarScanFilter((JarScanFilter)new StandardJarScanFilter());
        final FragmentJarScannerCallback callback = new FragmentJarScannerCallback(webXmlParser, false, true);
        scanner.scan(JarScanType.PLUGGABILITY, (ServletContext)this, (JarScannerCallback)callback);
        if (!callback.isOk()) {
            throw new JasperException(Localizer.getMessage("jspc.error.invalidFragment"));
        }
        return callback.getFragments();
    }
    
    public Object getAttribute(final String name) {
        return this.myAttributes.get(name);
    }
    
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(this.myAttributes.keySet());
    }
    
    public ServletContext getContext(final String uripath) {
        return null;
    }
    
    public String getContextPath() {
        return null;
    }
    
    public String getInitParameter(final String name) {
        return this.myParameters.get(name);
    }
    
    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration((Collection<String>)this.myParameters.keySet());
    }
    
    public int getMajorVersion() {
        return 3;
    }
    
    public String getMimeType(final String file) {
        return null;
    }
    
    public int getMinorVersion() {
        return 1;
    }
    
    public RequestDispatcher getNamedDispatcher(final String name) {
        return null;
    }
    
    public String getRealPath(final String path) {
        if (!this.myResourceBaseURL.getProtocol().equals("file")) {
            return null;
        }
        if (!path.startsWith("/")) {
            return null;
        }
        try {
            final File f = new File(this.getResource(path).toURI());
            return f.getAbsolutePath();
        }
        catch (final Throwable t) {
            ExceptionUtils.handleThrowable(t);
            return null;
        }
    }
    
    public RequestDispatcher getRequestDispatcher(final String path) {
        return null;
    }
    
    public URL getResource(String path) throws MalformedURLException {
        if (!path.startsWith("/")) {
            throw new MalformedURLException(Localizer.getMessage("jsp.error.URLMustStartWithSlash", path));
        }
        path = path.substring(1);
        URL url = new URL(this.myResourceBaseURL, path);
        try {
            final InputStream is = url.openStream();
            final Throwable t2 = null;
            if (is != null) {
                if (t2 != null) {
                    try {
                        is.close();
                    }
                    catch (final Throwable x2) {
                        t2.addSuppressed(x2);
                    }
                }
                else {
                    is.close();
                }
            }
        }
        catch (final Throwable t) {
            ExceptionUtils.handleThrowable(t);
            url = null;
        }
        if (url == null && this.resourceJARs != null) {
            final String jarPath = "META-INF/resources/" + path;
            for (final URL jarUrl : this.resourceJARs) {
                try (final Jar jar = JarFactory.newInstance(jarUrl)) {
                    if (jar.exists(jarPath)) {
                        return new URL(jar.getURL(jarPath));
                    }
                }
                catch (final IOException ex) {}
            }
        }
        return url;
    }
    
    public InputStream getResourceAsStream(final String path) {
        try {
            return this.getResource(path).openStream();
        }
        catch (final Throwable t) {
            ExceptionUtils.handleThrowable(t);
            return null;
        }
    }
    
    public Set<String> getResourcePaths(String path) {
        final Set<String> thePaths = new HashSet<String>();
        if (!path.endsWith("/")) {
            path += "/";
        }
        final String basePath = this.getRealPath(path);
        if (basePath != null) {
            final File theBaseDir = new File(basePath);
            if (theBaseDir.isDirectory()) {
                final String[] theFiles = theBaseDir.list();
                if (theFiles != null) {
                    for (final String theFile : theFiles) {
                        final File testFile = new File(basePath + File.separator + theFile);
                        if (testFile.isFile()) {
                            thePaths.add(path + theFile);
                        }
                        else if (testFile.isDirectory()) {
                            thePaths.add(path + theFile + "/");
                        }
                    }
                }
            }
        }
        if (this.resourceJARs != null) {
            final String jarPath = "META-INF/resources" + path;
            for (final URL jarUrl : this.resourceJARs) {
                try (final Jar jar = JarFactory.newInstance(jarUrl)) {
                    jar.nextEntry();
                    for (String entryName = jar.getEntryName(); entryName != null; entryName = jar.getEntryName()) {
                        if (entryName.startsWith(jarPath) && entryName.length() > jarPath.length()) {
                            final int sep = entryName.indexOf(47, jarPath.length());
                            if (sep < 0) {
                                thePaths.add(entryName.substring(18));
                            }
                            else {
                                thePaths.add(entryName.substring(18, sep + 1));
                            }
                        }
                        jar.nextEntry();
                    }
                }
                catch (final IOException e) {
                    this.log(e.getMessage(), e);
                }
            }
        }
        return thePaths;
    }
    
    public String getServerInfo() {
        return "JspC/ApacheTomcat8";
    }
    
    @Deprecated
    public Servlet getServlet(final String name) throws ServletException {
        return null;
    }
    
    public String getServletContextName() {
        return this.getServerInfo();
    }
    
    @Deprecated
    public Enumeration<String> getServletNames() {
        return new Vector<String>().elements();
    }
    
    @Deprecated
    public Enumeration<Servlet> getServlets() {
        return new Vector<Servlet>().elements();
    }
    
    public void log(final String message) {
        this.myLogWriter.println(message);
    }
    
    @Deprecated
    public void log(final Exception exception, final String message) {
        this.log(message, exception);
    }
    
    public void log(final String message, final Throwable exception) {
        this.myLogWriter.println(message);
        exception.printStackTrace(this.myLogWriter);
    }
    
    public void removeAttribute(final String name) {
        this.myAttributes.remove(name);
    }
    
    public void setAttribute(final String name, final Object value) {
        this.myAttributes.put(name, value);
    }
    
    public FilterRegistration.Dynamic addFilter(final String filterName, final String className) {
        return null;
    }
    
    public ServletRegistration.Dynamic addServlet(final String servletName, final String className) {
        return null;
    }
    
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return EnumSet.noneOf(SessionTrackingMode.class);
    }
    
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        return EnumSet.noneOf(SessionTrackingMode.class);
    }
    
    public SessionCookieConfig getSessionCookieConfig() {
        return null;
    }
    
    public void setSessionTrackingModes(final Set<SessionTrackingMode> sessionTrackingModes) {
    }
    
    public FilterRegistration.Dynamic addFilter(final String filterName, final Filter filter) {
        return null;
    }
    
    public FilterRegistration.Dynamic addFilter(final String filterName, final Class<? extends Filter> filterClass) {
        return null;
    }
    
    public ServletRegistration.Dynamic addServlet(final String servletName, final Servlet servlet) {
        return null;
    }
    
    public ServletRegistration.Dynamic addServlet(final String servletName, final Class<? extends Servlet> servletClass) {
        return null;
    }
    
    public <T extends Filter> T createFilter(final Class<T> c) throws ServletException {
        return null;
    }
    
    public <T extends Servlet> T createServlet(final Class<T> c) throws ServletException {
        return null;
    }
    
    public FilterRegistration getFilterRegistration(final String filterName) {
        return null;
    }
    
    public ServletRegistration getServletRegistration(final String servletName) {
        return null;
    }
    
    public boolean setInitParameter(final String name, final String value) {
        return this.myParameters.putIfAbsent(name, value) == null;
    }
    
    public void addListener(final Class<? extends EventListener> listenerClass) {
    }
    
    public void addListener(final String className) {
    }
    
    public <T extends EventListener> void addListener(final T t) {
    }
    
    public <T extends EventListener> T createListener(final Class<T> c) throws ServletException {
        return null;
    }
    
    public void declareRoles(final String... roleNames) {
    }
    
    public ClassLoader getClassLoader() {
        return this.loader;
    }
    
    public int getEffectiveMajorVersion() {
        return this.webXml.getMajorVersion();
    }
    
    public int getEffectiveMinorVersion() {
        return this.webXml.getMinorVersion();
    }
    
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        return null;
    }
    
    public JspConfigDescriptor getJspConfigDescriptor() {
        return this.jspConfigDescriptor;
    }
    
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        return null;
    }
    
    public String getVirtualServerName() {
        return null;
    }
}
