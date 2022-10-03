package org.apache.catalina.startup;

import org.apache.juli.logging.LogFactory;
import org.apache.catalina.security.SecurityConfig;
import java.io.PrintStream;
import org.apache.tomcat.util.log.SystemLogHandler;
import org.apache.catalina.LifecycleState;
import org.apache.juli.ClassLoaderLogManager;
import java.util.logging.LogManager;
import org.xml.sax.SAXParseException;
import java.io.OutputStream;
import org.apache.catalina.LifecycleException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.io.InputStream;
import org.xml.sax.InputSource;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import org.apache.tomcat.util.digester.RuleSet;
import org.apache.tomcat.util.digester.Rule;
import java.util.Map;
import org.apache.catalina.core.StandardContext;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import org.apache.tomcat.util.digester.Digester;
import java.io.File;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.juli.logging.Log;
import org.apache.catalina.Server;
import org.apache.tomcat.util.res.StringManager;

public class Catalina
{
    protected static final StringManager sm;
    protected boolean await;
    protected String configFile;
    protected ClassLoader parentClassLoader;
    protected Server server;
    protected boolean useShutdownHook;
    protected Thread shutdownHook;
    protected boolean useNaming;
    protected boolean loaded;
    private static final Log log;
    
    public Catalina() {
        this.await = false;
        this.configFile = "conf/server.xml";
        this.parentClassLoader = Catalina.class.getClassLoader();
        this.server = null;
        this.useShutdownHook = true;
        this.shutdownHook = null;
        this.useNaming = true;
        this.loaded = false;
        this.setSecurityProtection();
        ExceptionUtils.preload();
    }
    
    public void setConfigFile(final String file) {
        this.configFile = file;
    }
    
    public String getConfigFile() {
        return this.configFile;
    }
    
    public void setUseShutdownHook(final boolean useShutdownHook) {
        this.useShutdownHook = useShutdownHook;
    }
    
    public boolean getUseShutdownHook() {
        return this.useShutdownHook;
    }
    
    public void setParentClassLoader(final ClassLoader parentClassLoader) {
        this.parentClassLoader = parentClassLoader;
    }
    
    public ClassLoader getParentClassLoader() {
        if (this.parentClassLoader != null) {
            return this.parentClassLoader;
        }
        return ClassLoader.getSystemClassLoader();
    }
    
    public void setServer(final Server server) {
        this.server = server;
    }
    
    public Server getServer() {
        return this.server;
    }
    
    public boolean isUseNaming() {
        return this.useNaming;
    }
    
    public void setUseNaming(final boolean useNaming) {
        this.useNaming = useNaming;
    }
    
    public void setAwait(final boolean b) {
        this.await = b;
    }
    
    public boolean isAwait() {
        return this.await;
    }
    
