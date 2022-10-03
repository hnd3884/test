package org.apache.catalina.startup;

import java.util.HashMap;
import java.util.LinkedHashMap;
import org.apache.juli.logging.LogFactory;
import org.apache.catalina.Manager;
import java.util.SortedSet;
import org.apache.catalina.DistributedManager;
import java.util.Collection;
import java.util.TreeSet;
import org.apache.tomcat.util.modeler.Registry;
import java.nio.file.Files;
import java.nio.file.CopyOption;
import java.util.jar.JarEntry;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import org.apache.tomcat.util.buf.UriUtil;
import java.util.zip.ZipEntry;
import java.util.jar.JarFile;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.Container;
import java.io.InputStream;
import org.apache.catalina.Context;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import org.apache.tomcat.util.ExceptionUtils;
import java.util.Locale;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.List;
import java.util.regex.Pattern;
import java.io.IOException;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.LifecycleEvent;
import java.security.Permission;
import java.security.PermissionCollection;
import java.net.URL;
import java.net.MalformedURLException;
import org.apache.catalina.security.DeployXmlPermission;
import java.security.CodeSource;
import java.security.cert.Certificate;
import java.security.Policy;
import org.apache.catalina.Globals;
import org.apache.catalina.util.ContextName;
import java.io.File;
import java.util.HashSet;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.tomcat.util.digester.Digester;
import java.util.Set;
import java.util.ArrayList;
import java.util.Map;
import javax.management.ObjectName;
import org.apache.catalina.Host;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import org.apache.catalina.LifecycleListener;

public class HostConfig implements LifecycleListener
{
    private static final Log log;
    protected static final StringManager sm;
    protected static final long FILE_MODIFICATION_RESOLUTION_MS = 1000L;
    protected String contextClass;
    protected Host host;
    protected ObjectName oname;
    protected boolean deployXML;
    protected boolean copyXML;
    protected boolean unpackWARs;
    protected final Map<String, DeployedApplication> deployed;
    @Deprecated
    protected final ArrayList<String> serviced;
    private Set<String> servicedSet;
    protected Digester digester;
    private final Object digesterLock;
    protected final Set<String> invalidWars;
    
    public HostConfig() {
        this.contextClass = "org.apache.catalina.core.StandardContext";
        this.host = null;
        this.oname = null;
        this.deployXML = false;
        this.copyXML = false;
        this.unpackWARs = false;
        this.deployed = new ConcurrentHashMap<String, DeployedApplication>();
        this.serviced = new ArrayList<String>();
        this.servicedSet = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
        this.digester = createDigester(this.contextClass);
        this.digesterLock = new Object();
        this.invalidWars = new HashSet<String>();
    }
    
    public String getContextClass() {
        return this.contextClass;
    }
    
    public void setContextClass(final String contextClass) {
        final String oldContextClass = this.contextClass;
        this.contextClass = contextClass;
        if (!oldContextClass.equals(contextClass)) {
            synchronized (this.digesterLock) {
                this.digester = createDigester(this.getContextClass());
            }
        }
    }
    
    public boolean isDeployXML() {
        return this.deployXML;
    }
    
    public void setDeployXML(final boolean deployXML) {
        this.deployXML = deployXML;
    }
    
    private boolean isDeployThisXML(final File docBase, final ContextName cn) {
        boolean deployThisXML = this.isDeployXML();
        if (Globals.IS_SECURITY_ENABLED && !deployThisXML) {
            final Policy currentPolicy = Policy.getPolicy();
            if (currentPolicy != null) {
                try {
                    final URL contextRootUrl = docBase.toURI().toURL();
                    final CodeSource cs = new CodeSource(contextRootUrl, (Certificate[])null);
                    final PermissionCollection pc = currentPolicy.getPermissions(cs);
                    final Permission p = new DeployXmlPermission(cn.getBaseName());
                    if (pc.implies(p)) {
                        deployThisXML = true;
                    }
                }
                catch (final MalformedURLException e) {
                    HostConfig.log.warn((Object)"hostConfig.docBaseUrlInvalid", (Throwable)e);
                }
            }
        }
        return deployThisXML;
    }
    
    public boolean isCopyXML() {
        return this.copyXML;
    }
    
    public void setCopyXML(final boolean copyXML) {
        this.copyXML = copyXML;
    }
    
    public boolean isUnpackWARs() {
        return this.unpackWARs;
    }
    
    public void setUnpackWARs(final boolean unpackWARs) {
        this.unpackWARs = unpackWARs;
    }
    
    @Override
    public void lifecycleEvent(final LifecycleEvent event) {
        try {
            this.host = (Host)event.getLifecycle();
            if (this.host instanceof StandardHost) {
                this.setCopyXML(((StandardHost)this.host).isCopyXML());
                this.setDeployXML(((StandardHost)this.host).isDeployXML());
                this.setUnpackWARs(((StandardHost)this.host).isUnpackWARs());
                this.setContextClass(((StandardHost)this.host).getContextClass());
            }
        }
        catch (final ClassCastException e) {
            HostConfig.log.error((Object)HostConfig.sm.getString("hostConfig.cce", new Object[] { event.getLifecycle() }), (Throwable)e);
            return;
        }
        if (event.getType().equals("periodic")) {
            this.check();
        }
        else if (event.getType().equals("before_start")) {
            this.beforeStart();
        }
        else if (event.getType().equals("start")) {
            this.start();
        }
        else if (event.getType().equals("stop")) {
            this.stop();
        }
    }
    
    public boolean tryAddServiced(final String name) {
        if (this.servicedSet.add(name)) {
            synchronized (this) {
                this.serviced.add(name);
            }
            return true;
        }
        return false;
    }
    
    @Deprecated
    public void addServiced(final String name) {
        this.servicedSet.add(name);
        synchronized (this) {
            this.serviced.add(name);
        }
    }
    
    @Deprecated
    public boolean isServiced(final String name) {
        return this.servicedSet.contains(name);
    }
    
    public void removeServiced(final String name) {
        this.servicedSet.remove(name);
        synchronized (this) {
            this.serviced.remove(name);
        }
    }
    
