package org.apache.commons.logging.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Vector;
import java.net.URL;
import org.apache.commons.logging.LogConfigurationException;
import org.apache.commons.logging.Log;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.util.Hashtable;
import org.apache.commons.logging.LogFactory;

public class LogFactoryImpl extends LogFactory
{
    private static final String LOGGING_IMPL_LOG4J_LOGGER = "org.apache.commons.logging.impl.Log4JLogger";
    private static final String LOGGING_IMPL_JDK14_LOGGER = "org.apache.commons.logging.impl.Jdk14Logger";
    private static final String LOGGING_IMPL_LUMBERJACK_LOGGER = "org.apache.commons.logging.impl.Jdk13LumberjackLogger";
    private static final String LOGGING_IMPL_SIMPLE_LOGGER = "org.apache.commons.logging.impl.SimpleLog";
    private static final String PKG_IMPL = "org.apache.commons.logging.impl.";
    private static final int PKG_LEN;
    public static final String LOG_PROPERTY = "org.apache.commons.logging.Log";
    protected static final String LOG_PROPERTY_OLD = "org.apache.commons.logging.log";
    public static final String ALLOW_FLAWED_CONTEXT_PROPERTY = "org.apache.commons.logging.Log.allowFlawedContext";
    public static final String ALLOW_FLAWED_DISCOVERY_PROPERTY = "org.apache.commons.logging.Log.allowFlawedDiscovery";
    public static final String ALLOW_FLAWED_HIERARCHY_PROPERTY = "org.apache.commons.logging.Log.allowFlawedHierarchy";
    private static final String[] classesToDiscover;
    private boolean useTCCL;
    private String diagnosticPrefix;
    protected Hashtable attributes;
    protected Hashtable instances;
    private String logClassName;
    protected Constructor logConstructor;
    protected Class[] logConstructorSignature;
    protected Method logMethod;
    protected Class[] logMethodSignature;
    private boolean allowFlawedContext;
    private boolean allowFlawedDiscovery;
    private boolean allowFlawedHierarchy;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$org$apache$commons$logging$LogFactory;
    static /* synthetic */ Class class$org$apache$commons$logging$impl$LogFactoryImpl;
    static /* synthetic */ Class class$org$apache$commons$logging$Log;
    
    static {
        PKG_LEN = "org.apache.commons.logging.impl.".length();
        classesToDiscover = new String[] { "org.apache.commons.logging.impl.Log4JLogger", "org.apache.commons.logging.impl.Jdk14Logger", "org.apache.commons.logging.impl.Jdk13LumberjackLogger", "org.apache.commons.logging.impl.SimpleLog" };
    }
    
    public LogFactoryImpl() {
        this.useTCCL = true;
        this.attributes = new Hashtable();
        this.instances = new Hashtable();
        this.logConstructor = null;
        this.logConstructorSignature = new Class[] { (LogFactoryImpl.class$java$lang$String != null) ? LogFactoryImpl.class$java$lang$String : (LogFactoryImpl.class$java$lang$String = class$("java.lang.String")) };
        this.logMethod = null;
        this.logMethodSignature = new Class[] { (LogFactoryImpl.class$org$apache$commons$logging$LogFactory != null) ? LogFactoryImpl.class$org$apache$commons$logging$LogFactory : (LogFactoryImpl.class$org$apache$commons$logging$LogFactory = class$("org.apache.commons.logging.LogFactory")) };
        this.initDiagnostics();
        if (isDiagnosticsEnabled()) {
            this.logDiagnostic("Instance created.");
        }
    }
    
    static /* synthetic */ Class class$(final String class$) {
        try {
            return Class.forName(class$);
        }
        catch (final ClassNotFoundException forName) {
            throw new NoClassDefFoundError(forName.getMessage());
        }
    }
    
