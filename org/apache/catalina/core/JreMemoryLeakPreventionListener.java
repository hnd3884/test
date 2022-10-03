package org.apache.catalina.core;

import org.apache.juli.logging.LogFactory;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import java.lang.reflect.Method;
import java.util.StringTokenizer;
import org.apache.catalina.startup.SafeForkJoinWorkerThreadFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.security.Security;
import java.lang.reflect.InvocationTargetException;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.compat.JreVendor;
import java.awt.Toolkit;
import javax.imageio.ImageIO;
import org.apache.tomcat.util.compat.JreCompat;
import java.sql.DriverManager;
import org.apache.catalina.LifecycleEvent;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import org.apache.catalina.LifecycleListener;

public class JreMemoryLeakPreventionListener implements LifecycleListener
{
    private static final Log log;
    private static final StringManager sm;
    private static final String FORK_JOIN_POOL_THREAD_FACTORY_PROPERTY = "java.util.concurrent.ForkJoinPool.common.threadFactory";
    private boolean appContextProtection;
    private boolean awtThreadProtection;
    private boolean gcDaemonProtection;
    private boolean securityPolicyProtection;
    private boolean securityLoginConfigurationProtection;
    private boolean tokenPollerProtection;
    private boolean urlCacheProtection;
    private boolean xmlParsingProtection;
    private boolean ldapPoolProtection;
    private boolean driverManagerProtection;
    private boolean forkJoinCommonPoolProtection;
    private String classesToInitialize;
    
    public JreMemoryLeakPreventionListener() {
        this.appContextProtection = false;
        this.awtThreadProtection = false;
        this.gcDaemonProtection = true;
        this.securityPolicyProtection = true;
        this.securityLoginConfigurationProtection = true;
        this.tokenPollerProtection = true;
        this.urlCacheProtection = true;
        this.xmlParsingProtection = true;
        this.ldapPoolProtection = true;
        this.driverManagerProtection = true;
        this.forkJoinCommonPoolProtection = true;
        this.classesToInitialize = null;
    }
    
    public boolean isAppContextProtection() {
        return this.appContextProtection;
    }
    
    public void setAppContextProtection(final boolean appContextProtection) {
        this.appContextProtection = appContextProtection;
    }
    
    public boolean isAWTThreadProtection() {
        return this.awtThreadProtection;
    }
    
    public void setAWTThreadProtection(final boolean awtThreadProtection) {
        this.awtThreadProtection = awtThreadProtection;
    }
    
    public boolean isGcDaemonProtection() {
        return this.gcDaemonProtection;
    }
    
    public void setGcDaemonProtection(final boolean gcDaemonProtection) {
        this.gcDaemonProtection = gcDaemonProtection;
    }
    
    public boolean isSecurityPolicyProtection() {
        return this.securityPolicyProtection;
    }
    
    public void setSecurityPolicyProtection(final boolean securityPolicyProtection) {
        this.securityPolicyProtection = securityPolicyProtection;
    }
    
    public boolean isSecurityLoginConfigurationProtection() {
        return this.securityLoginConfigurationProtection;
    }
    
    public void setSecurityLoginConfigurationProtection(final boolean securityLoginConfigurationProtection) {
        this.securityLoginConfigurationProtection = securityLoginConfigurationProtection;
    }
    
    public boolean isTokenPollerProtection() {
        return this.tokenPollerProtection;
    }
    
    public void setTokenPollerProtection(final boolean tokenPollerProtection) {
        this.tokenPollerProtection = tokenPollerProtection;
    }
    
    public boolean isUrlCacheProtection() {
        return this.urlCacheProtection;
    }
    
    public void setUrlCacheProtection(final boolean urlCacheProtection) {
        this.urlCacheProtection = urlCacheProtection;
    }
    
    public boolean isXmlParsingProtection() {
        return this.xmlParsingProtection;
    }
    
    public void setXmlParsingProtection(final boolean xmlParsingProtection) {
        this.xmlParsingProtection = xmlParsingProtection;
    }
    