    public long getDeploymentTime(final String name) {
        final DeployedApplication app = this.deployed.get(name);
        if (app == null) {
            return 0L;
        }
        return app.timestamp;
    }
    
    public boolean isDeployed(final String name) {
        return this.deployed.containsKey(name);
    }
    
    protected static Digester createDigester(final String contextClassName) {
        final Digester digester = new Digester();
        digester.setValidating(false);
        digester.addObjectCreate("Context", contextClassName, "className");
        digester.addSetProperties("Context");
        return digester;
    }
    
    protected File returnCanonicalPath(final String path) {
        File file = new File(path);
        if (!file.isAbsolute()) {
            file = new File(this.host.getCatalinaBase(), path);
        }
        try {
            return file.getCanonicalFile();
        }
        catch (final IOException e) {
            return file;
        }
    }
    
    public String getConfigBaseName() {
        return this.host.getConfigBaseFile().getAbsolutePath();
    }
    
    protected void deployApps() {
        final File appBase = this.host.getAppBaseFile();
        final File configBase = this.host.getConfigBaseFile();
        final String[] filteredAppPaths = this.filterAppPaths(appBase.list());
        this.deployDescriptors(configBase, configBase.list());
        this.deployWARs(appBase, filteredAppPaths);
        this.deployDirectories(appBase, filteredAppPaths);
    }
    
    protected String[] filterAppPaths(final String[] unfilteredAppPaths) {
        final Pattern filter = this.host.getDeployIgnorePattern();
        if (filter == null || unfilteredAppPaths == null) {
            return unfilteredAppPaths;
        }
        final List<String> filteredList = new ArrayList<String>();
        Matcher matcher = null;
        for (final String appPath : unfilteredAppPaths) {
            if (matcher == null) {
                matcher = filter.matcher(appPath);
            }
            else {
                matcher.reset(appPath);
            }
            if (matcher.matches()) {
                if (HostConfig.log.isDebugEnabled()) {
                    HostConfig.log.debug((Object)HostConfig.sm.getString("hostConfig.ignorePath", new Object[] { appPath }));
                }
            }
            else {
                filteredList.add(appPath);
            }
        }
        return filteredList.toArray(new String[0]);
    }
    
    protected void deployApps(final String name) {
        final File appBase = this.host.getAppBaseFile();
        final File configBase = this.host.getConfigBaseFile();
        final ContextName cn = new ContextName(name, false);
        final String baseName = cn.getBaseName();
        if (this.deploymentExists(cn.getName())) {
            return;
        }
        final File xml = new File(configBase, baseName + ".xml");
        if (xml.exists()) {
            this.deployDescriptor(cn, xml);
            return;
        }
        final File war = new File(appBase, baseName + ".war");
        if (war.exists()) {
            this.deployWAR(cn, war);
            return;
        }
        final File dir = new File(appBase, baseName);
        if (dir.exists()) {
            this.deployDirectory(cn, dir);
        }
    }
    
    protected void deployDescriptors(final File configBase, final String[] files) {
        if (files == null) {
            return;
        }
        final ExecutorService es = this.host.getStartStopExecutor();
        final List<Future<?>> results = new ArrayList<Future<?>>();
        for (final String file : files) {
            final File contextXml = new File(configBase, file);
            if (file.toLowerCase(Locale.ENGLISH).endsWith(".xml")) {
                final ContextName cn = new ContextName(file, true);
                if (this.tryAddServiced(cn.getName())) {
                    try {
                        if (this.deploymentExists(cn.getName())) {
                            this.removeServiced(cn.getName());
                        }
                        else {
                            results.add(es.submit(new DeployDescriptor(this, cn, contextXml)));
                        }
                    }
                    catch (final Throwable t) {
                        ExceptionUtils.handleThrowable(t);
                        this.removeServiced(cn.getName());
                        throw t;
                    }
                }
            }
        }
        for (final Future<?> result : results) {
            try {
                result.get();
            }
            catch (final Exception e) {
                HostConfig.log.error((Object)HostConfig.sm.getString("hostConfig.deployDescriptor.threaded.error"), (Throwable)e);
            }
        }
    }
    