    private Log createLogFromClass(final String logAdapterClassName, final String logCategory, final boolean affectState) throws LogConfigurationException {
        if (isDiagnosticsEnabled()) {
            this.logDiagnostic("Attempting to instantiate '" + logAdapterClassName + "'");
        }
        final Object[] params = { logCategory };
        Log logAdapter = null;
        Constructor constructor = null;
        Class logAdapterClass = null;
        ClassLoader currentCL = this.getBaseClassLoader();
        while (true) {
            this.logDiagnostic("Trying to load '" + logAdapterClassName + "' from classloader " + LogFactory.objectId(currentCL));
            try {
                if (isDiagnosticsEnabled()) {
                    final String resourceName = String.valueOf(logAdapterClassName.replace('.', '/')) + ".class";
                    URL url;
                    if (currentCL != null) {
                        url = currentCL.getResource(resourceName);
                    }
                    else {
                        url = ClassLoader.getSystemResource(String.valueOf(resourceName) + ".class");
                    }
                    if (url == null) {
                        this.logDiagnostic("Class '" + logAdapterClassName + "' [" + resourceName + "] cannot be found.");
                    }
                    else {
                        this.logDiagnostic("Class '" + logAdapterClassName + "' was found at '" + url + "'");
                    }
                }
                Class c = null;
                try {
                    c = Class.forName(logAdapterClassName, true, currentCL);
                }
                catch (final ClassNotFoundException originalClassNotFoundException) {
                    String msg = String.valueOf(originalClassNotFoundException.getMessage());
                    this.logDiagnostic("The log adapter '" + logAdapterClassName + "' is not available via classloader " + LogFactory.objectId(currentCL) + ": " + msg.trim());
                    try {
                        c = Class.forName(logAdapterClassName);
                    }
                    catch (final ClassNotFoundException secondaryClassNotFoundException) {
                        msg = String.valueOf(secondaryClassNotFoundException.getMessage());
                        this.logDiagnostic("The log adapter '" + logAdapterClassName + "' is not available via the LogFactoryImpl class classloader: " + msg.trim());
                    }
                }
                constructor = c.getConstructor((Class[])this.logConstructorSignature);
                final Object o = constructor.newInstance(params);
                if (o instanceof Log) {
                    logAdapterClass = c;
                    logAdapter = (Log)o;
                    break;
                }
                this.handleFlawedHierarchy(currentCL, c);
            }
            catch (final NoClassDefFoundError e) {
                final String msg2 = String.valueOf(e.getMessage());
                this.logDiagnostic("The log adapter '" + logAdapterClassName + "' is missing dependencies when loaded via classloader " + LogFactory.objectId(currentCL) + ": " + msg2.trim());
                break;
            }
            catch (final ExceptionInInitializerError e2) {
                final String msg2 = String.valueOf(e2.getMessage());
                this.logDiagnostic("The log adapter '" + logAdapterClassName + "' is unable to initialize itself when loaded via classloader " + LogFactory.objectId(currentCL) + ": " + msg2.trim());
                break;
            }
            catch (final LogConfigurationException e3) {
                throw e3;
            }
            catch (final Throwable t) {
                this.handleFlawedDiscovery(logAdapterClassName, currentCL, t);
            }
            if (currentCL == null) {
                break;
            }
            currentCL = currentCL.getParent();
        }
        if (logAdapter != null && affectState) {
            this.logClassName = logAdapterClassName;
            this.logConstructor = constructor;
            try {
                this.logMethod = logAdapterClass.getMethod("setLogFactory", (Class[])this.logMethodSignature);
                this.logDiagnostic("Found method setLogFactory(LogFactory) in '" + logAdapterClassName + "'");
            }
            catch (final Throwable t2) {
                this.logMethod = null;
                this.logDiagnostic("[INFO] '" + logAdapterClassName + "' from classloader " + LogFactory.objectId(currentCL) + " does not declare optional method " + "setLogFactory(LogFactory)");
            }
            this.logDiagnostic("Log adapter '" + logAdapterClassName + "' from classloader " + LogFactory.objectId(logAdapterClass.getClassLoader()) + " has been selected for use.");
        }
        return logAdapter;
    }
    
