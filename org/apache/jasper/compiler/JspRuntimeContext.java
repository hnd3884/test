package org.apache.jasper.compiler;

import java.io.IOException;
import java.security.Permission;
import java.io.FilePermission;
import java.security.cert.Certificate;
import java.security.Policy;
import java.net.URL;
import java.net.URISyntaxException;
import java.io.File;
import java.net.URLClassLoader;
import org.apache.jasper.JspCompilationContext;
import java.util.List;
import javax.servlet.ServletException;
import org.apache.jasper.runtime.ExceptionUtils;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.jasper.Constants;
import org.apache.jasper.servlet.JspCServletContext;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.juli.logging.LogFactory;
import org.apache.jasper.util.FastRemovalDequeue;
import org.apache.jasper.servlet.JspServletWrapper;
import java.util.Map;
import java.security.CodeSource;
import java.security.PermissionCollection;
import org.apache.jasper.Options;
import javax.servlet.ServletContext;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.juli.logging.Log;

public final class JspRuntimeContext
{
    private final Log log;
    private final AtomicInteger jspReloadCount;
    private final AtomicInteger jspUnloadCount;
    private final ServletContext context;
    private final Options options;
    private final ClassLoader parentClassLoader;
    private final PermissionCollection permissionCollection;
    private final CodeSource codeSource;
    private final String classpath;
    private volatile long lastCompileCheck;
    private volatile long lastJspQueueUpdate;
    private long jspIdleTimeout;
    private final Map<String, JspServletWrapper> jsps;
    private FastRemovalDequeue<JspServletWrapper> jspQueue;
    private volatile boolean compileCheckInProgress;
    