    protected void deployDescriptor(final ContextName cn, final File contextXml) {
        final DeployedApplication deployedApp = new DeployedApplication(cn.getName(), true);
        long startTime = 0L;
        if (HostConfig.log.isInfoEnabled()) {
            startTime = System.currentTimeMillis();
            HostConfig.log.info((Object)HostConfig.sm.getString("hostConfig.deployDescriptor", new Object[] { contextXml.getAbsolutePath() }));
        }
        Context context = null;
        boolean isExternalWar = false;
        boolean isExternal = false;
        File expandedDocBase = null;
        try (final FileInputStream fis = new FileInputStream(contextXml)) {
            synchronized (this.digesterLock) {
                try {
                    context = (Context)this.digester.parse((InputStream)fis);
                }
                catch (final Exception e) {
                    HostConfig.log.error((Object)HostConfig.sm.getString("hostConfig.deployDescriptor.error", new Object[] { contextXml.getAbsolutePath() }), (Throwable)e);
                }
                finally {
                    this.digester.reset();
                    if (context == null) {
                        context = new FailedContext();
                    }
                }
            }
            final Class<?> clazz = Class.forName(this.host.getConfigClass());
            final LifecycleListener listener = (LifecycleListener)clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
            context.addLifecycleListener(listener);
            context.setConfigFile(contextXml.toURI().toURL());
            context.setName(cn.getName());
            context.setPath(cn.getPath());
            context.setWebappVersion(cn.getVersion());
            if (context.getDocBase() != null) {
                File docBase = new File(context.getDocBase());
                if (!docBase.isAbsolute()) {
                    docBase = new File(this.host.getAppBaseFile(), context.getDocBase());
                }
                if (!docBase.getCanonicalFile().toPath().startsWith(this.host.getAppBaseFile().toPath())) {
                    isExternal = true;
                    deployedApp.redeployResources.put(contextXml.getAbsolutePath(), contextXml.lastModified());
                    deployedApp.redeployResources.put(docBase.getAbsolutePath(), docBase.lastModified());
                    if (docBase.getAbsolutePath().toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                        isExternalWar = true;
                    }
                }
                else {
                    HostConfig.log.warn((Object)HostConfig.sm.getString("hostConfig.deployDescriptor.localDocBaseSpecified", new Object[] { docBase }));
                    context.setDocBase(null);
                }
            }
            this.host.addChild(context);
        }
        catch (final Throwable t) {
            ExceptionUtils.handleThrowable(t);
            HostConfig.log.error((Object)HostConfig.sm.getString("hostConfig.deployDescriptor.error", new Object[] { contextXml.getAbsolutePath() }), t);
        }
        finally {
            expandedDocBase = new File(this.host.getAppBaseFile(), cn.getBaseName());
            if (context.getDocBase() != null && !context.getDocBase().toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                expandedDocBase = new File(context.getDocBase());
                if (!expandedDocBase.isAbsolute()) {
                    expandedDocBase = new File(this.host.getAppBaseFile(), context.getDocBase());
                }
            }
            boolean unpackWAR = this.unpackWARs;
            if (unpackWAR && context instanceof StandardContext) {
                unpackWAR = ((StandardContext)context).getUnpackWAR();
            }
            if (isExternalWar) {
                if (unpackWAR) {
                    deployedApp.redeployResources.put(expandedDocBase.getAbsolutePath(), expandedDocBase.lastModified());
                    this.addWatchedResources(deployedApp, expandedDocBase.getAbsolutePath(), context);
                }
                else {
                    this.addWatchedResources(deployedApp, null, context);
                }
            }
            else {
                if (!isExternal) {
                    final File warDocBase = new File(expandedDocBase.getAbsolutePath() + ".war");
                    if (warDocBase.exists()) {
                        deployedApp.redeployResources.put(warDocBase.getAbsolutePath(), warDocBase.lastModified());
                    }
                    else {
                        deployedApp.redeployResources.put(warDocBase.getAbsolutePath(), 0L);
                    }
                }
                if (unpackWAR) {
                    deployedApp.redeployResources.put(expandedDocBase.getAbsolutePath(), expandedDocBase.lastModified());
                    this.addWatchedResources(deployedApp, expandedDocBase.getAbsolutePath(), context);
                }
                else {
                    this.addWatchedResources(deployedApp, null, context);
                }
                if (!isExternal) {
                    deployedApp.redeployResources.put(contextXml.getAbsolutePath(), contextXml.lastModified());
                }
            }
            this.addGlobalRedeployResources(deployedApp);
        }
        if (this.host.findChild(context.getName()) != null) {
            this.deployed.put(context.getName(), deployedApp);
        }
        if (HostConfig.log.isInfoEnabled()) {
            HostConfig.log.info((Object)HostConfig.sm.getString("hostConfig.deployDescriptor.finished", new Object[] { contextXml.getAbsolutePath(), System.currentTimeMillis() - startTime }));
        }
    }
    
    protected void deployWARs(final File appBase, final String[] files) {
        if (files == null) {
            return;
        }
        final ExecutorService es = this.host.getStartStopExecutor();
        final List<Future<?>> results = new ArrayList<Future<?>>();
        for (final String file : files) {
            if (!file.equalsIgnoreCase("META-INF")) {
                if (!file.equalsIgnoreCase("WEB-INF")) {
                    final File war = new File(appBase, file);
                    if (file.toLowerCase(Locale.ENGLISH).endsWith(".war") && war.isFile() && !this.invalidWars.contains(file)) {
                        final ContextName cn = new ContextName(file, true);
                        if (this.tryAddServiced(cn.getName())) {
                            try {
                                if (this.deploymentExists(cn.getName())) {
                                    final DeployedApplication app = this.deployed.get(cn.getName());
                                    boolean unpackWAR = this.unpackWARs;
                                    if (unpackWAR && this.host.findChild(cn.getName()) instanceof StandardContext) {
                                        unpackWAR = ((StandardContext)this.host.findChild(cn.getName())).getUnpackWAR();
                                    }
                                    if (!unpackWAR && app != null) {
                                        final File dir = new File(appBase, cn.getBaseName());
                                        if (dir.exists()) {
                                            if (!app.loggedDirWarning) {
                                                HostConfig.log.warn((Object)HostConfig.sm.getString("hostConfig.deployWar.hiddenDir", new Object[] { dir.getAbsoluteFile(), war.getAbsoluteFile() }));
                                                app.loggedDirWarning = true;
                                            }
                                        }
                                        else {
                                            app.loggedDirWarning = false;
                                        }
                                    }
                                    this.removeServiced(cn.getName());
                                }
                                else if (!this.validateContextPath(appBase, cn.getBaseName())) {
                                    HostConfig.log.error((Object)HostConfig.sm.getString("hostConfig.illegalWarName", new Object[] { file }));
                                    this.invalidWars.add(file);
                                    this.removeServiced(cn.getName());
                                }
                                else {
                                    results.add(es.submit(new DeployWar(this, cn, war)));
                                }
                            }
                            catch (final Throwable t) {
                                ExceptionUtils.handleThrowable(t);
                                this.removeServiced(cn.getName());
                                throw t;
                            }
                        }
                    }
                }
            }
        }
        for (final Future<?> result : results) {
            try {
                result.get();
            }
            catch (final Exception e) {
                HostConfig.log.error((Object)HostConfig.sm.getString("hostConfig.deployWar.threaded.error"), (Throwable)e);
            }
        }
    }
    
    private boolean validateContextPath(final File appBase, final String contextPath) {
        String canonicalDocBase = null;
        StringBuilder docBase;
        try {
            final String canonicalAppBase = appBase.getCanonicalPath();
            docBase = new StringBuilder(canonicalAppBase);
            if (canonicalAppBase.endsWith(File.separator)) {
                docBase.append(contextPath.substring(1).replace('/', File.separatorChar));
            }
            else {
                docBase.append(contextPath.replace('/', File.separatorChar));
            }
            canonicalDocBase = new File(docBase.toString()).getCanonicalPath();
            if (canonicalDocBase.endsWith(File.separator)) {
                docBase.append(File.separator);
            }
        }
        catch (final IOException ioe) {
            return false;
        }
        return canonicalDocBase.equals(docBase.toString());
    }
    
