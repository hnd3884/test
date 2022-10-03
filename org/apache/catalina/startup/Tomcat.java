package org.apache.catalina.startup;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import java.util.Stack;
import javax.servlet.SingleThreadModel;
import org.apache.catalina.core.StandardWrapper;
import org.apache.catalina.Valve;
import org.apache.catalina.authenticator.NonLoginAuthenticator;
import org.apache.tomcat.util.descriptor.web.LoginConfig;
import org.apache.catalina.LifecycleEvent;
import java.util.jar.JarEntry;
import org.apache.tomcat.util.buf.UriUtil;
import java.util.jar.JarFile;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Properties;
import org.apache.catalina.core.NamingContextListener;
import java.lang.reflect.InvocationTargetException;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.ContainerBase;
import java.util.logging.Level;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.RealmBase;
import org.apache.catalina.Realm;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.core.StandardService;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.Engine;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import java.util.ArrayList;
import org.apache.catalina.LifecycleException;
import javax.servlet.Servlet;
import org.apache.catalina.Container;
import org.apache.catalina.Wrapper;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.URLConnection;
import org.apache.catalina.Host;
import org.apache.catalina.util.IOTools;
import java.io.FileOutputStream;
import java.io.File;
import org.apache.catalina.util.ContextName;
import java.net.URL;
import org.apache.catalina.Context;
import org.apache.tomcat.util.ExceptionUtils;
import java.util.HashMap;
import java.security.Principal;
import java.util.List;
import org.apache.catalina.Server;
import java.util.logging.Logger;
import java.util.Map;
import org.apache.tomcat.util.res.StringManager;

public class Tomcat
{
    private static final StringManager sm;
    private final Map<String, Logger> pinnedLoggers;
    protected Server server;
    protected int port;
    protected String hostname;
    protected String basedir;
    protected boolean defaultConnectorCreated;
    private final Map<String, String> userPass;
    private final Map<String, List<String>> userRoles;
    private final Map<String, Principal> userPrincipals;
    private boolean addDefaultWebXmlToWebapp;
    static final String[] silences;
    private boolean silent;
    
    public Tomcat() {
        this.pinnedLoggers = new HashMap<String, Logger>();
        this.port = 8080;
        this.hostname = "localhost";
        this.defaultConnectorCreated = false;
        this.userPass = new HashMap<String, String>();
        this.userRoles = new HashMap<String, List<String>>();
        this.userPrincipals = new HashMap<String, Principal>();
        this.addDefaultWebXmlToWebapp = true;
        this.silent = false;
        ExceptionUtils.preload();
    }
    
    public void setBaseDir(final String basedir) {
        this.basedir = basedir;
    }
    
    public void setPort(final int port) {
        this.port = port;
    }
    
    public void setHostname(final String s) {
        this.hostname = s;
    }
    
    public Context addWebapp(final String contextPath, final String docBase) {
        return this.addWebapp(this.getHost(), contextPath, docBase);
    }
    
    public Context addWebapp(final String contextPath, final URL source) throws IOException {
        final ContextName cn = new ContextName(contextPath, null);
        final Host h = this.getHost();
        if (h.findChild(cn.getName()) != null) {
            throw new IllegalArgumentException(Tomcat.sm.getString("tomcat.addWebapp.conflictChild", new Object[] { source, contextPath, cn.getName() }));
        }
        final File targetWar = new File(h.getAppBaseFile(), cn.getBaseName() + ".war");
        final File targetDir = new File(h.getAppBaseFile(), cn.getBaseName());
        if (targetWar.exists()) {
            throw new IllegalArgumentException(Tomcat.sm.getString("tomcat.addWebapp.conflictFile", new Object[] { source, contextPath, targetWar.getAbsolutePath() }));
        }
        if (targetDir.exists()) {
            throw new IllegalArgumentException(Tomcat.sm.getString("tomcat.addWebapp.conflictFile", new Object[] { source, contextPath, targetDir.getAbsolutePath() }));
        }
        final URLConnection uConn = source.openConnection();
        try (final InputStream is = uConn.getInputStream();
             final OutputStream os = new FileOutputStream(targetWar)) {
            IOTools.flow(is, os);
        }
        return this.addWebapp(contextPath, targetWar.getAbsolutePath());
    }
    
    public Context addContext(final String contextPath, final String docBase) {
        return this.addContext(this.getHost(), contextPath, docBase);
    }
    