    private Log discoverLogImplementation(final String logCategory) throws LogConfigurationException {
        if (isDiagnosticsEnabled()) {
            this.logDiagnostic("Discovering a Log implementation...");
        }
        this.initConfiguration();
        Log result = null;
        final String specifiedLogClassName = this.findUserSpecifiedLogClassName();
        if (specifiedLogClassName != null) {
            if (isDiagnosticsEnabled()) {
                this.logDiagnostic("Attempting to load user-specified log class '" + specifiedLogClassName + "'...");
            }
            result = this.createLogFromClass(specifiedLogClassName, logCategory, true);
            if (result == null) {
                final StringBuffer messageBuffer = new StringBuffer("User-specified log class '");
                messageBuffer.append(specifiedLogClassName);
                messageBuffer.append("' cannot be found or is not useable.");
                if (specifiedLogClassName != null) {
                    this.informUponSimilarName(messageBuffer, specifiedLogClassName, "org.apache.commons.logging.impl.Log4JLogger");
                    this.informUponSimilarName(messageBuffer, specifiedLogClassName, "org.apache.commons.logging.impl.Jdk14Logger");
                    this.informUponSimilarName(messageBuffer, specifiedLogClassName, "org.apache.commons.logging.impl.Jdk13LumberjackLogger");
                    this.informUponSimilarName(messageBuffer, specifiedLogClassName, "org.apache.commons.logging.impl.SimpleLog");
                }
                throw new LogConfigurationException(messageBuffer.toString());
            }
            return result;
        }
        else {
            if (isDiagnosticsEnabled()) {
                this.logDiagnostic("No user-specified Log implementation; performing discovery using the standard supported logging implementations...");
            }
            for (int i = 0; i < LogFactoryImpl.classesToDiscover.length && result == null; result = this.createLogFromClass(LogFactoryImpl.classesToDiscover[i], logCategory, true), ++i) {}
            if (result == null) {
                throw new LogConfigurationException("No suitable Log implementation");
            }
            return result;
        }
    }
    
    private String findUserSpecifiedLogClassName() {
        if (isDiagnosticsEnabled()) {
            this.logDiagnostic("Trying to get log class from attribute 'org.apache.commons.logging.Log'");
        }
        String specifiedClass = (String)this.getAttribute("org.apache.commons.logging.Log");
        if (specifiedClass == null) {
            if (isDiagnosticsEnabled()) {
                this.logDiagnostic("Trying to get log class from attribute 'org.apache.commons.logging.log'");
            }
            specifiedClass = (String)this.getAttribute("org.apache.commons.logging.log");
        }
        if (specifiedClass == null) {
            if (isDiagnosticsEnabled()) {
                this.logDiagnostic("Trying to get log class from system property 'org.apache.commons.logging.Log'");
            }
            try {
                specifiedClass = System.getProperty("org.apache.commons.logging.Log");
            }
            catch (final SecurityException e) {
                if (isDiagnosticsEnabled()) {
                    this.logDiagnostic("No access allowed to system property 'org.apache.commons.logging.Log' - " + e.getMessage());
                }
            }
        }
        if (specifiedClass == null) {
            if (isDiagnosticsEnabled()) {
                this.logDiagnostic("Trying to get log class from system property 'org.apache.commons.logging.log'");
            }
            try {
                specifiedClass = System.getProperty("org.apache.commons.logging.log");
            }
            catch (final SecurityException e) {
                if (isDiagnosticsEnabled()) {
                    this.logDiagnostic("No access allowed to system property 'org.apache.commons.logging.log' - " + e.getMessage());
                }
            }
        }
        if (specifiedClass != null) {
            specifiedClass = specifiedClass.trim();
        }
        return specifiedClass;
    }
    
    public Object getAttribute(final String name) {
        return this.attributes.get(name);
    }
    
    public String[] getAttributeNames() {
        final Vector names = new Vector();
        final Enumeration keys = this.attributes.keys();
        while (keys.hasMoreElements()) {
            names.addElement(keys.nextElement());
        }
        final String[] results = new String[names.size()];
        for (int i = 0; i < results.length; ++i) {
            results[i] = names.elementAt(i);
        }
        return results;
    }
    
