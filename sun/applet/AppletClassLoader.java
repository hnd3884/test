package sun.applet;

import java.security.PrivilegedAction;
import java.util.NoSuchElementException;
import java.util.Enumeration;
import java.net.URLConnection;
import java.io.EOFException;
import java.io.InputStream;
import sun.misc.IOUtils;
import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.security.Permission;
import java.io.File;
import java.io.FilePermission;
import java.security.PermissionCollection;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import sun.net.www.ParseUtil;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.AccessController;
import java.security.cert.Certificate;
import java.util.HashMap;
import sun.awt.AppContext;
import java.security.AccessControlContext;
import java.security.CodeSource;
import java.net.URL;
import java.net.URLClassLoader;

public class AppletClassLoader extends URLClassLoader
{
    private URL base;
    private CodeSource codesource;
    private AccessControlContext acc;
    private boolean exceptionStatus;
    private final Object threadGroupSynchronizer;
    private final Object grabReleaseSynchronizer;
    private boolean codebaseLookup;
    private volatile boolean allowRecursiveDirectoryRead;
    private Object syncResourceAsStream;
    private Object syncResourceAsStreamFromJar;
    private boolean resourceAsStreamInCall;
    private boolean resourceAsStreamFromJarInCall;
    private AppletThreadGroup threadGroup;
    private AppContext appContext;
    int usageCount;
    private HashMap jdk11AppletInfo;
    private HashMap jdk12AppletInfo;
    private static AppletMessageHandler mh;
    
    protected AppletClassLoader(final URL base) {
        super(new URL[0]);
        this.exceptionStatus = false;
        this.threadGroupSynchronizer = new Object();
        this.grabReleaseSynchronizer = new Object();
        this.codebaseLookup = true;
        this.allowRecursiveDirectoryRead = true;
        this.syncResourceAsStream = new Object();
        this.syncResourceAsStreamFromJar = new Object();
        this.resourceAsStreamInCall = false;
        this.resourceAsStreamFromJarInCall = false;
        this.usageCount = 0;
        this.jdk11AppletInfo = new HashMap();
        this.jdk12AppletInfo = new HashMap();
        this.base = base;
        this.codesource = new CodeSource(base, (Certificate[])null);
        this.acc = AccessController.getContext();
    }
    
    public void disableRecursiveDirectoryRead() {
        this.allowRecursiveDirectoryRead = false;
    }
    
    void setCodebaseLookup(final boolean codebaseLookup) {
        this.codebaseLookup = codebaseLookup;
    }
    
    URL getBaseURL() {
        return this.base;
    }
    
    @Override
    public URL[] getURLs() {
        final URL[] urLs = super.getURLs();
        final URL[] array = new URL[urLs.length + 1];
        System.arraycopy(urLs, 0, array, 0, urLs.length);
        array[array.length - 1] = this.base;
        return array;
    }
    
    protected void addJar(final String s) throws IOException {
        URL url;
        try {
            url = new URL(this.base, s);
        }
        catch (final MalformedURLException ex) {
            throw new IllegalArgumentException("name");
        }
        this.addURL(url);
    }
    