    public Wrapper addServlet(final String contextPath, final String servletName, final String servletClass) {
        final Container ctx = this.getHost().findChild(contextPath);
        return addServlet((Context)ctx, servletName, servletClass);
    }
    
    public static Wrapper addServlet(final Context ctx, final String servletName, final String servletClass) {
        final Wrapper sw = ctx.createWrapper();
        sw.setServletClass(servletClass);
        sw.setName(servletName);
        ctx.addChild(sw);
        return sw;
    }
    
    public Wrapper addServlet(final String contextPath, final String servletName, final Servlet servlet) {
        final Container ctx = this.getHost().findChild(contextPath);
        return addServlet((Context)ctx, servletName, servlet);
    }
    
    public static Wrapper addServlet(final Context ctx, final String servletName, final Servlet servlet) {
        final Wrapper sw = new ExistingStandardWrapper(servlet);
        sw.setName(servletName);
        ctx.addChild(sw);
        return sw;
    }
    
    public void init() throws LifecycleException {
        this.getServer();
        this.getConnector();
        this.server.init();
    }
    
    public void start() throws LifecycleException {
        this.getServer();
        this.getConnector();
        this.server.start();
    }
    
    public void stop() throws LifecycleException {
        this.getServer();
        this.server.stop();
    }
    
    public void destroy() throws LifecycleException {
        this.getServer();
        this.server.destroy();
    }
    
    public void addUser(final String user, final String pass) {
        this.userPass.put(user, pass);
    }
    
    public void addRole(final String user, final String role) {
        List<String> roles = this.userRoles.get(user);
        if (roles == null) {
            roles = new ArrayList<String>();
            this.userRoles.put(user, roles);
        }
        roles.add(role);
    }
    
    public Connector getConnector() {
        final Service service = this.getService();
        if (service.findConnectors().length > 0) {
            return service.findConnectors()[0];
        }
        if (this.defaultConnectorCreated) {
            return null;
        }
        final Connector connector = new Connector("HTTP/1.1");
        connector.setPort(this.port);
        service.addConnector(connector);
        this.defaultConnectorCreated = true;
        return connector;
    }
    
    public void setConnector(final Connector connector) {
        this.defaultConnectorCreated = true;
        final Service service = this.getService();
        boolean found = false;
        for (final Connector serviceConnector : service.findConnectors()) {
            if (connector == serviceConnector) {
                found = true;
                break;
            }
        }
        if (!found) {
            service.addConnector(connector);
        }
    }
    
    public Service getService() {
        return this.getServer().findServices()[0];
    }
    
    public void setHost(final Host host) {
        final Engine engine = this.getEngine();
        boolean found = false;
        for (final Container engineHost : engine.findChildren()) {
            if (engineHost == host) {
                found = true;
                break;
            }
        }
        if (!found) {
            engine.addChild(host);
        }
    }
    
    public Host getHost() {
        final Engine engine = this.getEngine();
        if (engine.findChildren().length > 0) {
            return (Host)engine.findChildren()[0];
        }
        final Host host = new StandardHost();
        host.setName(this.hostname);
        this.getEngine().addChild(host);
        return host;
    }
    
    public Engine getEngine() {
        final Service service = this.getServer().findServices()[0];
        if (service.getContainer() != null) {
            return service.getContainer();
        }
        final Engine engine = new StandardEngine();
        engine.setName("Tomcat");
        engine.setDefaultHost(this.hostname);
        engine.setRealm(this.createDefaultRealm());
        service.setContainer(engine);
        return engine;
    }
    
    public Server getServer() {
        if (this.server != null) {
            return this.server;
        }
        System.setProperty("catalina.useNaming", "false");
        this.server = new StandardServer();
        this.initBaseDir();
        this.server.setPort(-1);
        final Service service = new StandardService();
        service.setName("Tomcat");
        this.server.addService(service);
        return this.server;
    }
    
    public Context addContext(final Host host, final String contextPath, final String dir) {
        return this.addContext(host, contextPath, contextPath, dir);
    }
    
    public Context addContext(final Host host, final String contextPath, final String contextName, final String dir) {
        this.silence(host, contextName);
        final Context ctx = this.createContext(host, contextPath);
        ctx.setName(contextName);
        ctx.setPath(contextPath);
        ctx.setDocBase(dir);
        ctx.addLifecycleListener(new FixContextListener());
        if (host == null) {
            this.getHost().addChild(ctx);
        }
        else {
            host.addChild(ctx);
        }
        return ctx;
    }
    