    protected void deployWAR(final ContextName cn, final File war) {
        File xml = new File(this.host.getAppBaseFile(), cn.getBaseName() + "/" + "META-INF/context.xml");
        final File warTracker = new File(this.host.getAppBaseFile(), cn.getBaseName() + "/META-INF/war-tracker");
        boolean xmlInWar = false;
        try (final JarFile jar = new JarFile(war)) {
            final JarEntry entry = jar.getJarEntry("META-INF/context.xml");
            if (entry != null) {
                xmlInWar = true;
            }
        }
        catch (final IOException ex) {}
        boolean useXml = false;
        if (xml.exists() && this.unpackWARs && (!warTracker.exists() || warTracker.lastModified() == war.lastModified())) {
            useXml = true;
        }
        Context context = null;
        final boolean deployThisXML = this.isDeployThisXML(war, cn);
        try {
            if (deployThisXML && useXml && !this.copyXML) {
                synchronized (this.digesterLock) {
                    try {
                        context = (Context)this.digester.parse(xml);
                    }
                    catch (final Exception e) {
                        HostConfig.log.error((Object)HostConfig.sm.getString("hostConfig.deployDescriptor.error", new Object[] { war.getAbsolutePath() }), (Throwable)e);
                        this.digester.reset();
                    }
                    finally {
                        this.digester.reset();
                        if (context == null) {
                            context = new FailedContext();
                        }
                    }
                }
                context.setConfigFile(xml.toURI().toURL());
            }
            else if (deployThisXML && xmlInWar) {
                synchronized (this.digesterLock) {
                    try (final JarFile jar2 = new JarFile(war)) {
                        final JarEntry entry2 = jar2.getJarEntry("META-INF/context.xml");
                        try (final InputStream istream = jar2.getInputStream(entry2)) {
                            context = (Context)this.digester.parse(istream);
                        }
                    }
                    catch (final Exception e) {
                        HostConfig.log.error((Object)HostConfig.sm.getString("hostConfig.deployDescriptor.error", new Object[] { war.getAbsolutePath() }), (Throwable)e);
                    }
                    finally {
                        this.digester.reset();
                        if (context == null) {
                            context = new FailedContext();
                        }
                        context.setConfigFile(UriUtil.buildJarUrl(war, "META-INF/context.xml"));
                    }
                }
            }
            else if (!deployThisXML && xmlInWar) {
                HostConfig.log.error((Object)HostConfig.sm.getString("hostConfig.deployDescriptor.blocked", new Object[] { cn.getPath(), "META-INF/context.xml", new File(this.host.getConfigBaseFile(), cn.getBaseName() + ".xml") }));
            }
            else {
                context = (Context)Class.forName(this.contextClass).getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
            }
        }
        catch (final Throwable t) {
            ExceptionUtils.handleThrowable(t);
            HostConfig.log.error((Object)HostConfig.sm.getString("hostConfig.deployWar.error", new Object[] { war.getAbsolutePath() }), t);
        }
        finally {
            if (context == null) {
                context = new FailedContext();
            }
        }
        boolean copyThisXml = false;
        if (deployThisXML) {
            if (this.host instanceof StandardHost) {
                copyThisXml = ((StandardHost)this.host).isCopyXML();
            }
            if (!copyThisXml && context instanceof StandardContext) {
                copyThisXml = ((StandardContext)context).getCopyXML();
            }
            if (xmlInWar && copyThisXml) {
                xml = new File(this.host.getConfigBaseFile(), cn.getBaseName() + ".xml");
                try (final JarFile jar2 = new JarFile(war)) {
                    final JarEntry entry2 = jar2.getJarEntry("META-INF/context.xml");
                    try (final InputStream istream = jar2.getInputStream(entry2);
                         final FileOutputStream fos = new FileOutputStream(xml);
                         final BufferedOutputStream ostream = new BufferedOutputStream(fos, 1024)) {
                        final byte[] buffer = new byte[1024];
                        while (true) {
                            final int n = istream.read(buffer);
                            if (n < 0) {
                                break;
                            }
                            ostream.write(buffer, 0, n);
                        }
                        ostream.flush();
                    }
                }
                catch (final IOException ex2) {}
            }
        }
        final DeployedApplication deployedApp = new DeployedApplication(cn.getName(), xml.exists() && deployThisXML && copyThisXml);
        long startTime = 0L;
        if (HostConfig.log.isInfoEnabled()) {
            startTime = System.currentTimeMillis();
            HostConfig.log.info((Object)HostConfig.sm.getString("hostConfig.deployWar", new Object[] { war.getAbsolutePath() }));
        }
        try {
            deployedApp.redeployResources.put(war.getAbsolutePath(), war.lastModified());
            if (deployThisXML && xml.exists() && copyThisXml) {
                deployedApp.redeployResources.put(xml.getAbsolutePath(), xml.lastModified());
            }
            else {
                deployedApp.redeployResources.put(new File(this.host.getConfigBaseFile(), cn.getBaseName() + ".xml").getAbsolutePath(), 0L);
            }
            final Class<?> clazz = Class.forName(this.host.getConfigClass());
            final LifecycleListener listener = (LifecycleListener)clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
            context.addLifecycleListener(listener);
            context.setName(cn.getName());
            context.setPath(cn.getPath());
            context.setWebappVersion(cn.getVersion());
            context.setDocBase(cn.getBaseName() + ".war");
            this.host.addChild(context);
        }
        catch (final Throwable t2) {
            ExceptionUtils.handleThrowable(t2);
            HostConfig.log.error((Object)HostConfig.sm.getString("hostConfig.deployWar.error", new Object[] { war.getAbsolutePath() }), t2);
        }
        finally {
            boolean unpackWAR = this.unpackWARs;
            if (unpackWAR && context instanceof StandardContext) {
                unpackWAR = ((StandardContext)context).getUnpackWAR();
            }
            if (unpackWAR && context.getDocBase() != null) {
                final File docBase = new File(this.host.getAppBaseFile(), cn.getBaseName());
                deployedApp.redeployResources.put(docBase.getAbsolutePath(), docBase.lastModified());
                this.addWatchedResources(deployedApp, docBase.getAbsolutePath(), context);
                if (deployThisXML && !copyThisXml && (xmlInWar || xml.exists())) {
                    deployedApp.redeployResources.put(xml.getAbsolutePath(), xml.lastModified());
                }
            }
            else {
                this.addWatchedResources(deployedApp, null, context);
            }
            this.addGlobalRedeployResources(deployedApp);
        }
        this.deployed.put(cn.getName(), deployedApp);
        if (HostConfig.log.isInfoEnabled()) {
            HostConfig.log.info((Object)HostConfig.sm.getString("hostConfig.deployWar.finished", new Object[] { war.getAbsolutePath(), System.currentTimeMillis() - startTime }));
        }
    }
    