    public boolean isLdapPoolProtection() {
        return this.ldapPoolProtection;
    }
    
    public void setLdapPoolProtection(final boolean ldapPoolProtection) {
        this.ldapPoolProtection = ldapPoolProtection;
    }
    
    public boolean isDriverManagerProtection() {
        return this.driverManagerProtection;
    }
    
    public void setDriverManagerProtection(final boolean driverManagerProtection) {
        this.driverManagerProtection = driverManagerProtection;
    }
    
    public boolean getForkJoinCommonPoolProtection() {
        return this.forkJoinCommonPoolProtection;
    }
    
    public void setForkJoinCommonPoolProtection(final boolean forkJoinCommonPoolProtection) {
        this.forkJoinCommonPoolProtection = forkJoinCommonPoolProtection;
    }
    
    public String getClassesToInitialize() {
        return this.classesToInitialize;
    }
    
    public void setClassesToInitialize(final String classesToInitialize) {
        this.classesToInitialize = classesToInitialize;
    }
    
    @Override
    public void lifecycleEvent(final LifecycleEvent event) {
        if ("before_init".equals(event.getType())) {
            if (this.driverManagerProtection) {
                DriverManager.getDrivers();
            }
            final ClassLoader loader = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader());
                if (this.appContextProtection && !JreCompat.isJre8Available()) {
                    ImageIO.getCacheDirectory();
                }
                if (this.awtThreadProtection && !JreCompat.isJre9Available()) {
                    Toolkit.getDefaultToolkit();
                }
                if (this.gcDaemonProtection && !JreCompat.isJre9Available()) {
                    try {
                        final Class<?> clazz = Class.forName("sun.misc.GC");
                        final Method method = clazz.getDeclaredMethod("requestLatency", Long.TYPE);
                        method.invoke(null, 9223372036854775806L);
                    }
                    catch (final ClassNotFoundException e) {
                        if (JreVendor.IS_ORACLE_JVM) {
                            JreMemoryLeakPreventionListener.log.error((Object)JreMemoryLeakPreventionListener.sm.getString("jreLeakListener.gcDaemonFail"), (Throwable)e);
                        }
                        else {
                            JreMemoryLeakPreventionListener.log.debug((Object)JreMemoryLeakPreventionListener.sm.getString("jreLeakListener.gcDaemonFail"), (Throwable)e);
                        }
                    }
                    catch (final SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException e2) {
                        JreMemoryLeakPreventionListener.log.error((Object)JreMemoryLeakPreventionListener.sm.getString("jreLeakListener.gcDaemonFail"), (Throwable)e2);
                    }
                    catch (final InvocationTargetException e3) {
                        ExceptionUtils.handleThrowable(e3.getCause());
                        JreMemoryLeakPreventionListener.log.error((Object)JreMemoryLeakPreventionListener.sm.getString("jreLeakListener.gcDaemonFail"), (Throwable)e3);
                    }
                }
                if (this.securityPolicyProtection && !JreCompat.isJre8Available()) {
                    try {
                        final Class<?> policyClass = Class.forName("javax.security.auth.Policy");
                        final Method method = policyClass.getMethod("getPolicy", (Class<?>[])new Class[0]);
                        method.invoke(null, new Object[0]);
                    }
                    catch (final ClassNotFoundException ex) {}
                    catch (final SecurityException ex2) {}
                    catch (final NoSuchMethodException e4) {
                        JreMemoryLeakPreventionListener.log.warn((Object)JreMemoryLeakPreventionListener.sm.getString("jreLeakListener.authPolicyFail"), (Throwable)e4);
                    }
                    catch (final IllegalArgumentException e5) {
                        JreMemoryLeakPreventionListener.log.warn((Object)JreMemoryLeakPreventionListener.sm.getString("jreLeakListener.authPolicyFail"), (Throwable)e5);
                    }
                    catch (final IllegalAccessException e6) {
                        JreMemoryLeakPreventionListener.log.warn((Object)JreMemoryLeakPreventionListener.sm.getString("jreLeakListener.authPolicyFail"), (Throwable)e6);
                    }
                    catch (final InvocationTargetException e3) {
                        ExceptionUtils.handleThrowable(e3.getCause());
                        JreMemoryLeakPreventionListener.log.warn((Object)JreMemoryLeakPreventionListener.sm.getString("jreLeakListener.authPolicyFail"), (Throwable)e3);
                    }
                }
                if (this.securityLoginConfigurationProtection && !JreCompat.isJre8Available()) {
                    try {
                        Class.forName("javax.security.auth.login.Configuration", true, ClassLoader.getSystemClassLoader());
                    }
                    catch (final ClassNotFoundException ex3) {}
                }
                if (this.tokenPollerProtection && !JreCompat.isJre9Available()) {
                    Security.getProviders();
                }
                if (this.urlCacheProtection) {
                    try {
                        JreCompat.getInstance().disableCachingForJarUrlConnections();
                    }
                    catch (final IOException e7) {
                        JreMemoryLeakPreventionListener.log.error((Object)JreMemoryLeakPreventionListener.sm.getString("jreLeakListener.jarUrlConnCacheFail"), (Throwable)e7);
                    }
                }
                if (this.xmlParsingProtection && !JreCompat.isJre9Available()) {
                    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    try {
                        final DocumentBuilder documentBuilder = factory.newDocumentBuilder();
                        final Document document = documentBuilder.newDocument();
                        document.createElement("dummy");
                        final DOMImplementationLS implementation = (DOMImplementationLS)document.getImplementation();
                        implementation.createLSSerializer().writeToString(document);
                        document.normalize();
                    }
                    catch (final ParserConfigurationException e8) {
                        JreMemoryLeakPreventionListener.log.error((Object)JreMemoryLeakPreventionListener.sm.getString("jreLeakListener.xmlParseFail"), (Throwable)e8);
                    }
                }
                if (this.ldapPoolProtection && !JreCompat.isJre9Available()) {
                    try {
                        Class.forName("com.sun.jndi.ldap.LdapPoolManager");
                    }
                    catch (final ClassNotFoundException e) {
                        if (JreVendor.IS_ORACLE_JVM) {
                            JreMemoryLeakPreventionListener.log.error((Object)JreMemoryLeakPreventionListener.sm.getString("jreLeakListener.ldapPoolManagerFail"), (Throwable)e);
                        }
                        else {
                            JreMemoryLeakPreventionListener.log.debug((Object)JreMemoryLeakPreventionListener.sm.getString("jreLeakListener.ldapPoolManagerFail"), (Throwable)e);
                        }
                    }
                }
                if (this.forkJoinCommonPoolProtection && JreCompat.isJre8Available() && !JreCompat.isJre9Available() && System.getProperty("java.util.concurrent.ForkJoinPool.common.threadFactory") == null) {
                    System.setProperty("java.util.concurrent.ForkJoinPool.common.threadFactory", SafeForkJoinWorkerThreadFactory.class.getName());
                }
                if (this.classesToInitialize != null) {
                    final StringTokenizer strTok = new StringTokenizer(this.classesToInitialize, ", \r\n\t");
                    while (strTok.hasMoreTokens()) {
                        final String classNameToLoad = strTok.nextToken();
                        try {
                            Class.forName(classNameToLoad);
                        }
                        catch (final ClassNotFoundException e9) {
                            JreMemoryLeakPreventionListener.log.error((Object)JreMemoryLeakPreventionListener.sm.getString("jreLeakListener.classToInitializeFail", new Object[] { classNameToLoad }), (Throwable)e9);
                        }
                    }
                }
            }
            finally {
                Thread.currentThread().setContextClassLoader(loader);
            }
        }
    }
    
    static {
        log = LogFactory.getLog((Class)JreMemoryLeakPreventionListener.class);
        sm = StringManager.getManager((Class)JreMemoryLeakPreventionListener.class);
    }
}