    public JspRuntimeContext(final ServletContext context, final Options options) {
        this.log = LogFactory.getLog((Class)JspRuntimeContext.class);
        this.jspReloadCount = new AtomicInteger(0);
        this.jspUnloadCount = new AtomicInteger(0);
        this.lastCompileCheck = -1L;
        this.lastJspQueueUpdate = System.currentTimeMillis();
        this.jsps = new ConcurrentHashMap<String, JspServletWrapper>();
        this.jspQueue = null;
        this.compileCheckInProgress = false;
        this.context = context;
        this.options = options;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = this.getClass().getClassLoader();
        }
        if (this.log.isDebugEnabled()) {
            if (loader != null) {
                this.log.debug((Object)Localizer.getMessage("jsp.message.parent_class_loader_is", loader.toString()));
            }
            else {
                this.log.debug((Object)Localizer.getMessage("jsp.message.parent_class_loader_is", "<none>"));
            }
        }
        this.parentClassLoader = loader;
        this.classpath = this.initClassPath();
        if (context instanceof JspCServletContext) {
            this.codeSource = null;
            this.permissionCollection = null;
            return;
        }
        if (Constants.IS_SECURITY_ENABLED) {
            final SecurityHolder holder = this.initSecurity();
            this.codeSource = holder.cs;
            this.permissionCollection = holder.pc;
        }
        else {
            this.codeSource = null;
            this.permissionCollection = null;
        }
        final String appBase = context.getRealPath("/");
        if (!options.getDevelopment() && appBase != null && options.getCheckInterval() > 0) {
            this.lastCompileCheck = System.currentTimeMillis();
        }
        if (options.getMaxLoadedJsps() > 0) {
            this.jspQueue = new FastRemovalDequeue<JspServletWrapper>(options.getMaxLoadedJsps());
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)Localizer.getMessage("jsp.message.jsp_queue_created", "" + options.getMaxLoadedJsps(), context.getContextPath()));
            }
        }
        this.jspIdleTimeout = options.getJspIdleTimeout() * 1000;
    }
    
    public void addWrapper(final String jspUri, final JspServletWrapper jsw) {
        this.jsps.put(jspUri, jsw);
    }
    
    public JspServletWrapper getWrapper(final String jspUri) {
        return this.jsps.get(jspUri);
    }
    
    public void removeWrapper(final String jspUri) {
        this.jsps.remove(jspUri);
    }
    
    public FastRemovalDequeue.Entry push(final JspServletWrapper jsw) {
        if (this.log.isTraceEnabled()) {
            this.log.trace((Object)Localizer.getMessage("jsp.message.jsp_added", jsw.getJspUri(), this.context.getContextPath()));
        }
        final FastRemovalDequeue.Entry entry = this.jspQueue.push(jsw);
        final JspServletWrapper replaced = entry.getReplaced();
        if (replaced != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)Localizer.getMessage("jsp.message.jsp_removed_excess", replaced.getJspUri(), this.context.getContextPath()));
            }
            this.unloadJspServletWrapper(replaced);
            entry.clearReplaced();
        }
        return entry;
    }
    
    public void makeYoungest(final FastRemovalDequeue.Entry unloadHandle) {
        if (this.log.isTraceEnabled()) {
            final JspServletWrapper jsw = unloadHandle.getContent();
            this.log.trace((Object)Localizer.getMessage("jsp.message.jsp_queue_update", jsw.getJspUri(), this.context.getContextPath()));
        }
        this.jspQueue.moveFirst(unloadHandle);
    }
    
    public int getJspCount() {
        return this.jsps.size();
    }
    
    public CodeSource getCodeSource() {
        return this.codeSource;
    }
    
    public ClassLoader getParentClassLoader() {
        return this.parentClassLoader;
    }
    
    public PermissionCollection getPermissionCollection() {
        return this.permissionCollection;
    }
    
    public void destroy() {
        for (final JspServletWrapper jspServletWrapper : this.jsps.values()) {
            jspServletWrapper.destroy();
        }
    }
    
    public void incrementJspReloadCount() {
        this.jspReloadCount.incrementAndGet();
    }
    
    public void setJspReloadCount(final int count) {
        this.jspReloadCount.set(count);
    }
    
    public int getJspReloadCount() {
        return this.jspReloadCount.intValue();
    }
    
    public int getJspQueueLength() {
        if (this.jspQueue != null) {
            return this.jspQueue.getSize();
        }
        return -1;
    }
    
    public int getJspUnloadCount() {
        return this.jspUnloadCount.intValue();
    }
    
    public void checkCompile() {
        if (this.lastCompileCheck < 0L) {
            return;
        }
        final long now = System.currentTimeMillis();
        if (now > this.lastCompileCheck + this.options.getCheckInterval() * 1000L) {
            this.lastCompileCheck = now;
            final List<JspServletWrapper> wrappersToReload = new ArrayList<JspServletWrapper>();
            this.compileCheckInProgress = true;
            final Object[] arr$;
            final Object[] wrappers = arr$ = this.jsps.values().toArray();
            for (final Object wrapper : arr$) {
                final JspServletWrapper jsw = (JspServletWrapper)wrapper;
                final JspCompilationContext ctxt = jsw.getJspEngineContext();
                synchronized (jsw) {
                    try {
                        ctxt.compile();
                        if (jsw.getReload()) {
                            wrappersToReload.add(jsw);
                        }
                    }
                    catch (final FileNotFoundException ex) {
                        ctxt.incrementRemoved();
                    }
                    catch (final Throwable t) {
                        ExceptionUtils.handleThrowable(t);
                        jsw.getServletContext().log(Localizer.getMessage("jsp.error.backgroundCompilationFailed"), t);
                    }
                }
            }
            this.compileCheckInProgress = false;
            for (final JspServletWrapper jsw2 : wrappersToReload) {
                try {
                    if (jsw2.isTagFile()) {
                        synchronized (this) {
                            jsw2.loadTagFile();
                        }
                    }
                    else {
                        jsw2.getServlet();
                    }
                }
                catch (final ServletException e) {
                    jsw2.getServletContext().log(Localizer.getMessage("jsp.error.reload"), (Throwable)e);
                }
            }
        }
    }
    
    public boolean isCompileCheckInProgress() {
        return this.compileCheckInProgress;
    }
    
    public String getClassPath() {
        return this.classpath;
    }
    
    public long getLastJspQueueUpdate() {
        return this.lastJspQueueUpdate;
    }
    
    private String initClassPath() {
        final StringBuilder cpath = new StringBuilder();
        if (this.parentClassLoader instanceof URLClassLoader) {
            final URL[] arr$;
            final URL[] urls = arr$ = ((URLClassLoader)this.parentClassLoader).getURLs();
            for (final URL url : arr$) {
                if (url.getProtocol().equals("file")) {
                    try {
                        final String decoded = url.toURI().getPath();
                        cpath.append(decoded + File.pathSeparator);
                    }
                    catch (final URISyntaxException e) {
                        this.log.warn((Object)Localizer.getMessage("jsp.warning.classpathUrl"), (Throwable)e);
                    }
                }
            }
        }
        cpath.append(this.options.getScratchDir() + File.pathSeparator);
        String cp = (String)this.context.getAttribute(Constants.SERVLET_CLASSPATH);
        if (cp == null || cp.equals("")) {
            cp = this.options.getClassPath();
        }
        final String path = cpath.toString() + cp;
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Compilation classpath initialized: " + path));
        }
        return path;
    }
    
    private SecurityHolder initSecurity() {
        final Policy policy = Policy.getPolicy();
        CodeSource source = null;
        PermissionCollection permissions = null;
        if (policy != null) {
            try {
                String docBase = this.context.getRealPath("/");
                if (docBase == null) {
                    docBase = this.options.getScratchDir().toString();
                }
                String codeBase = docBase;
                if (!codeBase.endsWith(File.separator)) {
                    codeBase += File.separator;
                }
                final File contextDir = new File(codeBase);
                final URL url = contextDir.getCanonicalFile().toURI().toURL();
                source = new CodeSource(url, (Certificate[])null);
                permissions = policy.getPermissions(source);
                if (!docBase.endsWith(File.separator)) {
                    permissions.add(new FilePermission(docBase, "read"));
                    docBase += File.separator;
                }
                else {
                    permissions.add(new FilePermission(docBase.substring(0, docBase.length() - 1), "read"));
                }
                docBase += "-";
                permissions.add(new FilePermission(docBase, "read"));
                String workDir = this.options.getScratchDir().toString();
                if (!workDir.endsWith(File.separator)) {
                    permissions.add(new FilePermission(workDir, "read,write"));
                    workDir += File.separator;
                }
                workDir += "-";
                permissions.add(new FilePermission(workDir, "read,write,delete"));
                permissions.add(new RuntimePermission("accessClassInPackage.org.apache.jasper.runtime"));
            }
            catch (final RuntimeException | IOException e) {
                this.context.log(Localizer.getMessage("jsp.error.security"), (Throwable)e);
            }
        }
        return new SecurityHolder(source, permissions);
    }
    
    private void unloadJspServletWrapper(final JspServletWrapper jsw) {
        this.removeWrapper(jsw.getJspUri());
        synchronized (jsw) {
            jsw.destroy();
        }
        this.jspUnloadCount.incrementAndGet();
    }
    
    public void checkUnload() {
        if (this.log.isTraceEnabled()) {
            int queueLength = -1;
            if (this.jspQueue != null) {
                queueLength = this.jspQueue.getSize();
            }
            this.log.trace((Object)Localizer.getMessage("jsp.message.jsp_unload_check", this.context.getContextPath(), "" + this.jsps.size(), "" + queueLength));
        }
        final long now = System.currentTimeMillis();
        if (this.jspIdleTimeout > 0L) {
            final long unloadBefore = now - this.jspIdleTimeout;
            final Object[] arr$;
            final Object[] wrappers = arr$ = this.jsps.values().toArray();
            for (final Object wrapper : arr$) {
                final JspServletWrapper jsw = (JspServletWrapper)wrapper;
                synchronized (jsw) {
                    if (jsw.getLastUsageTime() < unloadBefore) {
                        if (this.log.isDebugEnabled()) {
                            this.log.debug((Object)Localizer.getMessage("jsp.message.jsp_removed_idle", jsw.getJspUri(), this.context.getContextPath(), "" + (now - jsw.getLastUsageTime())));
                        }
                        if (this.jspQueue != null) {
                            this.jspQueue.remove(jsw.getUnloadHandle());
                        }
                        this.unloadJspServletWrapper(jsw);
                    }
                }
            }
        }
        this.lastJspQueueUpdate = now;
    }
    
    private static class SecurityHolder
    {
        private final CodeSource cs;
        private final PermissionCollection pc;
        
        private SecurityHolder(final CodeSource cs, final PermissionCollection pc) {
            this.cs = cs;
            this.pc = pc;
        }
    }
}