    public Context addWebapp(final Host host, final String contextPath, final String docBase) {
        LifecycleListener listener = null;
        try {
            final Class<?> clazz = Class.forName(this.getHost().getConfigClass());
            listener = (LifecycleListener)clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
        }
        catch (final ReflectiveOperationException e) {
            throw new IllegalArgumentException(e);
        }
        return this.addWebapp(host, contextPath, docBase, listener);
    }
    
    @Deprecated
    public Context addWebapp(final Host host, final String contextPath, final String docBase, final ContextConfig config) {
        return this.addWebapp(host, contextPath, docBase, (LifecycleListener)config);
    }
    
    public Context addWebapp(final Host host, final String contextPath, final String docBase, final LifecycleListener config) {
        this.silence(host, contextPath);
        final Context ctx = this.createContext(host, contextPath);
        ctx.setPath(contextPath);
        ctx.setDocBase(docBase);
        if (this.addDefaultWebXmlToWebapp) {
            ctx.addLifecycleListener(this.getDefaultWebXmlListener());
        }
        ctx.setConfigFile(this.getWebappConfigFile(docBase, contextPath));
        ctx.addLifecycleListener(config);
        if (this.addDefaultWebXmlToWebapp && config instanceof ContextConfig) {
            ((ContextConfig)config).setDefaultWebXml(this.noDefaultWebXmlPath());
        }
        if (host == null) {
            this.getHost().addChild(ctx);
        }
        else {
            host.addChild(ctx);
        }
        return ctx;
    }
    
    public LifecycleListener getDefaultWebXmlListener() {
        return new DefaultWebXmlListener();
    }
    
    public String noDefaultWebXmlPath() {
        return "org/apache/catalina/startup/NO_DEFAULT_XML";
    }
    
    protected Realm createDefaultRealm() {
        return new RealmBase() {
            @Deprecated
            @Override
            protected String getName() {
                return "Simple";
            }
            
            @Override
            protected String getPassword(final String username) {
                return Tomcat.this.userPass.get(username);
            }
            
            @Override
            protected Principal getPrincipal(final String username) {
                Principal p = Tomcat.this.userPrincipals.get(username);
                if (p == null) {
                    final String pass = Tomcat.this.userPass.get(username);
                    if (pass != null) {
                        p = new GenericPrincipal(username, pass, Tomcat.this.userRoles.get(username));
                        Tomcat.this.userPrincipals.put(username, p);
                    }
                }
                return p;
            }
        };
    }
    
    protected void initBaseDir() {
        final String catalinaHome = System.getProperty("catalina.home");
        if (this.basedir == null) {
            this.basedir = System.getProperty("catalina.base");
        }
        if (this.basedir == null) {
            this.basedir = catalinaHome;
        }
        if (this.basedir == null) {
            this.basedir = System.getProperty("user.dir") + "/tomcat." + this.port;
        }
        File baseFile = new File(this.basedir);
        if (baseFile.exists()) {
            if (!baseFile.isDirectory()) {
                throw new IllegalArgumentException(Tomcat.sm.getString("tomcat.baseDirNotDir", new Object[] { baseFile }));
            }
        }
        else if (!baseFile.mkdirs()) {
            throw new IllegalStateException(Tomcat.sm.getString("tomcat.baseDirMakeFail", new Object[] { baseFile }));
        }
        try {
            baseFile = baseFile.getCanonicalFile();
        }
        catch (final IOException e) {
            baseFile = baseFile.getAbsoluteFile();
        }
        this.server.setCatalinaBase(baseFile);
        System.setProperty("catalina.base", baseFile.getPath());
        this.basedir = baseFile.getPath();
        if (catalinaHome == null) {
            this.server.setCatalinaHome(baseFile);
        }
        else {
            File homeFile = new File(catalinaHome);
            if (!homeFile.isDirectory() && !homeFile.mkdirs()) {
                throw new IllegalStateException(Tomcat.sm.getString("tomcat.homeDirMakeFail", new Object[] { homeFile }));
            }
            try {
                homeFile = homeFile.getCanonicalFile();
            }
            catch (final IOException e2) {
                homeFile = homeFile.getAbsoluteFile();
            }
            this.server.setCatalinaHome(homeFile);
        }
        System.setProperty("catalina.home", this.server.getCatalinaHome().getPath());
    }
    