    private ClassLoader getBaseClassLoader() throws LogConfigurationException {
        final ClassLoader thisClassLoader = getClassLoader((LogFactoryImpl.class$org$apache$commons$logging$impl$LogFactoryImpl != null) ? LogFactoryImpl.class$org$apache$commons$logging$impl$LogFactoryImpl : (LogFactoryImpl.class$org$apache$commons$logging$impl$LogFactoryImpl = class$("org.apache.commons.logging.impl.LogFactoryImpl")));
        if (!this.useTCCL) {
            return thisClassLoader;
        }
        final ClassLoader contextClassLoader = getContextClassLoader();
        final ClassLoader baseClassLoader = this.getLowestClassLoader(contextClassLoader, thisClassLoader);
        if (baseClassLoader != null) {
            if (baseClassLoader != contextClassLoader) {
                if (!this.allowFlawedContext) {
                    throw new LogConfigurationException("Bad classloader hierarchy; LogFactoryImpl was loaded via a classloader that is not related to the current context classloader.");
                }
                if (isDiagnosticsEnabled()) {
                    this.logDiagnostic("Warning: the context classloader is an ancestor of the classloader that loaded LogFactoryImpl; it should be the same or a descendant. The application using commons-logging should ensure the context classloader is used correctly.");
                }
            }
            return baseClassLoader;
        }
        if (this.allowFlawedContext) {
            if (isDiagnosticsEnabled()) {
                this.logDiagnostic("[WARNING] the context classloader is not part of a parent-child relationship with the classloader that loaded LogFactoryImpl.");
            }
            return contextClassLoader;
        }
        throw new LogConfigurationException("Bad classloader hierarchy; LogFactoryImpl was loaded via a classloader that is not related to the current context classloader.");
    }
    
    private boolean getBooleanConfiguration(final String key, final boolean dflt) {
        final String val = this.getConfigurationValue(key);
        if (val == null) {
            return dflt;
        }
        return Boolean.valueOf(val);
    }
    
    protected static ClassLoader getClassLoader(final Class clazz) {
        return LogFactory.getClassLoader(clazz);
    }
    
    private String getConfigurationValue(final String property) {
        if (isDiagnosticsEnabled()) {
            this.logDiagnostic("[ENV] Trying to get configuration for item " + property);
        }
        final Object valueObj = this.getAttribute(property);
        if (valueObj != null) {
            if (isDiagnosticsEnabled()) {
                this.logDiagnostic("[ENV] Found LogFactory attribute [" + valueObj + "] for " + property);
            }
            return valueObj.toString();
        }
        if (isDiagnosticsEnabled()) {
            this.logDiagnostic("[ENV] No LogFactory attribute found for " + property);
        }
        try {
            final String value = System.getProperty(property);
            if (value != null) {
                if (isDiagnosticsEnabled()) {
                    this.logDiagnostic("[ENV] Found system property [" + value + "] for " + property);
                }
                return value;
            }
            if (isDiagnosticsEnabled()) {
                this.logDiagnostic("[ENV] No system property found for property " + property);
            }
        }
        catch (final SecurityException ex) {
            if (isDiagnosticsEnabled()) {
                this.logDiagnostic("[ENV] Security prevented reading system property " + property);
            }
        }
        if (isDiagnosticsEnabled()) {
            this.logDiagnostic("[ENV] No configuration defined for item " + property);
        }
        return null;
    }
    
    protected static ClassLoader getContextClassLoader() throws LogConfigurationException {
        return LogFactory.getContextClassLoader();
    }
    
    public Log getInstance(final Class clazz) throws LogConfigurationException {
        return this.getInstance(clazz.getName());
    }
    
    public Log getInstance(final String name) throws LogConfigurationException {
        Log instance = this.instances.get(name);
        if (instance == null) {
            instance = this.newInstance(name);
            this.instances.put(name, instance);
        }
        return instance;
    }
    
    protected String getLogClassName() {
        if (this.logClassName == null) {
            this.discoverLogImplementation(this.getClass().getName());
        }
        return this.logClassName;
    }
    
    protected Constructor getLogConstructor() throws LogConfigurationException {
        if (this.logConstructor == null) {
            this.discoverLogImplementation(this.getClass().getName());
        }
        return this.logConstructor;
    }
    
    private ClassLoader getLowestClassLoader(final ClassLoader c1, final ClassLoader c2) {
        if (c1 == null) {
            return c2;
        }
        if (c2 == null) {
            return c1;
        }
        for (ClassLoader current = c1; current != null; current = current.getParent()) {
            if (current == c2) {
                return c1;
            }
        }
        for (ClassLoader current = c2; current != null; current = current.getParent()) {
            if (current == c1) {
                return c2;
            }
        }
        return null;
    }
    
