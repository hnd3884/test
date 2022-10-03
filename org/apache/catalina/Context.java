package org.apache.catalina;

import org.apache.tomcat.util.http.CookieProcessor;
import java.util.Map;
import javax.servlet.ServletSecurityElement;
import javax.servlet.ServletRegistration;
import java.util.Set;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.ServletRequest;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.apache.tomcat.util.descriptor.web.ApplicationParameter;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.JarScanner;
import javax.servlet.ServletContext;
import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.tomcat.util.descriptor.web.LoginConfig;
import java.net.URL;
import java.util.Locale;
import org.apache.tomcat.ContextBind;

public interface Context extends Container, ContextBind
{
    public static final String ADD_WELCOME_FILE_EVENT = "addWelcomeFile";
    public static final String REMOVE_WELCOME_FILE_EVENT = "removeWelcomeFile";
    public static final String CLEAR_WELCOME_FILES_EVENT = "clearWelcomeFiles";
    public static final String CHANGE_SESSION_ID_EVENT = "changeSessionId";
    
    boolean getAllowCasualMultipartParsing();
    
    void setAllowCasualMultipartParsing(final boolean p0);
    
    Object[] getApplicationEventListeners();
    
    void setApplicationEventListeners(final Object[] p0);
    
    Object[] getApplicationLifecycleListeners();
    
    void setApplicationLifecycleListeners(final Object[] p0);
    
    String getCharset(final Locale p0);
    
    URL getConfigFile();
    
    void setConfigFile(final URL p0);
    
    boolean getConfigured();
    
    void setConfigured(final boolean p0);
    
    boolean getCookies();
    
    void setCookies(final boolean p0);
    
    String getSessionCookieName();
    
    void setSessionCookieName(final String p0);
    
    boolean getUseHttpOnly();
    
    void setUseHttpOnly(final boolean p0);
    
    String getSessionCookieDomain();
    
    void setSessionCookieDomain(final String p0);
    
    String getSessionCookiePath();
    
    void setSessionCookiePath(final String p0);
    
    boolean getSessionCookiePathUsesTrailingSlash();
    
    void setSessionCookiePathUsesTrailingSlash(final boolean p0);
    
    boolean getCrossContext();
    
    String getAltDDName();
    
    void setAltDDName(final String p0);
    
    void setCrossContext(final boolean p0);
    
    boolean getDenyUncoveredHttpMethods();
    
    void setDenyUncoveredHttpMethods(final boolean p0);
    
    String getDisplayName();
    
    void setDisplayName(final String p0);
    
    boolean getDistributable();
    
    void setDistributable(final boolean p0);
    
    String getDocBase();
    
    void setDocBase(final String p0);
    
    String getEncodedPath();
    
    boolean getIgnoreAnnotations();
    
    void setIgnoreAnnotations(final boolean p0);
    
    LoginConfig getLoginConfig();
    
    void setLoginConfig(final LoginConfig p0);
    
    NamingResourcesImpl getNamingResources();
    
    void setNamingResources(final NamingResourcesImpl p0);
    
    String getPath();
    
    void setPath(final String p0);
    
    String getPublicId();
    
    void setPublicId(final String p0);
    
    boolean getReloadable();
    
    void setReloadable(final boolean p0);
    
    boolean getOverride();
    
    void setOverride(final boolean p0);
    
    boolean getPrivileged();
    
    void setPrivileged(final boolean p0);
    
    ServletContext getServletContext();
    
    int getSessionTimeout();
    
    void setSessionTimeout(final int p0);
    
    boolean getSwallowAbortedUploads();
    
    void setSwallowAbortedUploads(final boolean p0);
    
    boolean getSwallowOutput();
    
    void setSwallowOutput(final boolean p0);
    
    String getWrapperClass();
    
    void setWrapperClass(final String p0);
    
    boolean getXmlNamespaceAware();
    
    void setXmlNamespaceAware(final boolean p0);
    
    boolean getXmlValidation();
    
    void setXmlValidation(final boolean p0);
    
    boolean getXmlBlockExternal();
    
    void setXmlBlockExternal(final boolean p0);
    
    boolean getTldValidation();
    
    void setTldValidation(final boolean p0);
    
    JarScanner getJarScanner();
    
    void setJarScanner(final JarScanner p0);
    
    Authenticator getAuthenticator();
    
    void setLogEffectiveWebXml(final boolean p0);
    
    boolean getLogEffectiveWebXml();
    
    InstanceManager getInstanceManager();
    
    void setInstanceManager(final InstanceManager p0);
    
    void setContainerSciFilter(final String p0);
    
    String getContainerSciFilter();
    
    void addApplicationListener(final String p0);
    
    void addApplicationParameter(final ApplicationParameter p0);
    
    void addConstraint(final SecurityConstraint p0);
    
    void addErrorPage(final ErrorPage p0);
    
    void addFilterDef(final FilterDef p0);
    
    void addFilterMap(final FilterMap p0);
    
    void addFilterMapBefore(final FilterMap p0);
    
    void addLocaleEncodingMappingParameter(final String p0, final String p1);
    
    void addMimeMapping(final String p0, final String p1);
    
    void addParameter(final String p0, final String p1);
    
    void addRoleMapping(final String p0, final String p1);
    
    void addSecurityRole(final String p0);
    
    @Deprecated
    void addServletMapping(final String p0, final String p1);
    
    @Deprecated
    void addServletMapping(final String p0, final String p1, final boolean p2);
    
    void addServletMappingDecoded(final String p0, final String p1);
    
    void addServletMappingDecoded(final String p0, final String p1, final boolean p2);
    
    void addWatchedResource(final String p0);
    
    void addWelcomeFile(final String p0);
    
    void addWrapperLifecycle(final String p0);
    
