package org.apache.catalina.session;

import javax.servlet.ServletContext;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import java.util.Iterator;
import java.util.List;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.catalina.Session;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.io.ObjectInputStream;
import org.apache.catalina.Context;
import org.apache.catalina.Loader;
import java.io.File;
import java.io.FileNotFoundException;
import org.apache.catalina.Manager;
import org.apache.catalina.util.CustomObjectInputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.security.PrivilegedActionException;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.security.AccessController;
import org.apache.catalina.security.SecurityUtil;
import org.apache.juli.logging.LogFactory;
import org.apache.juli.logging.Log;

public class StandardManager extends ManagerBase
{
    private final Log log;
    protected static final String name = "StandardManager";
    protected String pathname;
    
    public StandardManager() {
        this.log = LogFactory.getLog((Class)StandardManager.class);
        this.pathname = "SESSIONS.ser";
    }
    
    @Override
    public String getName() {
        return "StandardManager";
    }
    
    public String getPathname() {
        return this.pathname;
    }
    
    public void setPathname(final String pathname) {
        final String oldPathname = this.pathname;
        this.pathname = pathname;
        this.support.firePropertyChange("pathname", oldPathname, this.pathname);
    }
    
    @Override
    public void load() throws ClassNotFoundException, IOException {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedDoLoad());
            }
            catch (final PrivilegedActionException ex) {
                final Exception exception = ex.getException();
                if (exception instanceof ClassNotFoundException) {
                    throw (ClassNotFoundException)exception;
                }
                if (exception instanceof IOException) {
                    throw (IOException)exception;
                }
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)"Unreported exception in load() ", (Throwable)exception);
                }
            }
        }
        else {
            this.doLoad();
        }
    }
    
    protected void doLoad() throws ClassNotFoundException, IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)"Start: Loading persisted sessions");
        }
        this.sessions.clear();
        final File file = this.file();
        if (file == null) {
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)StandardManager.sm.getString("standardManager.loading", new Object[] { this.pathname }));
        }
        Loader loader = null;
        ClassLoader classLoader = null;
        Log logger = null;
        try (final FileInputStream fis = new FileInputStream(file.getAbsolutePath());
             final BufferedInputStream bis = new BufferedInputStream(fis)) {
            final Context c = this.getContext();
            loader = c.getLoader();
            logger = c.getLogger();
            if (loader != null) {
                classLoader = loader.getClassLoader();
            }
            if (classLoader == null) {
                classLoader = this.getClass().getClassLoader();
            }
            synchronized (this.sessions) {
                try (final ObjectInputStream ois = new CustomObjectInputStream(bis, classLoader, logger, this.getSessionAttributeValueClassNamePattern(), this.getWarnOnSessionAttributeFilterFailure())) {
                    final Integer count = (Integer)ois.readObject();
                    final int n = count;
                    if (this.log.isDebugEnabled()) {
                        this.log.debug((Object)("Loading " + n + " persisted sessions"));
                    }
                    for (int i = 0; i < n; ++i) {
                        final StandardSession session = this.getNewSession();
                        session.readObjectData(ois);
                        session.setManager(this);
                        this.sessions.put(session.getIdInternal(), session);
                        session.activate();
                        if (!session.isValidInternal()) {
                            session.setValid(true);
                            session.expire();
                        }
                        ++this.sessionCounter;
                    }
                }
                finally {
                    if (file.exists() && !file.delete()) {
                        this.log.warn((Object)StandardManager.sm.getString("standardManager.deletePersistedFileFail", new Object[] { file }));
                    }
                }
            }
        }
        catch (final FileNotFoundException e) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)"No persisted data file found");
            }
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)"Finish: Loading persisted sessions");
        }
    }
    
    @Override
    public void unload() throws IOException {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedDoUnload());
            }
            catch (final PrivilegedActionException ex) {
                final Exception exception = ex.getException();
                if (exception instanceof IOException) {
                    throw (IOException)exception;
                }
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)"Unreported exception in unLoad()", (Throwable)exception);
                }
            }
        }
        else {
            this.doUnload();
        }
    }
    
    protected void doUnload() throws IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)StandardManager.sm.getString("standardManager.unloading.debug"));
        }
        if (this.sessions.isEmpty()) {
            this.log.debug((Object)StandardManager.sm.getString("standardManager.unloading.nosessions"));
            return;
        }
        final File file = this.file();
        if (file == null) {
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)StandardManager.sm.getString("standardManager.unloading", new Object[] { this.pathname }));
        }
        final List<StandardSession> list = new ArrayList<StandardSession>();
        try (final FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
             final BufferedOutputStream bos = new BufferedOutputStream(fos);
             final ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            synchronized (this.sessions) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)("Unloading " + this.sessions.size() + " sessions"));
                }
                oos.writeObject(this.sessions.size());
                for (final Session s : this.sessions.values()) {
                    final StandardSession session = (StandardSession)s;
                    list.add(session);
                    session.passivate();
                    session.writeObjectData(oos);
                }
            }
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Expiring " + list.size() + " persisted sessions"));
        }
        for (final StandardSession session2 : list) {
            try {
                session2.expire(false);
            }
            catch (final Throwable t) {
                ExceptionUtils.handleThrowable(t);
            }
            finally {
                session2.recycle();
            }
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)"Unloading complete");
        }
    }
    
    @Override
    protected synchronized void startInternal() throws LifecycleException {
        super.startInternal();
        try {
            this.load();
        }
        catch (final Throwable t) {
            ExceptionUtils.handleThrowable(t);
            this.log.error((Object)StandardManager.sm.getString("standardManager.managerLoad"), t);
        }
        this.setState(LifecycleState.STARTING);
    }
    
    @Override
    protected synchronized void stopInternal() throws LifecycleException {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)"Stopping");
        }
        this.setState(LifecycleState.STOPPING);
        try {
            this.unload();
        }
        catch (final Throwable t) {
            ExceptionUtils.handleThrowable(t);
            this.log.error((Object)StandardManager.sm.getString("standardManager.managerUnload"), t);
        }
        final Session[] arr$;
        final Session[] sessions = arr$ = this.findSessions();
        for (final Session session : arr$) {
            try {
                if (session.isValid()) {
                    session.expire();
                }
            }
            catch (final Throwable t2) {
                ExceptionUtils.handleThrowable(t2);
            }
            finally {
                session.recycle();
            }
        }
        super.stopInternal();
    }
    
    protected File file() {
        if (this.pathname == null || this.pathname.length() == 0) {
            return null;
        }
        File file = new File(this.pathname);
        if (!file.isAbsolute()) {
            final Context context = this.getContext();
            final ServletContext servletContext = context.getServletContext();
            final File tempdir = (File)servletContext.getAttribute("javax.servlet.context.tempdir");
            if (tempdir != null) {
                file = new File(tempdir, this.pathname);
            }
        }
        return file;
    }
    
    private class PrivilegedDoLoad implements PrivilegedExceptionAction<Void>
    {
        PrivilegedDoLoad() {
        }
        
        @Override
        public Void run() throws Exception {
            StandardManager.this.doLoad();
            return null;
        }
    }
    
    private class PrivilegedDoUnload implements PrivilegedExceptionAction<Void>
    {
        PrivilegedDoUnload() {
        }
        
        @Override
        public Void run() throws Exception {
            StandardManager.this.doUnload();
            return null;
        }
    }
}