    protected boolean arguments(final String[] args) {
        boolean isConfig = false;
        if (args.length < 1) {
            this.usage();
            return false;
        }
        for (final String arg : args) {
            if (isConfig) {
                this.configFile = arg;
                isConfig = false;
            }
            else if (arg.equals("-config")) {
                isConfig = true;
            }
            else if (arg.equals("-nonaming")) {
                this.setUseNaming(false);
            }
            else {
                if (arg.equals("-help")) {
                    this.usage();
                    return false;
                }
                if (!arg.equals("start")) {
                    if (!arg.equals("configtest")) {
                        if (!arg.equals("stop")) {
                            this.usage();
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    
    protected File configFile() {
        File file = new File(this.configFile);
        if (!file.isAbsolute()) {
            file = new File(Bootstrap.getCatalinaBase(), this.configFile);
        }
        return file;
    }
    
    protected Digester createStartDigester() {
        final long t1 = System.currentTimeMillis();
        final Digester digester = new Digester();
        digester.setValidating(false);
        digester.setRulesValidation(true);
        final Map<Class<?>, List<String>> fakeAttributes = new HashMap<Class<?>, List<String>>();
        final List<String> objectAttrs = new ArrayList<String>();
        objectAttrs.add("className");
        fakeAttributes.put(Object.class, objectAttrs);
        final List<String> contextAttrs = new ArrayList<String>();
        contextAttrs.add("source");
        fakeAttributes.put(StandardContext.class, contextAttrs);
        digester.setFakeAttributes((Map)fakeAttributes);
        digester.setUseContextClassLoader(true);
        digester.addObjectCreate("Server", "org.apache.catalina.core.StandardServer", "className");
        digester.addSetProperties("Server");
        digester.addSetNext("Server", "setServer", "org.apache.catalina.Server");
        digester.addObjectCreate("Server/GlobalNamingResources", "org.apache.catalina.deploy.NamingResourcesImpl");
        digester.addSetProperties("Server/GlobalNamingResources");
        digester.addSetNext("Server/GlobalNamingResources", "setGlobalNamingResources", "org.apache.catalina.deploy.NamingResourcesImpl");
        digester.addObjectCreate("Server/Listener", (String)null, "className");
        digester.addSetProperties("Server/Listener");
        digester.addSetNext("Server/Listener", "addLifecycleListener", "org.apache.catalina.LifecycleListener");
        digester.addObjectCreate("Server/Service", "org.apache.catalina.core.StandardService", "className");
        digester.addSetProperties("Server/Service");
        digester.addSetNext("Server/Service", "addService", "org.apache.catalina.Service");
        digester.addObjectCreate("Server/Service/Listener", (String)null, "className");
        digester.addSetProperties("Server/Service/Listener");
        digester.addSetNext("Server/Service/Listener", "addLifecycleListener", "org.apache.catalina.LifecycleListener");
        digester.addObjectCreate("Server/Service/Executor", "org.apache.catalina.core.StandardThreadExecutor", "className");
        digester.addSetProperties("Server/Service/Executor");
        digester.addSetNext("Server/Service/Executor", "addExecutor", "org.apache.catalina.Executor");
        digester.addRule("Server/Service/Connector", (Rule)new ConnectorCreateRule());
        digester.addRule("Server/Service/Connector", (Rule)new SetAllPropertiesRule(new String[] { "executor", "sslImplementationName" }));
        digester.addSetNext("Server/Service/Connector", "addConnector", "org.apache.catalina.connector.Connector");
        digester.addObjectCreate("Server/Service/Connector/SSLHostConfig", "org.apache.tomcat.util.net.SSLHostConfig");
        digester.addSetProperties("Server/Service/Connector/SSLHostConfig");
        digester.addSetNext("Server/Service/Connector/SSLHostConfig", "addSslHostConfig", "org.apache.tomcat.util.net.SSLHostConfig");
        digester.addRule("Server/Service/Connector/SSLHostConfig/Certificate", (Rule)new CertificateCreateRule());
        digester.addRule("Server/Service/Connector/SSLHostConfig/Certificate", (Rule)new SetAllPropertiesRule(new String[] { "type" }));
        digester.addSetNext("Server/Service/Connector/SSLHostConfig/Certificate", "addCertificate", "org.apache.tomcat.util.net.SSLHostConfigCertificate");
        digester.addObjectCreate("Server/Service/Connector/SSLHostConfig/OpenSSLConf", "org.apache.tomcat.util.net.openssl.OpenSSLConf");
        digester.addSetProperties("Server/Service/Connector/SSLHostConfig/OpenSSLConf");
        digester.addSetNext("Server/Service/Connector/SSLHostConfig/OpenSSLConf", "setOpenSslConf", "org.apache.tomcat.util.net.openssl.OpenSSLConf");
        digester.addObjectCreate("Server/Service/Connector/SSLHostConfig/OpenSSLConf/OpenSSLConfCmd", "org.apache.tomcat.util.net.openssl.OpenSSLConfCmd");
        digester.addSetProperties("Server/Service/Connector/SSLHostConfig/OpenSSLConf/OpenSSLConfCmd");
        digester.addSetNext("Server/Service/Connector/SSLHostConfig/OpenSSLConf/OpenSSLConfCmd", "addCmd", "org.apache.tomcat.util.net.openssl.OpenSSLConfCmd");
        digester.addObjectCreate("Server/Service/Connector/Listener", (String)null, "className");
        digester.addSetProperties("Server/Service/Connector/Listener");
        digester.addSetNext("Server/Service/Connector/Listener", "addLifecycleListener", "org.apache.catalina.LifecycleListener");
        digester.addObjectCreate("Server/Service/Connector/UpgradeProtocol", (String)null, "className");
        digester.addSetProperties("Server/Service/Connector/UpgradeProtocol");
        digester.addSetNext("Server/Service/Connector/UpgradeProtocol", "addUpgradeProtocol", "org.apache.coyote.UpgradeProtocol");
        digester.addRuleSet((RuleSet)new NamingRuleSet("Server/GlobalNamingResources/"));
        digester.addRuleSet((RuleSet)new EngineRuleSet("Server/Service/"));
        digester.addRuleSet((RuleSet)new HostRuleSet("Server/Service/Engine/"));
        digester.addRuleSet((RuleSet)new ContextRuleSet("Server/Service/Engine/Host/"));
        this.addClusterRuleSet(digester, "Server/Service/Engine/Host/Cluster/");
        digester.addRuleSet((RuleSet)new NamingRuleSet("Server/Service/Engine/Host/Context/"));
        digester.addRule("Server/Service/Engine", (Rule)new SetParentClassLoaderRule(this.parentClassLoader));
        this.addClusterRuleSet(digester, "Server/Service/Engine/Cluster/");
        final long t2 = System.currentTimeMillis();
        if (Catalina.log.isDebugEnabled()) {
            Catalina.log.debug((Object)("Digester for server.xml created " + (t2 - t1)));
        }
        return digester;
    }
    
    private void addClusterRuleSet(final Digester digester, final String prefix) {
        Class<?> clazz = null;
        Constructor<?> constructor = null;
        try {
            clazz = Class.forName("org.apache.catalina.ha.ClusterRuleSet");
            constructor = clazz.getConstructor(String.class);
            final RuleSet ruleSet = (RuleSet)constructor.newInstance(prefix);
            digester.addRuleSet(ruleSet);
        }
        catch (final Exception e) {
            if (Catalina.log.isDebugEnabled()) {
                Catalina.log.debug((Object)Catalina.sm.getString("catalina.noCluster", new Object[] { e.getClass().getName() + ": " + e.getMessage() }), (Throwable)e);
            }
            else if (Catalina.log.isInfoEnabled()) {
                Catalina.log.info((Object)Catalina.sm.getString("catalina.noCluster", new Object[] { e.getClass().getName() + ": " + e.getMessage() }));
            }
        }
    }
    
    protected Digester createStopDigester() {
        final Digester digester = new Digester();
        digester.setUseContextClassLoader(true);
        digester.addObjectCreate("Server", "org.apache.catalina.core.StandardServer", "className");
        digester.addSetProperties("Server");
        digester.addSetNext("Server", "setServer", "org.apache.catalina.Server");
        return digester;
    }
    
    public void stopServer() {
        this.stopServer(null);
    }
    
    public void stopServer(final String[] arguments) {
        if (arguments != null) {
            this.arguments(arguments);
        }
        Server s = this.getServer();
        if (s == null) {
            final Digester digester = this.createStopDigester();
            final File file = this.configFile();
            try (final FileInputStream fis = new FileInputStream(file)) {
                final InputSource is = new InputSource(file.toURI().toURL().toString());
                is.setByteStream(fis);
                digester.push((Object)this);
                digester.parse(is);
            }
            catch (final Exception e) {
                Catalina.log.error((Object)"Catalina.stop: ", (Throwable)e);
                System.exit(1);
            }
            s = this.getServer();
            if (s.getPort() > 0) {
                try (final Socket socket = new Socket(s.getAddress(), s.getPort());
                     final OutputStream stream = socket.getOutputStream()) {
                    final String shutdown = s.getShutdown();
                    for (int i = 0; i < shutdown.length(); ++i) {
                        stream.write(shutdown.charAt(i));
                    }
                    stream.flush();
                }
                catch (final ConnectException ce) {
                    Catalina.log.error((Object)Catalina.sm.getString("catalina.stopServer.connectException", new Object[] { s.getAddress(), String.valueOf(s.getPort()) }));
                    Catalina.log.error((Object)"Catalina.stop: ", (Throwable)ce);
                    System.exit(1);
                }
                catch (final IOException e2) {
                    Catalina.log.error((Object)"Catalina.stop: ", (Throwable)e2);
                    System.exit(1);
                }
            }
            else {
                Catalina.log.error((Object)Catalina.sm.getString("catalina.stopServer"));
                System.exit(1);
            }
            return;
        }
        try {
            s.stop();
            s.destroy();
        }
        catch (final LifecycleException e3) {
            Catalina.log.error((Object)"Catalina.stop: ", (Throwable)e3);
        }
    }
    
    public void load() {
        if (this.loaded) {
            return;
        }
        this.loaded = true;
        final long t1 = System.nanoTime();
        this.initDirs();
        this.initNaming();
        final Digester digester = this.createStartDigester();
        InputSource inputSource = null;
        InputStream inputStream = null;
        File file = null;
        try {
            try {
                file = this.configFile();
                inputStream = new FileInputStream(file);
                inputSource = new InputSource(file.toURI().toURL().toString());
            }
            catch (final Exception e) {
                if (Catalina.log.isDebugEnabled()) {
                    Catalina.log.debug((Object)Catalina.sm.getString("catalina.configFail", new Object[] { file }), (Throwable)e);
                }
            }
            if (inputStream == null) {
                try {
                    inputStream = this.getClass().getClassLoader().getResourceAsStream(this.getConfigFile());
                    inputSource = new InputSource(this.getClass().getClassLoader().getResource(this.getConfigFile()).toString());
                }
                catch (final Exception e) {
                    if (Catalina.log.isDebugEnabled()) {
                        Catalina.log.debug((Object)Catalina.sm.getString("catalina.configFail", new Object[] { this.getConfigFile() }), (Throwable)e);
                    }
                }
            }
            if (inputStream == null) {
                try {
                    inputStream = this.getClass().getClassLoader().getResourceAsStream("server-embed.xml");
                    inputSource = new InputSource(this.getClass().getClassLoader().getResource("server-embed.xml").toString());
                }
                catch (final Exception e) {
                    if (Catalina.log.isDebugEnabled()) {
                        Catalina.log.debug((Object)Catalina.sm.getString("catalina.configFail", new Object[] { "server-embed.xml" }), (Throwable)e);
                    }
                }
            }
            if (inputStream == null || inputSource == null) {
                if (file == null) {
                    Catalina.log.warn((Object)Catalina.sm.getString("catalina.configFail", new Object[] { this.getConfigFile() + "] or [server-embed.xml]" }));
                }
                else {
                    Catalina.log.warn((Object)Catalina.sm.getString("catalina.configFail", new Object[] { file.getAbsolutePath() }));
                    if (file.exists() && !file.canRead()) {
                        Catalina.log.warn((Object)"Permissions incorrect, read permission is not allowed on the file.");
                    }
                }
                return;
            }
            try {
                inputSource.setByteStream(inputStream);
                digester.push((Object)this);
                digester.parse(inputSource);
            }
            catch (final SAXParseException spe) {
                Catalina.log.warn((Object)("Catalina.start using " + this.getConfigFile() + ": " + spe.getMessage()));
                return;
            }
            catch (final Exception e) {
                Catalina.log.warn((Object)("Catalina.start using " + this.getConfigFile() + ": "), (Throwable)e);
                return;
            }
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (final IOException ex) {}
            }
        }
        this.getServer().setCatalina(this);
        this.getServer().setCatalinaHome(Bootstrap.getCatalinaHomeFile());
        this.getServer().setCatalinaBase(Bootstrap.getCatalinaBaseFile());
        this.initStreams();
        try {
            this.getServer().init();
        }
        catch (final LifecycleException e2) {
            if (Boolean.getBoolean("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE")) {
                throw new Error(e2);
            }
            Catalina.log.error((Object)"Catalina.start", (Throwable)e2);
        }
        final long t2 = System.nanoTime();
        if (Catalina.log.isInfoEnabled()) {
            Catalina.log.info((Object)("Initialization processed in " + (t2 - t1) / 1000000L + " ms"));
        }
    }
    
    public void load(final String[] args) {
        try {
            if (this.arguments(args)) {
                this.load();
            }
        }
        catch (final Exception e) {
            e.printStackTrace(System.out);
        }
    }
    
    public void start() {
        if (this.getServer() == null) {
            this.load();
        }
        if (this.getServer() == null) {
            Catalina.log.fatal((Object)"Cannot start server. Server instance is not configured.");
            return;
        }
        final long t1 = System.nanoTime();
        try {
            this.getServer().start();
        }
        catch (final LifecycleException e) {
            Catalina.log.fatal((Object)Catalina.sm.getString("catalina.serverStartFail"), (Throwable)e);
            try {
                this.getServer().destroy();
            }
            catch (final LifecycleException e2) {
                Catalina.log.debug((Object)"destroy() failed for failed Server ", (Throwable)e2);
            }
            return;
        }
        final long t2 = System.nanoTime();
        if (Catalina.log.isInfoEnabled()) {
            Catalina.log.info((Object)("Server startup in " + (t2 - t1) / 1000000L + " ms"));
        }
        if (this.useShutdownHook) {
            if (this.shutdownHook == null) {
                this.shutdownHook = new CatalinaShutdownHook();
            }
            Runtime.getRuntime().addShutdownHook(this.shutdownHook);
            final LogManager logManager = LogManager.getLogManager();
            if (logManager instanceof ClassLoaderLogManager) {
                ((ClassLoaderLogManager)logManager).setUseShutdownHook(false);
            }
        }
        if (this.await) {
            this.await();
            this.stop();
        }
    }
    
    public void stop() {
        try {
            if (this.useShutdownHook) {
                Runtime.getRuntime().removeShutdownHook(this.shutdownHook);
                final LogManager logManager = LogManager.getLogManager();
                if (logManager instanceof ClassLoaderLogManager) {
                    ((ClassLoaderLogManager)logManager).setUseShutdownHook(true);
                }
            }
        }
        catch (final Throwable t) {
            ExceptionUtils.handleThrowable(t);
        }
        try {
            final Server s = this.getServer();
            final LifecycleState state = s.getState();
            if (LifecycleState.STOPPING_PREP.compareTo(state) > 0 || LifecycleState.DESTROYED.compareTo(state) < 0) {
                s.stop();
                s.destroy();
            }
        }
        catch (final LifecycleException e) {
            Catalina.log.error((Object)"Catalina.stop", (Throwable)e);
        }
    }
    
    public void await() {
        this.getServer().await();
    }
    
    protected void usage() {
        System.out.println("usage: java org.apache.catalina.startup.Catalina [ -config {pathname} ] [ -nonaming ]  { -help | start | stop }");
    }
    
    @Deprecated
    protected void initDirs() {
    }
    
    protected void initStreams() {
        System.setOut((PrintStream)new SystemLogHandler(System.out));
        System.setErr((PrintStream)new SystemLogHandler(System.err));
    }
    
    protected void initNaming() {
        if (!this.useNaming) {
            Catalina.log.info((Object)Catalina.sm.getString("catalina.noNaming"));
            System.setProperty("catalina.useNaming", "false");
        }
        else {
            System.setProperty("catalina.useNaming", "true");
            String value = "org.apache.naming";
            final String oldValue = System.getProperty("java.naming.factory.url.pkgs");
            if (oldValue != null) {
                value = value + ":" + oldValue;
            }
            System.setProperty("java.naming.factory.url.pkgs", value);
            if (Catalina.log.isDebugEnabled()) {
                Catalina.log.debug((Object)("Setting naming prefix=" + value));
            }
            value = System.getProperty("java.naming.factory.initial");
            if (value == null) {
                System.setProperty("java.naming.factory.initial", "org.apache.naming.java.javaURLContextFactory");
            }
            else {
                Catalina.log.debug((Object)("INITIAL_CONTEXT_FACTORY already set " + value));
            }
        }
    }
    
    protected void setSecurityProtection() {
        final SecurityConfig securityConfig = SecurityConfig.newInstance();
        securityConfig.setPackageDefinition();
        securityConfig.setPackageAccess();
    }
    
    static {
        sm = StringManager.getManager("org.apache.catalina.startup");
        log = LogFactory.getLog((Class)Catalina.class);
    }
    
    protected class CatalinaShutdownHook extends Thread
    {
        @Override
        public void run() {
            try {
                if (Catalina.this.getServer() != null) {
                    Catalina.this.stop();
                }
            }
            catch (final Throwable ex) {
                ExceptionUtils.handleThrowable(ex);
                Catalina.log.error((Object)Catalina.sm.getString("catalina.shutdownHookFail"), ex);
            }
            finally {
                final LogManager logManager = LogManager.getLogManager();
                if (logManager instanceof ClassLoaderLogManager) {
                    ((ClassLoaderLogManager)logManager).shutdown();
                }
            }
        }
    }
}