    void addWrapperListener(final String p0);
    
    Wrapper createWrapper();
    
    String[] findApplicationListeners();
    
    ApplicationParameter[] findApplicationParameters();
    
    SecurityConstraint[] findConstraints();
    
    ErrorPage findErrorPage(final int p0);
    
    @Deprecated
    ErrorPage findErrorPage(final String p0);
    
    ErrorPage findErrorPage(final Throwable p0);
    
    ErrorPage[] findErrorPages();
    
    FilterDef findFilterDef(final String p0);
    
    FilterDef[] findFilterDefs();
    
    FilterMap[] findFilterMaps();
    
    String findMimeMapping(final String p0);
    
    String[] findMimeMappings();
    
    String findParameter(final String p0);
    
    String[] findParameters();
    
    String findRoleMapping(final String p0);
    
    boolean findSecurityRole(final String p0);
    
    String[] findSecurityRoles();
    
    String findServletMapping(final String p0);
    
    String[] findServletMappings();
    
    @Deprecated
    String findStatusPage(final int p0);
    
    @Deprecated
    int[] findStatusPages();
    
    ThreadBindingListener getThreadBindingListener();
    
    void setThreadBindingListener(final ThreadBindingListener p0);
    
    String[] findWatchedResources();
    
    boolean findWelcomeFile(final String p0);
    
    String[] findWelcomeFiles();
    
    String[] findWrapperLifecycles();
    
    String[] findWrapperListeners();
    
    boolean fireRequestInitEvent(final ServletRequest p0);
    
    boolean fireRequestDestroyEvent(final ServletRequest p0);
    
    void reload();
    
    void removeApplicationListener(final String p0);
    
    void removeApplicationParameter(final String p0);
    
    void removeConstraint(final SecurityConstraint p0);
    
    void removeErrorPage(final ErrorPage p0);
    
    void removeFilterDef(final FilterDef p0);
    
    void removeFilterMap(final FilterMap p0);
    
    void removeMimeMapping(final String p0);
    
    void removeParameter(final String p0);
    
    void removeRoleMapping(final String p0);
    
    void removeSecurityRole(final String p0);
    
    void removeServletMapping(final String p0);
    
    void removeWatchedResource(final String p0);
    
    void removeWelcomeFile(final String p0);
    
    void removeWrapperLifecycle(final String p0);
    
    void removeWrapperListener(final String p0);
    
    String getRealPath(final String p0);
    
    int getEffectiveMajorVersion();
    
    void setEffectiveMajorVersion(final int p0);
    
    int getEffectiveMinorVersion();
    
    void setEffectiveMinorVersion(final int p0);
    
    JspConfigDescriptor getJspConfigDescriptor();
    
    void setJspConfigDescriptor(final JspConfigDescriptor p0);
    
    void addServletContainerInitializer(final ServletContainerInitializer p0, final Set<Class<?>> p1);
    
    boolean getPaused();
    
    boolean isServlet22();
    
    Set<String> addServletSecurity(final ServletRegistration.Dynamic p0, final ServletSecurityElement p1);
    
    void setResourceOnlyServlets(final String p0);
    
    String getResourceOnlyServlets();
    
    boolean isResourceOnlyServlet(final String p0);
    
    String getBaseName();
    
    void setWebappVersion(final String p0);
    
    String getWebappVersion();
    
    void setFireRequestListenersOnForwards(final boolean p0);
    
    boolean getFireRequestListenersOnForwards();
    
    void setPreemptiveAuthentication(final boolean p0);
    
    boolean getPreemptiveAuthentication();
    
    void setSendRedirectBody(final boolean p0);
    
    boolean getSendRedirectBody();
    
    Loader getLoader();
    
    void setLoader(final Loader p0);
    
    WebResourceRoot getResources();
    
    void setResources(final WebResourceRoot p0);
    
    Manager getManager();
    
    void setManager(final Manager p0);
    
    void setAddWebinfClassesResources(final boolean p0);
    
    boolean getAddWebinfClassesResources();
    
    void addPostConstructMethod(final String p0, final String p1);
    
    void addPreDestroyMethod(final String p0, final String p1);
    
    void removePostConstructMethod(final String p0);
    
    void removePreDestroyMethod(final String p0);
    
    String findPostConstructMethod(final String p0);
    
    String findPreDestroyMethod(final String p0);
    
    Map<String, String> findPostConstructMethods();
    
    Map<String, String> findPreDestroyMethods();
    
    Object getNamingToken();
    
    void setCookieProcessor(final CookieProcessor p0);
    
    CookieProcessor getCookieProcessor();
    
    void setValidateClientProvidedNewSessionId(final boolean p0);
    
    boolean getValidateClientProvidedNewSessionId();
    
    void setMapperContextRootRedirectEnabled(final boolean p0);
    
    boolean getMapperContextRootRedirectEnabled();
    
    void setMapperDirectoryRedirectEnabled(final boolean p0);
    
    boolean getMapperDirectoryRedirectEnabled();
    
    void setUseRelativeRedirects(final boolean p0);
    
    boolean getUseRelativeRedirects();
    
    void setDispatchersUseEncodedPaths(final boolean p0);
    
    boolean getDispatchersUseEncodedPaths();
    
    void setRequestCharacterEncoding(final String p0);
    
    String getRequestCharacterEncoding();
    
    void setResponseCharacterEncoding(final String p0);
    
    String getResponseCharacterEncoding();
    
    void setAllowMultipleLeadingForwardSlashInPath(final boolean p0);
    
    boolean getAllowMultipleLeadingForwardSlashInPath();
    
    void incrementInProgressAsyncCount();
    
    void decrementInProgressAsyncCount();
    
    void setCreateUploadTargets(final boolean p0);
    
    boolean getCreateUploadTargets();
}