    protected void deployDirectories(final File appBase, final String[] files) {
        if (files == null) {
            return;
        }
        final ExecutorService es = this.host.getStartStopExecutor();
        final List<Future<?>> results = new ArrayList<Future<?>>();
        for (final String file : files) {
            if (!file.equalsIgnoreCase("META-INF")) {
                if (!file.equalsIgnoreCase("WEB-INF")) {
                    final File dir = new File(appBase, file);
                    if (dir.isDirectory()) {
                        final ContextName cn = new ContextName(file, false);
                        if (this.tryAddServiced(cn.getName())) {
                            try {
                                if (this.deploymentExists(cn.getName())) {
                                    this.removeServiced(cn.getName());
                                }
                                else {
                                    results.add(es.submit(new DeployDirectory(this, cn, dir)));
                                }
                            }
                            catch (final Throwable t) {
                                ExceptionUtils.handleThrowable(t);
                                this.removeServiced(cn.getName());
                                throw t;
                            }
                        }
                    }
                }
            }
        }
        for (final Future<?> result : results) {
            try {
                result.get();
            }
            catch (final Exception e) {
                HostConfig.log.error((Object)HostConfig.sm.getString("hostConfig.deployDir.threaded.error"), (Throwable)e);
            }
        }
    }
    
    protected void deployDirectory(final ContextName cn, final File dir) {
        long startTime = 0L;
        if (HostConfig.log.isInfoEnabled()) {
            startTime = System.currentTimeMillis();
            HostConfig.log.info((Object)HostConfig.sm.getString("hostConfig.deployDir", new Object[] { dir.getAbsolutePath() }));
        }
        Context context = null;
        final File xml = new File(dir, "META-INF/context.xml");
        final File xmlCopy = new File(this.host.getConfigBaseFile(), cn.getBaseName() + ".xml");
        boolean copyThisXml = this.isCopyXML();
        final boolean deployThisXML = this.isDeployThisXML(dir, cn);
        DeployedApplication deployedApp = null;
        try {
            if (deployThisXML && xml.exists()) {
                synchronized (this.digesterLock) {
                    try {
                        context = (Context)this.digester.parse(xml);
                    }
                    catch (final Exception e) {
                        HostConfig.log.error((Object)HostConfig.sm.getString("hostConfig.deployDescriptor.error", new Object[] { xml }), (Throwable)e);
                        context = new FailedContext();
                    }
                    finally {
                        this.digester.reset();
                        if (context == null) {
                            context = new FailedContext();
                        }
                    }
                }
                if (!copyThisXml && context instanceof StandardContext) {
                    copyThisXml = ((StandardContext)context).getCopyXML();
                }
                if (copyThisXml) {
                    Files.copy(xml.toPath(), xmlCopy.toPath(), new CopyOption[0]);
                    context.setConfigFile(xmlCopy.toURI().toURL());
                }
                else {
                    context.setConfigFile(xml.toURI().toURL());
                }
            }
            else if (!deployThisXML && xml.exists()) {
                HostConfig.log.error((Object)HostConfig.sm.getString("hostConfig.deployDescriptor.blocked", new Object[] { cn.getPath(), xml, xmlCopy }));
                context = new FailedContext();
            }
            else {
                context = (Context)Class.forName(this.contextClass).getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
            }
            final Class<?> clazz = Class.forName(this.host.getConfigClass());
            final LifecycleListener listener = (LifecycleListener)clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
            context.addLifecycleListener(listener);
            context.setName(cn.getName());
            context.setPath(cn.getPath());
            context.setWebappVersion(cn.getVersion());
            context.setDocBase(cn.getBaseName());
            this.host.addChild(context);
        }
        catch (final Throwable t) {
            ExceptionUtils.handleThrowable(t);
            HostConfig.log.error((Object)HostConfig.sm.getString("hostConfig.deployDir.error", new Object[] { dir.getAbsolutePath() }), t);
        }
        finally {
            deployedApp = new DeployedApplication(cn.getName(), xml.exists() && deployThisXML && copyThisXml);
            deployedApp.redeployResources.put(dir.getAbsolutePath() + ".war", 0L);
            deployedApp.redeployResources.put(dir.getAbsolutePath(), dir.lastModified());
            if (deployThisXML && xml.exists()) {
                if (copyThisXml) {
                    deployedApp.redeployResources.put(xmlCopy.getAbsolutePath(), xmlCopy.lastModified());
                }
                else {
                    deployedApp.redeployResources.put(xml.getAbsolutePath(), xml.lastModified());
                    deployedApp.redeployResources.put(xmlCopy.getAbsolutePath(), 0L);
                }
            }
            else {
                deployedApp.redeployResources.put(xmlCopy.getAbsolutePath(), 0L);
                if (!xml.exists()) {
                    deployedApp.redeployResources.put(xml.getAbsolutePath(), 0L);
                }
            }
            this.addWatchedResources(deployedApp, dir.getAbsolutePath(), context);
            this.addGlobalRedeployResources(deployedApp);
        }
        this.deployed.put(cn.getName(), deployedApp);
        if (HostConfig.log.isInfoEnabled()) {
            HostConfig.log.info((Object)HostConfig.sm.getString("hostConfig.deployDir.finished", new Object[] { dir.getAbsolutePath(), System.currentTimeMillis() - startTime }));
        }
    }
    