    public void setSilent(final boolean silent) {
        this.silent = silent;
        for (final String s : Tomcat.silences) {
            final Logger logger = Logger.getLogger(s);
            this.pinnedLoggers.put(s, logger);
            if (silent) {
                logger.setLevel(Level.WARNING);
            }
            else {
                logger.setLevel(Level.INFO);
            }
        }
    }
    
    private void silence(final Host host, final String contextPath) {
        final String loggerName = this.getLoggerName(host, contextPath);
        final Logger logger = Logger.getLogger(loggerName);
        this.pinnedLoggers.put(loggerName, logger);
        if (this.silent) {
            logger.setLevel(Level.WARNING);
        }
        else {
            logger.setLevel(Level.INFO);
        }
    }
    
    public void setAddDefaultWebXmlToWebapp(final boolean addDefaultWebXmlToWebapp) {
        this.addDefaultWebXmlToWebapp = addDefaultWebXmlToWebapp;
    }
    
    private String getLoggerName(Host host, final String contextName) {
        if (host == null) {
            host = this.getHost();
        }
        final StringBuilder loggerName = new StringBuilder();
        loggerName.append(ContainerBase.class.getName());
        loggerName.append(".[");
        loggerName.append(host.getParent().getName());
        loggerName.append("].[");
        loggerName.append(host.getName());
        loggerName.append("].[");
        if (contextName == null || contextName.equals("")) {
            loggerName.append('/');
        }
        else if (contextName.startsWith("##")) {
            loggerName.append('/');
            loggerName.append(contextName);
        }
        loggerName.append(']');
        return loggerName.toString();
    }
    
    private Context createContext(Host host, final String url) {
        String contextClass = StandardContext.class.getName();
        if (host == null) {
            host = this.getHost();
        }
        if (host instanceof StandardHost) {
            contextClass = ((StandardHost)host).getContextClass();
        }
        try {
            return (Context)Class.forName(contextClass).getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
        }
        catch (final InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
            throw new IllegalArgumentException("Can't instantiate context-class " + contextClass + " for host " + host + " and url " + url, e);
        }
    }
    
    public void enableNaming() {
        this.getServer();
        this.server.addLifecycleListener(new NamingContextListener());
        System.setProperty("catalina.useNaming", "true");
        String value = "org.apache.naming";
        final String oldValue = System.getProperty("java.naming.factory.url.pkgs");
        if (oldValue != null) {
            if (oldValue.contains(value)) {
                value = oldValue;
            }
            else {
                value = value + ":" + oldValue;
            }
        }
        System.setProperty("java.naming.factory.url.pkgs", value);
        value = System.getProperty("java.naming.factory.initial");
        if (value == null) {
            System.setProperty("java.naming.factory.initial", "org.apache.naming.java.javaURLContextFactory");
        }
    }
    
    public void initWebappDefaults(final String contextPath) {
        final Container ctx = this.getHost().findChild(contextPath);
        initWebappDefaults((Context)ctx);
    }
    
    public static void initWebappDefaults(final Context ctx) {
        Wrapper servlet = addServlet(ctx, "default", "org.apache.catalina.servlets.DefaultServlet");
        servlet.setLoadOnStartup(1);
        servlet.setOverridable(true);
        servlet = addServlet(ctx, "jsp", "org.apache.jasper.servlet.JspServlet");
        servlet.addInitParameter("fork", "false");
        servlet.setLoadOnStartup(3);
        servlet.setOverridable(true);
        ctx.addServletMappingDecoded("/", "default");
        ctx.addServletMappingDecoded("*.jsp", "jsp");
        ctx.addServletMappingDecoded("*.jspx", "jsp");
        ctx.setSessionTimeout(30);
        addDefaultMimeTypeMappings(ctx);
        ctx.addWelcomeFile("index.html");
        ctx.addWelcomeFile("index.htm");
        ctx.addWelcomeFile("index.jsp");
    }
    
    public static void addDefaultMimeTypeMappings(final Context context) {
        final Properties defaultMimeMappings = new Properties();
        try (final InputStream is = Tomcat.class.getResourceAsStream("MimeTypeMappings.properties")) {
            defaultMimeMappings.load(is);
            for (final Map.Entry<Object, Object> entry : defaultMimeMappings.entrySet()) {
                context.addMimeMapping(entry.getKey(), entry.getValue());
            }
        }
        catch (final IOException e) {
            throw new IllegalStateException(Tomcat.sm.getString("tomcat.defaultMimeTypeMappingsFail"), e);
        }
    }
    