    public synchronized Class loadClass(final String s, final boolean b) throws ClassNotFoundException {
        final int lastIndex = s.lastIndexOf(46);
        if (lastIndex != -1) {
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                securityManager.checkPackageAccess(s.substring(0, lastIndex));
            }
        }
        try {
            return super.loadClass(s, b);
        }
        catch (final ClassNotFoundException ex) {
            throw ex;
        }
        catch (final RuntimeException ex2) {
            throw ex2;
        }
        catch (final Error error) {
            throw error;
        }
    }
    
    @Override
    protected Class findClass(String substring) throws ClassNotFoundException {
        final int index = substring.indexOf(";");
        String substring2 = "";
        if (index != -1) {
            substring2 = substring.substring(index, substring.length());
            substring = substring.substring(0, index);
        }
        try {
            return super.findClass(substring);
        }
        catch (final ClassNotFoundException ex) {
            if (!this.codebaseLookup) {
                throw new ClassNotFoundException(substring);
            }
            final String string = new StringBuffer(ParseUtil.encodePath(substring.replace('.', '/'), false)).append(".class").append(substring2).toString();
            try {
                final byte[] array = AccessController.doPrivileged((PrivilegedExceptionAction<byte[]>)new PrivilegedExceptionAction() {
                    @Override
                    public Object run() throws IOException {
                        try {
                            final URL url = new URL(AppletClassLoader.this.base, string);
                            if (AppletClassLoader.this.base.getProtocol().equals(url.getProtocol()) && AppletClassLoader.this.base.getHost().equals(url.getHost()) && AppletClassLoader.this.base.getPort() == url.getPort()) {
                                return getBytes(url);
                            }
                            return null;
                        }
                        catch (final Exception ex) {
                            return null;
                        }
                    }
                }, this.acc);
                if (array != null) {
                    return this.defineClass(substring, array, 0, array.length, this.codesource);
                }
                throw new ClassNotFoundException(substring);
            }
            catch (final PrivilegedActionException ex2) {
                throw new ClassNotFoundException(substring, ex2.getException());
            }
        }
    }
    
    @Override
    protected PermissionCollection getPermissions(final CodeSource codeSource) {
        final PermissionCollection permissions = super.getPermissions(codeSource);
        final URL location = codeSource.getLocation();
        String s = null;
        Permission permission;
        try {
            permission = location.openConnection().getPermission();
        }
        catch (final IOException ex) {
            permission = null;
        }
        if (permission instanceof FilePermission) {
            s = permission.getName();
        }
        else if (permission == null && location.getProtocol().equals("file")) {
            s = ParseUtil.decode(location.getFile().replace('/', File.separatorChar));
        }
        if (s != null) {
            final String s2 = s;
            if (!s.endsWith(File.separator)) {
                final int lastIndex = s.lastIndexOf(File.separatorChar);
                if (lastIndex != -1) {
                    permissions.add(new FilePermission(s.substring(0, lastIndex + 1) + "-", "read"));
                }
            }
            final boolean directory = new File(s2).isDirectory();
            if (this.allowRecursiveDirectoryRead) {
                if (!directory && !s2.toLowerCase().endsWith(".jar")) {
                    if (!s2.toLowerCase().endsWith(".zip")) {
                        return permissions;
                    }
                }
                Permission permission2;
                try {
                    permission2 = this.base.openConnection().getPermission();
                }
                catch (final IOException ex2) {
                    permission2 = null;
                }
                if (permission2 instanceof FilePermission) {
                    String s3 = permission2.getName();
                    if (s3.endsWith(File.separator)) {
                        s3 += "-";
                    }
                    permissions.add(new FilePermission(s3, "read"));
                }
                else if (permission2 == null && this.base.getProtocol().equals("file")) {
                    String s4 = ParseUtil.decode(this.base.getFile().replace('/', File.separatorChar));
                    if (s4.endsWith(File.separator)) {
                        s4 += "-";
                    }
                    permissions.add(new FilePermission(s4, "read"));
                }
            }
        }
        return permissions;
    }
    
    private static byte[] getBytes(final URL url) throws IOException {
        final URLConnection openConnection = url.openConnection();
        if (openConnection instanceof HttpURLConnection && ((HttpURLConnection)openConnection).getResponseCode() >= 400) {
            throw new IOException("open HTTP connection failed.");
        }
        final int contentLength = openConnection.getContentLength();
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(openConnection.getInputStream());
        byte[] allBytes;
        try {
            allBytes = IOUtils.readAllBytes(bufferedInputStream);
            if (contentLength != -1 && allBytes.length != contentLength) {
                throw new EOFException("Expected:" + contentLength + ", read:" + allBytes.length);
            }
        }
        finally {
            bufferedInputStream.close();
        }
        return allBytes;
    }
    
    @Override
    public InputStream getResourceAsStream(final String s) {
        if (s == null) {
            throw new NullPointerException("name");
        }
        try {
            InputStream inputStream = null;
            synchronized (this.syncResourceAsStream) {
                this.resourceAsStreamInCall = true;
                inputStream = super.getResourceAsStream(s);
                this.resourceAsStreamInCall = false;
            }
            if (this.codebaseLookup && inputStream == null) {
                inputStream = new URL(this.base, ParseUtil.encodePath(s, false)).openStream();
            }
            return inputStream;
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    public InputStream getResourceAsStreamFromJar(final String s) {
        if (s == null) {
            throw new NullPointerException("name");
        }
        try {
            InputStream resourceAsStream = null;
            synchronized (this.syncResourceAsStreamFromJar) {
                this.resourceAsStreamFromJarInCall = true;
                resourceAsStream = super.getResourceAsStream(s);
                this.resourceAsStreamFromJarInCall = false;
            }
            return resourceAsStream;
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    @Override
    public URL findResource(final String s) {
        URL resource = super.findResource(s);
        if (s.startsWith("META-INF/")) {
            return resource;
        }
        if (!this.codebaseLookup) {
            return resource;
        }
        if (resource == null) {
            boolean resourceAsStreamFromJarInCall = false;
            synchronized (this.syncResourceAsStreamFromJar) {
                resourceAsStreamFromJarInCall = this.resourceAsStreamFromJarInCall;
            }
            if (resourceAsStreamFromJarInCall) {
                return null;
            }
            boolean resourceAsStreamInCall = false;
            synchronized (this.syncResourceAsStream) {
                resourceAsStreamInCall = this.resourceAsStreamInCall;
            }
            if (!resourceAsStreamInCall) {
                try {
                    resource = new URL(this.base, ParseUtil.encodePath(s, false));
                    if (!this.resourceExists(resource)) {
                        resource = null;
                    }
                }
                catch (final Exception ex) {
                    resource = null;
                }
            }
        }
        return resource;
    }
    
    private boolean resourceExists(final URL url) {
        boolean b = true;
        try {
            final URLConnection openConnection = url.openConnection();
            if (openConnection instanceof HttpURLConnection) {
                final HttpURLConnection httpURLConnection = (HttpURLConnection)openConnection;
                httpURLConnection.setRequestMethod("HEAD");
                final int responseCode = httpURLConnection.getResponseCode();
                if (responseCode == 200) {
                    return true;
                }
                if (responseCode >= 400) {
                    return false;
                }
            }
            else {
                openConnection.getInputStream().close();
            }
        }
        catch (final Exception ex) {
            b = false;
        }
        return b;
    }
    
    @Override
    public Enumeration findResources(final String s) throws IOException {
        final Enumeration<URL> resources = super.findResources(s);
        if (s.startsWith("META-INF/")) {
            return resources;
        }
        if (!this.codebaseLookup) {
            return resources;
        }
        URL url = new URL(this.base, ParseUtil.encodePath(s, false));
        if (!this.resourceExists(url)) {
            url = null;
        }
        return new Enumeration() {
            private boolean done;
            
            @Override
            public Object nextElement() {
                if (!this.done) {
                    if (resources.hasMoreElements()) {
                        return resources.nextElement();
                    }
                    this.done = true;
                    if (url != null) {
                        return url;
                    }
                }
                throw new NoSuchElementException();
            }
            
            @Override
            public boolean hasMoreElements() {
                return !this.done && (resources.hasMoreElements() || url != null);
            }
        };
    }
    
    Class loadCode(String s) throws ClassNotFoundException {
        s = s.replace('/', '.');
        s = s.replace(File.separatorChar, '.');
        String substring = null;
        final int index = s.indexOf(";");
        if (index != -1) {
            substring = s.substring(index, s.length());
            s = s.substring(0, index);
        }
        String string = s;
        if (s.endsWith(".class") || s.endsWith(".java")) {
            s = s.substring(0, s.lastIndexOf(46));
        }
        try {
            if (substring != null) {
                s = new StringBuffer(s).append(substring).toString();
            }
            return this.loadClass(s);
        }
        catch (final ClassNotFoundException ex) {
            if (substring != null) {
                string = new StringBuffer(string).append(substring).toString();
            }
            return this.loadClass(string);
        }
    }
    
    public ThreadGroup getThreadGroup() {
        synchronized (this.threadGroupSynchronizer) {
            if (this.threadGroup == null || this.threadGroup.isDestroyed()) {
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                    @Override
                    public Object run() {
                        AppletClassLoader.this.threadGroup = new AppletThreadGroup(AppletClassLoader.this.base + "-threadGroup");
                        final AppContextCreator appContextCreator = new AppContextCreator(AppletClassLoader.this.threadGroup);
                        appContextCreator.setContextClassLoader(AppletClassLoader.this);
                        appContextCreator.start();
                        try {
                            synchronized (appContextCreator.syncObject) {
                                while (!appContextCreator.created) {
                                    appContextCreator.syncObject.wait();
                                }
                            }
                        }
                        catch (final InterruptedException ex) {}
                        AppletClassLoader.this.appContext = appContextCreator.appContext;
                        return null;
                    }
                });
            }
            return this.threadGroup;
        }
    }
    
    public AppContext getAppContext() {
        return this.appContext;
    }
    
    public void grab() {
        synchronized (this.grabReleaseSynchronizer) {
            ++this.usageCount;
        }
        this.getThreadGroup();
    }
    
    protected void setExceptionStatus() {
        this.exceptionStatus = true;
    }
    
    public boolean getExceptionStatus() {
        return this.exceptionStatus;
    }
    
    protected void release() {
        AppContext resetAppContext = null;
        synchronized (this.grabReleaseSynchronizer) {
            if (this.usageCount > 1) {
                --this.usageCount;
            }
            else {
                synchronized (this.threadGroupSynchronizer) {
                    resetAppContext = this.resetAppContext();
                }
            }
        }
        if (resetAppContext != null) {
            try {
                resetAppContext.dispose();
            }
            catch (final IllegalThreadStateException ex) {}
        }
    }
    
    protected AppContext resetAppContext() {
        AppContext appContext = null;
        synchronized (this.threadGroupSynchronizer) {
            appContext = this.appContext;
            this.usageCount = 0;
            this.appContext = null;
            this.threadGroup = null;
        }
        return appContext;
    }
    
    void setJDK11Target(final Class clazz, final boolean b) {
        this.jdk11AppletInfo.put(clazz.toString(), b);
    }
    
    void setJDK12Target(final Class clazz, final boolean b) {
        this.jdk12AppletInfo.put(clazz.toString(), b);
    }
    
    Boolean isJDK11Target(final Class clazz) {
        return this.jdk11AppletInfo.get(clazz.toString());
    }
    
    Boolean isJDK12Target(final Class clazz) {
        return this.jdk12AppletInfo.get(clazz.toString());
    }
    
    private static void printError(final String s, final Throwable t) {
        String s2 = null;
        if (t == null) {
            s2 = AppletClassLoader.mh.getMessage("filenotfound", s);
        }
        else if (t instanceof IOException) {
            s2 = AppletClassLoader.mh.getMessage("fileioexception", s);
        }
        else if (t instanceof ClassFormatError) {
            s2 = AppletClassLoader.mh.getMessage("fileformat", s);
        }
        else if (t instanceof ThreadDeath) {
            s2 = AppletClassLoader.mh.getMessage("filedeath", s);
        }
        else if (t instanceof Error) {
            s2 = AppletClassLoader.mh.getMessage("fileerror", t.toString(), s);
        }
        if (s2 != null) {
            System.err.println(s2);
        }
    }
    
    static {
        AppletClassLoader.mh = new AppletMessageHandler("appletclassloader");
    }
}