    protected boolean deploymentExists(final String contextName) {
        return this.deployed.containsKey(contextName) || this.host.findChild(contextName) != null;
    }
    
    protected void addWatchedResources(final DeployedApplication app, final String docBase, final Context context) {
        File docBaseFile = null;
        if (docBase != null) {
            docBaseFile = new File(docBase);
            if (!docBaseFile.isAbsolute()) {
                docBaseFile = new File(this.host.getAppBaseFile(), docBase);
            }
        }
        final String[] arr$;
        final String[] watchedResources = arr$ = context.findWatchedResources();
        for (final String watchedResource : arr$) {
            File resource = new File(watchedResource);
            Label_0235: {
                if (!resource.isAbsolute()) {
                    if (docBase != null) {
                        resource = new File(docBaseFile, watchedResource);
                    }
                    else {
                        if (HostConfig.log.isDebugEnabled()) {
                            HostConfig.log.debug((Object)("Ignoring non-existent WatchedResource '" + resource.getAbsolutePath() + "'"));
                        }
                        break Label_0235;
                    }
                }
                if (HostConfig.log.isDebugEnabled()) {
                    HostConfig.log.debug((Object)("Watching WatchedResource '" + resource.getAbsolutePath() + "'"));
                }
                app.reloadResources.put(resource.getAbsolutePath(), resource.lastModified());
            }
        }
    }
    
    protected void addGlobalRedeployResources(final DeployedApplication app) {
        final File hostContextXml = new File(this.getConfigBaseName(), "context.xml.default");
        if (hostContextXml.isFile()) {
            app.redeployResources.put(hostContextXml.getAbsolutePath(), hostContextXml.lastModified());
        }
        final File globalContextXml = this.returnCanonicalPath("conf/context.xml");
        if (globalContextXml.isFile()) {
            app.redeployResources.put(globalContextXml.getAbsolutePath(), globalContextXml.lastModified());
        }
    }
    
    protected synchronized void checkResources(final DeployedApplication app, final boolean skipFileModificationResolutionCheck) {
        String[] resources = app.redeployResources.keySet().toArray(new String[0]);
        final long currentTimeWithResolutionOffset = System.currentTimeMillis() - 1000L;
        for (int i = 0; i < resources.length; ++i) {
            final File resource = new File(resources[i]);
            if (HostConfig.log.isDebugEnabled()) {
                HostConfig.log.debug((Object)("Checking context[" + app.name + "] redeploy resource " + resource));
            }
            final long lastModified = app.redeployResources.get(resources[i]);
            if (resource.exists() || lastModified == 0L) {
                if (resource.lastModified() != lastModified && (!this.host.getAutoDeploy() || resource.lastModified() < currentTimeWithResolutionOffset || skipFileModificationResolutionCheck)) {
                    if (resource.isDirectory()) {
                        app.redeployResources.put(resources[i], resource.lastModified());
                    }
                    else {
                        if (app.hasDescriptor && resource.getName().toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                            final Context context = (Context)this.host.findChild(app.name);
                            final String docBase = context.getDocBase();
                            if (!docBase.toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                                File docBaseFile = new File(docBase);
                                if (!docBaseFile.isAbsolute()) {
                                    docBaseFile = new File(this.host.getAppBaseFile(), docBase);
                                }
                                this.reload(app, docBaseFile, resource.getAbsolutePath());
                            }
                            else {
                                this.reload(app, null, null);
                            }
                            app.redeployResources.put(resources[i], resource.lastModified());
                            app.timestamp = System.currentTimeMillis();
                            boolean unpackWAR = this.unpackWARs;
                            if (unpackWAR && context instanceof StandardContext) {
                                unpackWAR = ((StandardContext)context).getUnpackWAR();
                            }
                            if (unpackWAR) {
                                this.addWatchedResources(app, context.getDocBase(), context);
                            }
                            else {
                                this.addWatchedResources(app, null, context);
                            }
                            return;
                        }
                        this.undeploy(app);
                        this.deleteRedeployResources(app, resources, i, false);
                        return;
                    }
                }
            }
            else {
                try {
                    Thread.sleep(500L);
                }
                catch (final InterruptedException ex) {}
                if (!resource.exists()) {
                    this.undeploy(app);
                    this.deleteRedeployResources(app, resources, i, true);
                    return;
                }
            }
        }
        resources = app.reloadResources.keySet().toArray(new String[0]);
        boolean update = false;
        for (final String s : resources) {
            final File resource2 = new File(s);
            if (HostConfig.log.isDebugEnabled()) {
                HostConfig.log.debug((Object)("Checking context[" + app.name + "] reload resource " + resource2));
            }
            final long lastModified2 = app.reloadResources.get(s);
            if ((resource2.lastModified() != lastModified2 && (!this.host.getAutoDeploy() || resource2.lastModified() < currentTimeWithResolutionOffset || skipFileModificationResolutionCheck)) || update) {
                if (!update) {
                    this.reload(app, null, null);
                    update = true;
                }
                app.reloadResources.put(s, resource2.lastModified());
            }
            app.timestamp = System.currentTimeMillis();
        }
    }
    
    private void reload(final DeployedApplication app, final File fileToRemove, final String newDocBase) {
        if (HostConfig.log.isInfoEnabled()) {
            HostConfig.log.info((Object)HostConfig.sm.getString("hostConfig.reload", new Object[] { app.name }));
        }
        final Context context = (Context)this.host.findChild(app.name);
        if (context.getState().isAvailable()) {
            if (fileToRemove != null && newDocBase != null) {
                context.addLifecycleListener(new ExpandedDirectoryRemovalListener(fileToRemove, newDocBase));
            }
            context.reload();
        }
        else {
            if (fileToRemove != null && newDocBase != null) {
                ExpandWar.delete(fileToRemove);
                context.setDocBase(newDocBase);
            }
            try {
                context.start();
            }
            catch (final Exception e) {
                HostConfig.log.error((Object)HostConfig.sm.getString("hostConfig.context.restart", new Object[] { app.name }), (Throwable)e);
            }
        }
    }
    
