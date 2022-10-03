package sun.applet;

import java.util.Iterator;
import java.lang.ref.WeakReference;
import java.util.Vector;
import sun.awt.SunToolkit;
import java.awt.Frame;
import java.net.URLConnection;
import java.security.ProtectionDomain;
import java.net.SocketPermission;
import java.net.JarURLConnection;
import java.io.File;
import java.io.FilePermission;
import sun.security.util.SecurityConstants;
import java.security.Permissions;
import java.security.CodeSource;
import java.security.cert.Certificate;
import java.security.Policy;
import java.security.PermissionCollection;
import java.security.AccessControlContext;
import java.net.URL;
import java.awt.AWTEvent;
import java.awt.event.InvocationEvent;
import java.awt.Toolkit;
import sun.awt.AppContext;
import java.util.StringTokenizer;
import java.io.IOException;
import java.io.InputStream;
import java.awt.Container;
import sun.awt.EmbeddedFrame;
import java.lang.reflect.AccessibleObject;
import java.awt.KeyboardFocusManager;
import java.lang.reflect.Method;
import java.awt.Window;
import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import sun.awt.AWTAccessor;
import java.awt.Font;
import java.util.Locale;
import sun.misc.PerformanceLogger;
import java.security.Permission;
import java.security.AccessControlException;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.awt.EventQueue;
import sun.misc.Queue;
import sun.misc.MessageUtils;
import java.awt.Dimension;
import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.Panel;

public abstract class AppletPanel extends Panel implements AppletStub, Runnable
{
    Applet applet;
    protected boolean doInit;
    protected AppletClassLoader loader;
    public static final int APPLET_DISPOSE = 0;
    public static final int APPLET_LOAD = 1;
    public static final int APPLET_INIT = 2;
    public static final int APPLET_START = 3;
    public static final int APPLET_STOP = 4;
    public static final int APPLET_DESTROY = 5;
    public static final int APPLET_QUIT = 6;
    public static final int APPLET_ERROR = 7;
    public static final int APPLET_RESIZE = 51234;
    public static final int APPLET_LOADING = 51235;
    public static final int APPLET_LOADING_COMPLETED = 51236;
    protected int status;
    protected Thread handler;
    Dimension defaultAppletSize;
    Dimension currentAppletSize;
    MessageUtils mu;
    Thread loaderThread;
    boolean loadAbortRequest;
    private static int threadGroupNumber;
    private AppletListener listeners;
    private Queue queue;
    private EventQueue appEvtQ;
    private static HashMap classloaders;
    private boolean jdk11Applet;
    private boolean jdk12Applet;
    private static AppletMessageHandler amh;
    
    public AppletPanel() {
        this.doInit = true;
        this.defaultAppletSize = new Dimension(10, 10);
        this.currentAppletSize = new Dimension(10, 10);
        this.mu = new MessageUtils();
        this.loaderThread = null;
        this.loadAbortRequest = false;
        this.queue = null;
        this.appEvtQ = null;
        this.jdk11Applet = false;
        this.jdk12Applet = false;
    }
    
    protected abstract String getCode();
    
    protected abstract String getJarFiles();
    
    protected abstract String getSerializedObject();
    
    @Override
    public abstract int getWidth();
    
    @Override
    public abstract int getHeight();
    
    public abstract boolean hasInitialFocus();
    
    protected void setupAppletAppContext() {
    }
    