    private void handleFlawedDiscovery(final String logAdapterClassName, final ClassLoader classLoader, final Throwable discoveryFlaw) {
        if (isDiagnosticsEnabled()) {
            this.logDiagnostic("Could not instantiate Log '" + logAdapterClassName + "' -- " + discoveryFlaw.getClass().getName() + ": " + discoveryFlaw.getLocalizedMessage());
        }
        if (!this.allowFlawedDiscovery) {
            throw new LogConfigurationException(discoveryFlaw);
        }
    }
    
    private void handleFlawedHierarchy(final ClassLoader badClassLoader, final Class badClass) throws LogConfigurationException {
        boolean implementsLog = false;
        final String logInterfaceName = ((LogFactoryImpl.class$org$apache$commons$logging$Log != null) ? LogFactoryImpl.class$org$apache$commons$logging$Log : (LogFactoryImpl.class$org$apache$commons$logging$Log = class$("org.apache.commons.logging.Log"))).getName();
        final Class[] interfaces = badClass.getInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            if (logInterfaceName.equals(interfaces[i].getName())) {
                implementsLog = true;
                break;
            }
        }
        if (implementsLog) {
            if (isDiagnosticsEnabled()) {
                try {
                    final ClassLoader logInterfaceClassLoader = getClassLoader((LogFactoryImpl.class$org$apache$commons$logging$Log != null) ? LogFactoryImpl.class$org$apache$commons$logging$Log : (LogFactoryImpl.class$org$apache$commons$logging$Log = class$("org.apache.commons.logging.Log")));
                    this.logDiagnostic("Class '" + badClass.getName() + "' was found in classloader " + LogFactory.objectId(badClassLoader) + ". It is bound to a Log interface which is not" + " the one loaded from classloader " + LogFactory.objectId(logInterfaceClassLoader));
                }
                catch (final Throwable t) {
                    this.logDiagnostic("Error while trying to output diagnostics about bad class '" + badClass + "'");
                }
            }
            if (!this.allowFlawedHierarchy) {
                final StringBuffer msg = new StringBuffer();
                msg.append("Terminating logging for this context ");
                msg.append("due to bad log hierarchy. ");
                msg.append("You have more than one version of '");
                msg.append(((LogFactoryImpl.class$org$apache$commons$logging$Log != null) ? LogFactoryImpl.class$org$apache$commons$logging$Log : (LogFactoryImpl.class$org$apache$commons$logging$Log = class$("org.apache.commons.logging.Log"))).getName());
                msg.append("' visible.");
                if (isDiagnosticsEnabled()) {
                    this.logDiagnostic(msg.toString());
                }
                throw new LogConfigurationException(msg.toString());
            }
            if (isDiagnosticsEnabled()) {
                final StringBuffer msg = new StringBuffer();
                msg.append("Warning: bad log hierarchy. ");
                msg.append("You have more than one version of '");
                msg.append(((LogFactoryImpl.class$org$apache$commons$logging$Log != null) ? LogFactoryImpl.class$org$apache$commons$logging$Log : (LogFactoryImpl.class$org$apache$commons$logging$Log = class$("org.apache.commons.logging.Log"))).getName());
                msg.append("' visible.");
                this.logDiagnostic(msg.toString());
            }
        }
        else {
            if (!this.allowFlawedDiscovery) {
                final StringBuffer msg = new StringBuffer();
                msg.append("Terminating logging for this context. ");
                msg.append("Log class '");
                msg.append(badClass.getName());
                msg.append("' does not implement the Log interface.");
                if (isDiagnosticsEnabled()) {
                    this.logDiagnostic(msg.toString());
                }
                throw new LogConfigurationException(msg.toString());
            }
            if (isDiagnosticsEnabled()) {
                final StringBuffer msg = new StringBuffer();
                msg.append("[WARNING] Log class '");
                msg.append(badClass.getName());
                msg.append("' does not implement the Log interface.");
                this.logDiagnostic(msg.toString());
            }
        }
    }
    
    private void informUponSimilarName(final StringBuffer messageBuffer, final String name, final String candidate) {
        if (name.equals(candidate)) {
            return;
        }
        if (name.regionMatches(true, 0, candidate, 0, LogFactoryImpl.PKG_LEN + 5)) {
            messageBuffer.append(" Did you mean '");
            messageBuffer.append(candidate);
            messageBuffer.append("'?");
        }
    }
    
    private void initConfiguration() {
        this.allowFlawedContext = this.getBooleanConfiguration("org.apache.commons.logging.Log.allowFlawedContext", true);
        this.allowFlawedDiscovery = this.getBooleanConfiguration("org.apache.commons.logging.Log.allowFlawedDiscovery", true);
        this.allowFlawedHierarchy = this.getBooleanConfiguration("org.apache.commons.logging.Log.allowFlawedHierarchy", true);
    }
    
    private void initDiagnostics() {
        final Class clazz = this.getClass();
        final ClassLoader classLoader = getClassLoader(clazz);
        String classLoaderName;
        try {
            if (classLoader == null) {
                classLoaderName = "BOOTLOADER";
            }
            else {
                classLoaderName = LogFactory.objectId(classLoader);
            }
        }
        catch (final SecurityException ex) {
            classLoaderName = "UNKNOWN";
        }
        this.diagnosticPrefix = "[LogFactoryImpl@" + System.identityHashCode(this) + " from " + classLoaderName + "] ";
    }
    
    protected static boolean isDiagnosticsEnabled() {
        return LogFactory.isDiagnosticsEnabled();
    }
    
    protected boolean isJdk13LumberjackAvailable() {
        return this.isLogLibraryAvailable("Jdk13Lumberjack", "org.apache.commons.logging.impl.Jdk13LumberjackLogger");
    }
    
    protected boolean isJdk14Available() {
        return this.isLogLibraryAvailable("Jdk14", "org.apache.commons.logging.impl.Jdk14Logger");
    }
    
    protected boolean isLog4JAvailable() {
        return this.isLogLibraryAvailable("Log4J", "org.apache.commons.logging.impl.Log4JLogger");
    }
    
    private boolean isLogLibraryAvailable(final String name, final String classname) {
        if (isDiagnosticsEnabled()) {
            this.logDiagnostic("Checking for '" + name + "'.");
        }
        try {
            final Log log = this.createLogFromClass(classname, this.getClass().getName(), false);
            if (log == null) {
                if (isDiagnosticsEnabled()) {
                    this.logDiagnostic("Did not find '" + name + "'.");
                }
                return false;
            }
            if (isDiagnosticsEnabled()) {
                this.logDiagnostic("Found '" + name + "'.");
            }
            return true;
        }
        catch (final LogConfigurationException ex) {
            if (isDiagnosticsEnabled()) {
                this.logDiagnostic("Logging system '" + name + "' is available but not useable.");
            }
            return false;
        }
    }
    
    protected void logDiagnostic(final String msg) {
        if (isDiagnosticsEnabled()) {
            LogFactory.logRawDiagnostic(String.valueOf(this.diagnosticPrefix) + msg);
        }
    }
    
    protected Log newInstance(final String name) throws LogConfigurationException {
        Log instance = null;
        try {
            if (this.logConstructor == null) {
                instance = this.discoverLogImplementation(name);
            }
            else {
                final Object[] params = { name };
                instance = this.logConstructor.newInstance(params);
            }
            if (this.logMethod != null) {
                final Object[] params = { this };
                this.logMethod.invoke(instance, params);
            }
            return instance;
        }
        catch (final LogConfigurationException lce) {
            throw lce;
        }
        catch (final InvocationTargetException e) {
            final Throwable c = e.getTargetException();
            if (c != null) {
                throw new LogConfigurationException(c);
            }
            throw new LogConfigurationException(e);
        }
        catch (final Throwable t) {
            throw new LogConfigurationException(t);
        }
    }
    
    public void release() {
        this.logDiagnostic("Releasing all known loggers");
        this.instances.clear();
    }
    
    public void removeAttribute(final String name) {
        this.attributes.remove(name);
    }
    
    public void setAttribute(final String name, final Object value) {
        if (this.logConstructor != null) {
            this.logDiagnostic("setAttribute: call too late; configuration already performed.");
        }
        if (value == null) {
            this.attributes.remove(name);
        }
        else {
            this.attributes.put(name, value);
        }
        if (name.equals("use_tccl")) {
            this.useTCCL = Boolean.valueOf(value.toString());
        }
    }
}