    private void undeploy(final DeployedApplication app) {
        if (HostConfig.log.isInfoEnabled()) {
            HostConfig.log.info((Object)HostConfig.sm.getString("hostConfig.undeploy", new Object[] { app.name }));
        }
        final Container context = this.host.findChild(app.name);
        try {
            this.host.removeChild(context);
        }
        catch (final Throwable t) {
            ExceptionUtils.handleThrowable(t);
            HostConfig.log.warn((Object)HostConfig.sm.getString("hostConfig.context.remove", new Object[] { app.name }), t);
        }
        this.deployed.remove(app.name);
    }
    
    private void deleteRedeployResources(final DeployedApplication app, final String[] resources, final int i, final boolean deleteReloadResources) {
        for (int j = i + 1; j < resources.length; ++j) {
            final File current = new File(resources[j]);
            if (!"context.xml.default".equals(current.getName())) {
                if (this.isDeletableResource(app, current)) {
                    if (HostConfig.log.isDebugEnabled()) {
                        HostConfig.log.debug((Object)("Delete " + current));
                    }
                    ExpandWar.delete(current);
                }
            }
        }
        if (deleteReloadResources) {
            final String[] arr$;
            final String[] resources2 = arr$ = app.reloadResources.keySet().toArray(new String[0]);
            for (final String s : arr$) {
                final File current2 = new File(s);
                if (!"context.xml.default".equals(current2.getName())) {
                    if (this.isDeletableResource(app, current2)) {
                        if (HostConfig.log.isDebugEnabled()) {
                            HostConfig.log.debug((Object)("Delete " + current2));
                        }
                        ExpandWar.delete(current2);
                    }
                }
            }
        }
    }
    
    private boolean isDeletableResource(final DeployedApplication app, final File resource) {
        if (!resource.isAbsolute()) {
            HostConfig.log.warn((Object)HostConfig.sm.getString("hostConfig.resourceNotAbsolute", new Object[] { app.name, resource }));
            return false;
        }
        String canonicalLocation;
        try {
            canonicalLocation = resource.getParentFile().getCanonicalPath();
        }
        catch (final IOException e) {
            HostConfig.log.warn((Object)HostConfig.sm.getString("hostConfig.canonicalizing", new Object[] { resource.getParentFile(), app.name }), (Throwable)e);
            return false;
        }
        String canonicalAppBase;
        try {
            canonicalAppBase = this.host.getAppBaseFile().getCanonicalPath();
        }
        catch (final IOException e2) {
            HostConfig.log.warn((Object)HostConfig.sm.getString("hostConfig.canonicalizing", new Object[] { this.host.getAppBaseFile(), app.name }), (Throwable)e2);
            return false;
        }
        if (canonicalLocation.equals(canonicalAppBase)) {
            return true;
        }
        String canonicalConfigBase;
        try {
            canonicalConfigBase = this.host.getConfigBaseFile().getCanonicalPath();
        }
        catch (final IOException e3) {
            HostConfig.log.warn((Object)HostConfig.sm.getString("hostConfig.canonicalizing", new Object[] { this.host.getConfigBaseFile(), app.name }), (Throwable)e3);
            return false;
        }
        return canonicalLocation.equals(canonicalConfigBase) && resource.getName().endsWith(".xml");
    }
    
    public void beforeStart() {
        if (this.host.getCreateDirs()) {
            final File[] arr$;
            final File[] dirs = arr$ = new File[] { this.host.getAppBaseFile(), this.host.getConfigBaseFile() };
            for (final File dir : arr$) {
                if (!dir.mkdirs() && !dir.isDirectory()) {
                    HostConfig.log.error((Object)HostConfig.sm.getString("hostConfig.createDirs", new Object[] { dir }));
                }
            }
        }
    }
    
    public void start() {
        if (HostConfig.log.isDebugEnabled()) {
            HostConfig.log.debug((Object)HostConfig.sm.getString("hostConfig.start"));
        }
        try {
            final ObjectName hostON = this.host.getObjectName();
            this.oname = new ObjectName(hostON.getDomain() + ":type=Deployer,host=" + this.host.getName());
            Registry.getRegistry((Object)null, (Object)null).registerComponent((Object)this, this.oname, this.getClass().getName());
        }
        catch (final Exception e) {
            HostConfig.log.warn((Object)HostConfig.sm.getString("hostConfig.jmx.register", new Object[] { this.oname }), (Throwable)e);
        }
        if (!this.host.getAppBaseFile().isDirectory()) {
            HostConfig.log.error((Object)HostConfig.sm.getString("hostConfig.appBase", new Object[] { this.host.getName(), this.host.getAppBaseFile().getPath() }));
            this.host.setDeployOnStartup(false);
            this.host.setAutoDeploy(false);
        }
        if (this.host.getDeployOnStartup()) {
            this.deployApps();
        }
    }
    
    public void stop() {
        if (HostConfig.log.isDebugEnabled()) {
            HostConfig.log.debug((Object)HostConfig.sm.getString("hostConfig.stop"));
        }
        if (this.oname != null) {
            try {
                Registry.getRegistry((Object)null, (Object)null).unregisterComponent(this.oname);
            }
            catch (final Exception e) {
                HostConfig.log.warn((Object)HostConfig.sm.getString("hostConfig.jmx.unregister", new Object[] { this.oname }), (Throwable)e);
            }
        }
        this.oname = null;
    }
    
    protected void check() {
        if (this.host.getAutoDeploy()) {
            final DeployedApplication[] arr$;
            final DeployedApplication[] apps = arr$ = this.deployed.values().toArray(new DeployedApplication[0]);
            for (final DeployedApplication app : arr$) {
                if (this.tryAddServiced(app.name)) {
                    try {
                        this.checkResources(app, false);
                    }
                    finally {
                        this.removeServiced(app.name);
                    }
                }
            }
            if (this.host.getUndeployOldVersions()) {
                this.checkUndeploy();
            }
            this.deployApps();
        }
    }
    
    public void check(final String name) {
        if (this.tryAddServiced(name)) {
            try {
                final DeployedApplication app = this.deployed.get(name);
                if (app != null) {
                    this.checkResources(app, true);
                }
                this.deployApps(name);
            }
            finally {
                this.removeServiced(name);
            }
        }
    }
    