    synchronized void createAppletThread() {
        final String string = "applet-" + this.getCode();
        (this.loader = this.getClassLoader(this.getCodeBase(), this.getClassLoaderCacheKey())).grab();
        final String parameter = this.getParameter("codebase_lookup");
        if (parameter != null && parameter.equals("false")) {
            this.loader.setCodebaseLookup(false);
        }
        else {
            this.loader.setCodebaseLookup(true);
        }
        this.handler = new Thread(this.loader.getThreadGroup(), this, "thread " + string);
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            @Override
            public Object run() {
                AppletPanel.this.handler.setContextClassLoader(AppletPanel.this.loader);
                return null;
            }
        });
        this.handler.start();
    }
    
    void joinAppletThread() throws InterruptedException {
        if (this.handler != null) {
            this.handler.join();
            this.handler = null;
        }
    }
    
    void release() {
        if (this.loader != null) {
            this.loader.release();
            this.loader = null;
        }
    }
    
    public void init() {
        try {
            this.defaultAppletSize.width = this.getWidth();
            this.currentAppletSize.width = this.defaultAppletSize.width;
            this.defaultAppletSize.height = this.getHeight();
            this.currentAppletSize.height = this.defaultAppletSize.height;
        }
        catch (final NumberFormatException ex) {
            this.status = 7;
            this.showAppletStatus("badattribute.exception");
            this.showAppletLog("badattribute.exception");
            this.showAppletException(ex);
        }
        this.setLayout(new BorderLayout());
        this.createAppletThread();
    }
    
    @Override
    public Dimension minimumSize() {
        return new Dimension(this.defaultAppletSize.width, this.defaultAppletSize.height);
    }
    
    @Override
    public Dimension preferredSize() {
        return new Dimension(this.currentAppletSize.width, this.currentAppletSize.height);
    }
    
    public synchronized void addAppletListener(final AppletListener appletListener) {
        this.listeners = AppletEventMulticaster.add(this.listeners, appletListener);
    }
    
    public synchronized void removeAppletListener(final AppletListener appletListener) {
        this.listeners = AppletEventMulticaster.remove(this.listeners, appletListener);
    }
    
    public void dispatchAppletEvent(final int n, final Object o) {
        if (this.listeners != null) {
            this.listeners.appletStateChanged(new AppletEvent(this, n, o));
        }
    }
    
    public void sendEvent(final int n) {
        synchronized (this) {
            if (this.queue == null) {
                this.queue = new Queue();
            }
            this.queue.enqueue(n);
            this.notifyAll();
        }
        if (n == 6) {
            try {
                this.joinAppletThread();
            }
            catch (final InterruptedException ex) {}
            if (this.loader == null) {
                this.loader = this.getClassLoader(this.getCodeBase(), this.getClassLoaderCacheKey());
            }
            this.release();
        }
    }
    
    synchronized AppletEvent getNextEvent() throws InterruptedException {
        while (this.queue == null || this.queue.isEmpty()) {
            this.wait();
        }
        return new AppletEvent(this, this.queue.dequeue(), null);
    }
    
    boolean emptyEventQueue() {
        return this.queue == null || this.queue.isEmpty();
    }
    
    private void setExceptionStatus(final AccessControlException ex) {
        final Permission permission = ex.getPermission();
        if (permission instanceof RuntimePermission && permission.getName().startsWith("modifyThread")) {
            if (this.loader == null) {
                this.loader = this.getClassLoader(this.getCodeBase(), this.getClassLoaderCacheKey());
            }
            this.loader.setExceptionStatus();
        }
    }
    
    @Override
    public void run() {
        final Thread currentThread = Thread.currentThread();
        if (currentThread == this.loaderThread) {
            this.runLoader();
            return;
        }
        int n = 0;
        while (n == 0 && !currentThread.isInterrupted()) {
            AppletEvent nextEvent;
            try {
                nextEvent = this.getNextEvent();
            }
            catch (final InterruptedException ex) {
                this.showAppletStatus("bail");
                return;
            }
            try {
                switch (nextEvent.getID()) {
                    case 1: {
                        if (!this.okToLoad()) {
                            break;
                        }
                        if (this.loaderThread == null) {
                            this.setLoaderThread(new Thread(this));
                            this.loaderThread.start();
                            this.loaderThread.join();
                            this.setLoaderThread(null);
                            break;
                        }
                        break;
                    }
                    case 2: {
                        if (this.status != 1 && this.status != 5) {
                            this.showAppletStatus("notloaded");
                            break;
                        }
                        this.applet.resize(this.defaultAppletSize);
                        if (this.doInit) {
                            if (PerformanceLogger.loggingEnabled()) {
                                PerformanceLogger.setTime("Applet Init");
                                PerformanceLogger.outputLog();
                            }
                            this.applet.init();
                        }
                        final Font font = this.getFont();
                        if (font == null || ("dialog".equals(font.getFamily().toLowerCase(Locale.ENGLISH)) && font.getSize() == 12 && font.getStyle() == 0)) {
                            this.setFont(new Font("Dialog", 0, 12));
                        }
                        this.doInit = true;
                        try {
                            AWTAccessor.getEventQueueAccessor().invokeAndWait(this.applet, new Runnable() {
                                @Override
                                public void run() {
                                    AppletPanel.this.validate();
                                }
                            });
                        }
                        catch (final InterruptedException ex2) {}
                        catch (final InvocationTargetException ex3) {}
                        this.status = 2;
                        this.showAppletStatus("inited");
                        break;
                    }
                    case 3: {
                        if (this.status != 2 && this.status != 4) {
                            this.showAppletStatus("notinited");
                            break;
                        }
                        this.applet.resize(this.currentAppletSize);
                        this.applet.start();
                        try {
                            AWTAccessor.getEventQueueAccessor().invokeAndWait(this.applet, new Runnable() {
                                final /* synthetic */ Applet val$a = AppletPanel.this.applet;
                                
                                @Override
                                public void run() {
                                    AppletPanel.this.validate();
                                    this.val$a.setVisible(true);
                                    if (AppletPanel.this.hasInitialFocus()) {
                                        AppletPanel.this.setDefaultFocus();
                                    }
                                }
                            });
                        }
                        catch (final InterruptedException ex4) {}
                        catch (final InvocationTargetException ex5) {}
                        this.status = 3;
                        this.showAppletStatus("started");
                        break;
                    }
                    case 4: {
                        if (this.status != 3) {
                            this.showAppletStatus("notstarted");
                            break;
                        }
                        this.status = 4;
                        try {
                            AWTAccessor.getEventQueueAccessor().invokeAndWait(this.applet, new Runnable() {
                                final /* synthetic */ Applet val$a = AppletPanel.this.applet;
                                
                                @Override
                                public void run() {
                                    this.val$a.setVisible(false);
                                }
                            });
                        }
                        catch (final InterruptedException ex6) {}
                        catch (final InvocationTargetException ex7) {}
                        try {
                            this.applet.stop();
                        }
                        catch (final AccessControlException exceptionStatus) {
                            this.setExceptionStatus(exceptionStatus);
                            throw exceptionStatus;
                        }
                        this.showAppletStatus("stopped");
                        break;
                    }
                    case 5: {
                        if (this.status != 4 && this.status != 2) {
                            this.showAppletStatus("notstopped");
                            break;
                        }
                        this.status = 5;
                        try {
                            this.applet.destroy();
                        }
                        catch (final AccessControlException exceptionStatus2) {
                            this.setExceptionStatus(exceptionStatus2);
                            throw exceptionStatus2;
                        }
                        this.showAppletStatus("destroyed");
                        break;
                    }
                    case 0: {
                        if (this.status != 5 && this.status != 1) {
                            this.showAppletStatus("notdestroyed");
                            break;
                        }
                        this.status = 0;
                        try {
                            AWTAccessor.getEventQueueAccessor().invokeAndWait(this.applet, new Runnable() {
                                final /* synthetic */ Applet val$a = AppletPanel.this.applet;
                                
                                @Override
                                public void run() {
                                    AppletPanel.this.remove(this.val$a);
                                }
                            });
                        }
                        catch (final InterruptedException ex8) {}
                        catch (final InvocationTargetException ex9) {}
                        this.applet = null;
                        this.showAppletStatus("disposed");
                        n = 1;
                        break;
                    }
                    case 6: {
                        return;
                    }
                }
            }
            catch (final Exception ex10) {
                this.status = 7;
                if (ex10.getMessage() != null) {
                    this.showAppletStatus("exception2", ex10.getClass().getName(), ex10.getMessage());
                }
                else {
                    this.showAppletStatus("exception", ex10.getClass().getName());
                }
                this.showAppletException(ex10);
            }
            catch (final ThreadDeath threadDeath) {
                this.showAppletStatus("death");
                return;
            }
            catch (final Error error) {
                this.status = 7;
                if (error.getMessage() != null) {
                    this.showAppletStatus("error2", error.getClass().getName(), error.getMessage());
                }
                else {
                    this.showAppletStatus("error", error.getClass().getName());
                }
                this.showAppletException(error);
            }
            this.clearLoadAbortRequest();
        }
    }
    
    private Component getMostRecentFocusOwnerForWindow(final Window window) {
        final Method method = AccessController.doPrivileged((PrivilegedAction<Method>)new PrivilegedAction() {
            @Override
            public Object run() {
                AccessibleObject declaredMethod = null;
                try {
                    declaredMethod = KeyboardFocusManager.class.getDeclaredMethod("getMostRecentFocusOwner", Window.class);
                    declaredMethod.setAccessible(true);
                }
                catch (final Exception ex) {
                    ex.printStackTrace();
                }
                return declaredMethod;
            }
        });
        if (method != null) {
            try {
                return (Component)method.invoke(null, window);
            }
            catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
        return window.getMostRecentFocusOwner();
    }
    
    private void setDefaultFocus() {
        Component component = null;
        final Container parent = this.getParent();
        if (parent != null) {
            if (parent instanceof Window) {
                component = this.getMostRecentFocusOwnerForWindow((Window)parent);
                if (component == parent || component == null) {
                    component = parent.getFocusTraversalPolicy().getInitialComponent((Window)parent);
                }
            }
            else if (parent.isFocusCycleRoot()) {
                component = parent.getFocusTraversalPolicy().getDefaultComponent(parent);
            }
        }
        if (component != null) {
            if (parent instanceof EmbeddedFrame) {
                ((EmbeddedFrame)parent).synthesizeWindowActivation(true);
            }
            component.requestFocusInWindow();
        }
    }
    
    private void runLoader() {
        if (this.status != 0) {
            this.showAppletStatus("notdisposed");
            return;
        }
        this.dispatchAppletEvent(51235, null);
        this.status = 1;
        this.loader = this.getClassLoader(this.getCodeBase(), this.getClassLoaderCacheKey());
        final String code = this.getCode();
        this.setupAppletAppContext();
        try {
            this.loadJarFiles(this.loader);
            this.applet = this.createApplet(this.loader);
        }
        catch (final ClassNotFoundException ex) {
            this.status = 7;
            this.showAppletStatus("notfound", code);
            this.showAppletLog("notfound", code);
            this.showAppletException(ex);
            return;
        }
        catch (final InstantiationException ex2) {
            this.status = 7;
            this.showAppletStatus("nocreate", code);
            this.showAppletLog("nocreate", code);
            this.showAppletException(ex2);
            return;
        }
        catch (final IllegalAccessException ex3) {
            this.status = 7;
            this.showAppletStatus("noconstruct", code);
            this.showAppletLog("noconstruct", code);
            this.showAppletException(ex3);
            return;
        }
        catch (final Exception ex4) {
            this.status = 7;
            this.showAppletStatus("exception", ex4.getMessage());
            this.showAppletException(ex4);
            return;
        }
        catch (final ThreadDeath threadDeath) {
            this.status = 7;
            this.showAppletStatus("death");
            return;
        }
        catch (final Error error) {
            this.status = 7;
            this.showAppletStatus("error", error.getMessage());
            this.showAppletException(error);
            return;
        }
        finally {
            this.dispatchAppletEvent(51236, null);
        }
        if (this.applet != null) {
            this.applet.setStub(this);
            this.applet.hide();
            this.add("Center", this.applet);
            this.showAppletStatus("loaded");
            this.validate();
        }
    }
    
    protected Applet createApplet(final AppletClassLoader appletClassLoader) throws ClassNotFoundException, IllegalAccessException, IOException, InstantiationException, InterruptedException {
        final String serializedObject = this.getSerializedObject();
        final String code = this.getCode();
        if (code != null && serializedObject != null) {
            System.err.println(AppletPanel.amh.getMessage("runloader.err"));
            throw new InstantiationException("Either \"code\" or \"object\" should be specified, but not both.");
        }
        if (code == null && serializedObject == null) {
            final String s = "nocode";
            this.status = 7;
            this.showAppletStatus(s);
            this.showAppletLog(s);
            this.repaint();
        }
        if (code != null) {
            this.applet = (Applet)appletClassLoader.loadCode(code).newInstance();
            this.doInit = true;
        }
        else {
            try (final InputStream inputStream = AccessController.doPrivileged(() -> appletClassLoader2.getResourceAsStream(s2));
                 final AppletObjectInputStream appletObjectInputStream = new AppletObjectInputStream(inputStream, appletClassLoader)) {
                this.applet = (Applet)appletObjectInputStream.readObject();
                this.doInit = false;
            }
        }
        this.findAppletJDKLevel(this.applet);
        if (Thread.interrupted()) {
            try {
                this.status = 0;
                this.applet = null;
                this.showAppletStatus("death");
            }
            finally {
                Thread.currentThread().interrupt();
            }
            return null;
        }
        return this.applet;
    }
    
    protected void loadJarFiles(final AppletClassLoader appletClassLoader) throws IOException, InterruptedException {
        final String jarFiles = this.getJarFiles();
        if (jarFiles != null) {
            final StringTokenizer stringTokenizer = new StringTokenizer(jarFiles, ",", false);
            while (stringTokenizer.hasMoreTokens()) {
                final String trim = stringTokenizer.nextToken().trim();
                try {
                    appletClassLoader.addJar(trim);
                }
                catch (final IllegalArgumentException ex) {}
            }
        }
    }
    
    protected synchronized void stopLoading() {
        if (this.loaderThread != null) {
            this.loaderThread.interrupt();
        }
        else {
            this.setLoadAbortRequest();
        }
    }
    
    protected synchronized boolean okToLoad() {
        return !this.loadAbortRequest;
    }
    
    protected synchronized void clearLoadAbortRequest() {
        this.loadAbortRequest = false;
    }
    
    protected synchronized void setLoadAbortRequest() {
        this.loadAbortRequest = true;
    }
    
    private synchronized void setLoaderThread(final Thread loaderThread) {
        this.loaderThread = loaderThread;
    }
    
    @Override
    public boolean isActive() {
        return this.status == 3;
    }
    
    @Override
    public void appletResize(final int width, final int height) {
        this.currentAppletSize.width = width;
        this.currentAppletSize.height = height;
        final Dimension dimension = new Dimension(this.currentAppletSize.width, this.currentAppletSize.height);
        if (this.loader != null) {
            final AppContext appContext = this.loader.getAppContext();
            if (appContext != null) {
                this.appEvtQ = (EventQueue)appContext.get(AppContext.EVENT_QUEUE_KEY);
            }
        }
        if (this.appEvtQ != null) {
            this.appEvtQ.postEvent(new InvocationEvent(Toolkit.getDefaultToolkit(), new Runnable() {
                @Override
                public void run() {
                    if (AppletPanel.this != null) {
                        AppletPanel.this.dispatchAppletEvent(51234, dimension);
                    }
                }
            }));
        }
    }
    
    @Override
    public void setBounds(final int n, final int n2, final int width, final int height) {
        super.setBounds(n, n2, width, height);
        this.currentAppletSize.width = width;
        this.currentAppletSize.height = height;
    }
    
    public Applet getApplet() {
        return this.applet;
    }
    
    protected void showAppletStatus(final String s) {
        this.getAppletContext().showStatus(AppletPanel.amh.getMessage(s));
    }
    
    protected void showAppletStatus(final String s, final Object o) {
        this.getAppletContext().showStatus(AppletPanel.amh.getMessage(s, o));
    }
    
    protected void showAppletStatus(final String s, final Object o, final Object o2) {
        this.getAppletContext().showStatus(AppletPanel.amh.getMessage(s, o, o2));
    }
    
    protected void showAppletLog(final String s) {
        System.out.println(AppletPanel.amh.getMessage(s));
    }
    
    protected void showAppletLog(final String s, final Object o) {
        System.out.println(AppletPanel.amh.getMessage(s, o));
    }
    
    protected void showAppletException(final Throwable t) {
        t.printStackTrace();
        this.repaint();
    }
    
    public String getClassLoaderCacheKey() {
        return this.getCodeBase().toString();
    }
    
    public static synchronized void flushClassLoader(final String s) {
        AppletPanel.classloaders.remove(s);
    }
    
    public static synchronized void flushClassLoaders() {
        AppletPanel.classloaders = new HashMap();
    }
    
    protected AppletClassLoader createClassLoader(final URL url) {
        return new AppletClassLoader(url);
    }
    
    synchronized AppletClassLoader getClassLoader(final URL url, final String s) {
        AppletClassLoader appletClassLoader = AppletPanel.classloaders.get(s);
        if (appletClassLoader == null) {
            appletClassLoader = AccessController.doPrivileged((PrivilegedAction<AppletClassLoader>)new PrivilegedAction() {
                @Override
                public Object run() {
                    final AppletClassLoader classLoader = AppletPanel.this.createClassLoader(url);
                    synchronized (this.getClass()) {
                        final AppletClassLoader appletClassLoader = AppletPanel.classloaders.get(s);
                        if (appletClassLoader == null) {
                            AppletPanel.classloaders.put(s, classLoader);
                            return classLoader;
                        }
                        return appletClassLoader;
                    }
                }
            }, this.getAccessControlContext(url));
        }
        return appletClassLoader;
    }
    
    private AccessControlContext getAccessControlContext(final URL url) {
        PermissionCollection collection = AccessController.doPrivileged((PrivilegedAction<PermissionCollection>)new PrivilegedAction() {
            @Override
            public Object run() {
                final Policy policy = Policy.getPolicy();
                if (policy != null) {
                    return policy.getPermissions(new CodeSource(null, (Certificate[])null));
                }
                return null;
            }
        });
        if (collection == null) {
            collection = new Permissions();
        }
        collection.add(SecurityConstants.CREATE_CLASSLOADER_PERMISSION);
        URLConnection openConnection = null;
        Permission permission;
        try {
            openConnection = url.openConnection();
            permission = openConnection.getPermission();
        }
        catch (final IOException ex) {
            permission = null;
        }
        if (permission != null) {
            collection.add(permission);
        }
        if (permission instanceof FilePermission) {
            final String name = permission.getName();
            final int lastIndex = name.lastIndexOf(File.separatorChar);
            if (lastIndex != -1) {
                String s = name.substring(0, lastIndex + 1);
                if (s.endsWith(File.separator)) {
                    s += "-";
                }
                collection.add(new FilePermission(s, "read"));
            }
        }
        else {
            URL jarFileURL = url;
            if (openConnection instanceof JarURLConnection) {
                jarFileURL = ((JarURLConnection)openConnection).getJarFileURL();
            }
            final String host = jarFileURL.getHost();
            if (host != null && host.length() > 0) {
                collection.add(new SocketPermission(host, "connect,accept"));
            }
        }
        return new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(new CodeSource(url, (Certificate[])null), collection) });
    }
    
    public Thread getAppletHandlerThread() {
        return this.handler;
    }
    
    public int getAppletWidth() {
        return this.currentAppletSize.width;
    }
    
    public int getAppletHeight() {
        return this.currentAppletSize.height;
    }
    
    public static void changeFrameAppContext(final Frame frame, final AppContext appContext) {
        final AppContext targetToAppContext = SunToolkit.targetToAppContext(frame);
        if (targetToAppContext == appContext) {
            return;
        }
        synchronized (Window.class) {
            WeakReference weakReference = null;
            final Vector vector = (Vector)targetToAppContext.get(Window.class);
            if (vector != null) {
                for (final WeakReference weakReference2 : vector) {
                    if (weakReference2.get() == frame) {
                        weakReference = weakReference2;
                        break;
                    }
                }
                if (weakReference != null) {
                    vector.remove(weakReference);
                }
            }
            SunToolkit.insertTargetMapping(frame, appContext);
            Object o = appContext.get(Window.class);
            if (o == null) {
                o = new Vector<WeakReference>();
                appContext.put(Window.class, o);
            }
            ((Vector<WeakReference>)o).add(weakReference);
        }
    }
    
    private void findAppletJDKLevel(final Applet applet) {
        final Class<? extends Applet> class1 = applet.getClass();
        synchronized (class1) {
            final Boolean jdk11Target = this.loader.isJDK11Target(class1);
            final Boolean jdk12Target = this.loader.isJDK12Target(class1);
            if (jdk11Target != null || jdk12Target != null) {
                this.jdk11Applet = (jdk11Target != null && jdk11Target);
                this.jdk12Applet = (jdk12Target != null && jdk12Target);
                return;
            }
            new StringBuilder().append(class1.getName().replace('.', '/')).append(".class").toString();
            final byte[] array = new byte[8];
            try (final InputStream inputStream = AccessController.doPrivileged(() -> this.loader.getResourceAsStream(s))) {
                if (inputStream.read(array, 0, 8) != 8) {
                    return;
                }
            }
            catch (final IOException ex) {
                return;
            }
            final int short1 = this.readShort(array, 6);
            if (short1 < 46) {
                this.jdk11Applet = true;
            }
            else if (short1 == 46) {
                this.jdk12Applet = true;
            }
            this.loader.setJDK11Target(class1, this.jdk11Applet);
            this.loader.setJDK12Target(class1, this.jdk12Applet);
        }
    }
    
    protected boolean isJDK11Applet() {
        return this.jdk11Applet;
    }
    
    protected boolean isJDK12Applet() {
        return this.jdk12Applet;
    }
    
    private int readShort(final byte[] array, final int n) {
        return this.readByte(array[n]) << 8 | this.readByte(array[n + 1]);
    }
    
    private int readByte(final byte b) {
        return b & 0xFF;
    }
    
    static {
        AppletPanel.threadGroupNumber = 0;
        AppletPanel.classloaders = new HashMap();
        AppletPanel.amh = new AppletMessageHandler("appletpanel");
    }
}