    protected URL getWebappConfigFile(final String path, final String contextName) {
        final File docBase = new File(path);
        if (docBase.isDirectory()) {
            return this.getWebappConfigFileFromDirectory(docBase, contextName);
        }
        return this.getWebappConfigFileFromWar(docBase, contextName);
    }
    
    private URL getWebappConfigFileFromDirectory(final File docBase, final String contextName) {
        URL result = null;
        final File webAppContextXml = new File(docBase, "META-INF/context.xml");
        if (webAppContextXml.exists()) {
            try {
                result = webAppContextXml.toURI().toURL();
            }
            catch (final MalformedURLException e) {
                Logger.getLogger(this.getLoggerName(this.getHost(), contextName)).log(Level.WARNING, "Unable to determine web application context.xml " + docBase, e);
            }
        }
        return result;
    }
    
    private URL getWebappConfigFileFromWar(final File docBase, final String contextName) {
        URL result = null;
        try (final JarFile jar = new JarFile(docBase)) {
            final JarEntry entry = jar.getJarEntry("META-INF/context.xml");
            if (entry != null) {
                result = UriUtil.buildJarUrl(docBase, "META-INF/context.xml");
            }
        }
        catch (final IOException e) {
            Logger.getLogger(this.getLoggerName(this.getHost(), contextName)).log(Level.WARNING, "Unable to determine web application context.xml " + docBase, e);
        }
        return result;
    }
    
    static {
        sm = StringManager.getManager((Class)Tomcat.class);
        silences = new String[] { "org.apache.coyote.http11.Http11NioProtocol", "org.apache.catalina.core.StandardService", "org.apache.catalina.core.StandardEngine", "org.apache.catalina.startup.ContextConfig", "org.apache.catalina.core.ApplicationContext", "org.apache.catalina.core.AprLifecycleListener" };
    }
    
    public static class FixContextListener implements LifecycleListener
    {
        @Override
        public void lifecycleEvent(final LifecycleEvent event) {
            try {
                final Context context = (Context)event.getLifecycle();
                if (event.getType().equals("configure_start")) {
                    context.setConfigured(true);
                    WebAnnotationSet.loadApplicationAnnotations(context);
                    if (context.getLoginConfig() == null) {
                        context.setLoginConfig(new LoginConfig("NONE", (String)null, (String)null, (String)null));
                        context.getPipeline().addValve(new NonLoginAuthenticator());
                    }
                }
            }
            catch (final ClassCastException ex) {}
        }
    }
    
    public static class DefaultWebXmlListener implements LifecycleListener
    {
        @Override
        public void lifecycleEvent(final LifecycleEvent event) {
            if ("before_start".equals(event.getType())) {
                Tomcat.initWebappDefaults((Context)event.getLifecycle());
            }
        }
    }
    
    public static class ExistingStandardWrapper extends StandardWrapper
    {
        private final Servlet existing;
        
        public ExistingStandardWrapper(final Servlet existing) {
            this.existing = existing;
            if (existing instanceof SingleThreadModel) {
                this.singleThreadModel = true;
                this.instancePool = new Stack<Servlet>();
            }
            this.asyncSupported = hasAsync(existing);
        }
        
        private static boolean hasAsync(final Servlet existing) {
            boolean result = false;
            final Class<?> clazz = existing.getClass();
            final WebServlet ws = clazz.getAnnotation(WebServlet.class);
            if (ws != null) {
                result = ws.asyncSupported();
            }
            return result;
        }
        
        @Override
        public synchronized Servlet loadServlet() throws ServletException {
            if (this.singleThreadModel) {
                Servlet instance;
                try {
                    instance = (Servlet)this.existing.getClass().getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
                }
                catch (final ReflectiveOperationException e) {
                    throw new ServletException((Throwable)e);
                }
                instance.init((ServletConfig)this.facade);
                return instance;
            }
            if (!this.instanceInitialized) {
                this.existing.init((ServletConfig)this.facade);
                this.instanceInitialized = true;
            }
            return this.existing;
        }
        
        @Override
        public long getAvailable() {
            return 0L;
        }
        
        @Override
        public boolean isUnavailable() {
            return false;
        }
        
        @Override
        public Servlet getServlet() {
            return this.existing;
        }
        
        @Override
        public String getServletClass() {
            return this.existing.getClass().getName();
        }
    }
}