    public synchronized void checkUndeploy() {
        if (this.deployed.size() < 2) {
            return;
        }
        final SortedSet<String> sortedAppNames = new TreeSet<String>(this.deployed.keySet());
        final Iterator<String> iter = sortedAppNames.iterator();
        ContextName previous = new ContextName(iter.next(), false);
        do {
            final ContextName current = new ContextName(iter.next(), false);
            if (current.getPath().equals(previous.getPath())) {
                final Context previousContext = (Context)this.host.findChild(previous.getName());
                final Context currentContext = (Context)this.host.findChild(current.getName());
                if (previousContext != null && currentContext != null && currentContext.getState().isAvailable() && this.tryAddServiced(previous.getName())) {
                    try {
                        final Manager manager = previousContext.getManager();
                        if (manager != null) {
                            int sessionCount;
                            if (manager instanceof DistributedManager) {
                                sessionCount = ((DistributedManager)manager).getActiveSessionsFull();
                            }
                            else {
                                sessionCount = manager.getActiveSessions();
                            }
                            if (sessionCount == 0) {
                                if (HostConfig.log.isInfoEnabled()) {
                                    HostConfig.log.info((Object)HostConfig.sm.getString("hostConfig.undeployVersion", new Object[] { previous.getName() }));
                                }
                                final DeployedApplication app = this.deployed.get(previous.getName());
                                final String[] resources = app.redeployResources.keySet().toArray(new String[0]);
                                this.undeploy(app);
                                this.deleteRedeployResources(app, resources, -1, true);
                            }
                        }
                    }
                    finally {
                        this.removeServiced(previous.getName());
                    }
                }
            }
            previous = current;
        } while (iter.hasNext());
    }
    
    public void manageApp(final Context context) {
        final String contextName = context.getName();
        if (this.deployed.containsKey(contextName)) {
            return;
        }
        final DeployedApplication deployedApp = new DeployedApplication(contextName, false);
        boolean isWar = false;
        if (context.getDocBase() != null) {
            File docBase = new File(context.getDocBase());
            if (!docBase.isAbsolute()) {
                docBase = new File(this.host.getAppBaseFile(), context.getDocBase());
            }
            deployedApp.redeployResources.put(docBase.getAbsolutePath(), docBase.lastModified());
            if (docBase.getAbsolutePath().toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                isWar = true;
            }
        }
        this.host.addChild(context);
        boolean unpackWAR = this.unpackWARs;
        if (unpackWAR && context instanceof StandardContext) {
            unpackWAR = ((StandardContext)context).getUnpackWAR();
        }
        if (isWar && unpackWAR) {
            final File docBase2 = new File(this.host.getAppBaseFile(), context.getBaseName());
            deployedApp.redeployResources.put(docBase2.getAbsolutePath(), docBase2.lastModified());
            this.addWatchedResources(deployedApp, docBase2.getAbsolutePath(), context);
        }
        else {
            this.addWatchedResources(deployedApp, null, context);
        }
        this.deployed.put(contextName, deployedApp);
    }
    
    public void unmanageApp(final String contextName) {
        this.deployed.remove(contextName);
        this.host.removeChild(this.host.findChild(contextName));
    }
    
    static {
        log = LogFactory.getLog((Class)HostConfig.class);
        sm = StringManager.getManager((Class)HostConfig.class);
    }
    
    protected static class DeployedApplication
    {
        public final String name;
        public final boolean hasDescriptor;
        public final LinkedHashMap<String, Long> redeployResources;
        public final HashMap<String, Long> reloadResources;
        public long timestamp;
        public boolean loggedDirWarning;
        
        public DeployedApplication(final String name, final boolean hasDescriptor) {
            this.redeployResources = new LinkedHashMap<String, Long>();
            this.reloadResources = new HashMap<String, Long>();
            this.timestamp = System.currentTimeMillis();
            this.loggedDirWarning = false;
            this.name = name;
            this.hasDescriptor = hasDescriptor;
        }
    }
    
    private static class DeployDescriptor implements Runnable
    {
        private HostConfig config;
        private ContextName cn;
        private File descriptor;
        
        public DeployDescriptor(final HostConfig config, final ContextName cn, final File descriptor) {
            this.config = config;
            this.cn = cn;
            this.descriptor = descriptor;
        }
        
        @Override
        public void run() {
            try {
                this.config.deployDescriptor(this.cn, this.descriptor);
            }
            finally {
                this.config.removeServiced(this.cn.getName());
            }
        }
    }
    
    private static class DeployWar implements Runnable
    {
        private HostConfig config;
        private ContextName cn;
        private File war;
        
        public DeployWar(final HostConfig config, final ContextName cn, final File war) {
            this.config = config;
            this.cn = cn;
            this.war = war;
        }
        
        @Override
        public void run() {
            try {
                this.config.deployWAR(this.cn, this.war);
            }
            finally {
                this.config.removeServiced(this.cn.getName());
            }
        }
    }
    
    private static class DeployDirectory implements Runnable
    {
        private HostConfig config;
        private ContextName cn;
        private File dir;
        
        public DeployDirectory(final HostConfig config, final ContextName cn, final File dir) {
            this.config = config;
            this.cn = cn;
            this.dir = dir;
        }
        
        @Override
        public void run() {
            try {
                this.config.deployDirectory(this.cn, this.dir);
            }
            finally {
                this.config.removeServiced(this.cn.getName());
            }
        }
    }
    
    private static class ExpandedDirectoryRemovalListener implements LifecycleListener
    {
        private final File toDelete;
        private final String newDocBase;
        
        public ExpandedDirectoryRemovalListener(final File toDelete, final String newDocBase) {
            this.toDelete = toDelete;
            this.newDocBase = newDocBase;
        }
        
        @Override
        public void lifecycleEvent(final LifecycleEvent event) {
            if ("after_stop".equals(event.getType())) {
                final Context context = (Context)event.getLifecycle();
                ExpandWar.delete(this.toDelete);
                context.setDocBase(this.newDocBase);
                context.removeLifecycleListener(this);
            }
        }
    }
}
