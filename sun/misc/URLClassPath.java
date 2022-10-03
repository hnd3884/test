package sun.misc;

import java.io.FileInputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.jar.Attributes;
import java.util.Set;
import java.util.HashSet;
import java.security.CodeSigner;
import java.security.cert.Certificate;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.security.AccessControlException;
import java.util.jar.JarEntry;
import java.io.FileNotFoundException;
import java.util.zip.ZipFile;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.jar.JarFile;
import java.io.Closeable;
import java.security.PrivilegedAction;
import sun.security.action.GetPropertyAction;
import java.security.Permission;
import java.net.URLConnection;
import java.net.JarURLConnection;
import java.net.SocketPermission;
import java.io.FilePermission;
import sun.net.www.ParseUtil;
import java.util.StringTokenizer;
import java.io.File;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import sun.net.util.URLUtil;
import java.util.NoSuchElementException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Collections;
import java.io.IOException;
import java.util.List;
import java.net.URLStreamHandlerFactory;
import java.security.AccessControlContext;
import java.net.URLStreamHandler;
import java.util.HashMap;
import java.util.Stack;
import java.net.URL;
import java.util.ArrayList;

public class URLClassPath
{
    static final String USER_AGENT_JAVA_VERSION = "UA-Java-Version";
    static final String JAVA_VERSION;
    private static final boolean DEBUG;
    private static final boolean DEBUG_LOOKUP_CACHE;
    private static final boolean DISABLE_JAR_CHECKING;
    private static final boolean DISABLE_ACC_CHECKING;
    private static final boolean DISABLE_CP_URL_CHECK;
    private static final boolean DEBUG_CP_URL_CHECK;
    private ArrayList<URL> path;
    Stack<URL> urls;
    ArrayList<Loader> loaders;
    HashMap<String, Loader> lmap;
    private URLStreamHandler jarHandler;
    private boolean closed;
    private final AccessControlContext acc;
    private static volatile boolean lookupCacheEnabled;
    private URL[] lookupCacheURLs;
    private ClassLoader lookupCacheLoader;
    
    public URLClassPath(final URL[] array, final URLStreamHandlerFactory urlStreamHandlerFactory, final AccessControlContext acc) {
        this.path = new ArrayList<URL>();
        this.urls = new Stack<URL>();
        this.loaders = new ArrayList<Loader>();
        this.lmap = new HashMap<String, Loader>();
        this.closed = false;
        for (int i = 0; i < array.length; ++i) {
            this.path.add(array[i]);
        }
        this.push(array);
        if (urlStreamHandlerFactory != null) {
            this.jarHandler = urlStreamHandlerFactory.createURLStreamHandler("jar");
        }
        if (URLClassPath.DISABLE_ACC_CHECKING) {
            this.acc = null;
        }
        else {
            this.acc = acc;
        }
    }
    
    public URLClassPath(final URL[] array) {
        this(array, null, null);
    }
    
    public URLClassPath(final URL[] array, final AccessControlContext accessControlContext) {
        this(array, null, accessControlContext);
    }
    
    public synchronized List<IOException> closeLoaders() {
        if (this.closed) {
            return Collections.emptyList();
        }
        final LinkedList list = new LinkedList();
        for (final Loader loader : this.loaders) {
            try {
                loader.close();
            }
            catch (final IOException ex) {
                list.add(ex);
            }
        }
        this.closed = true;
        return list;
    }
    
    public synchronized void addURL(final URL url) {
        if (this.closed) {
            return;
        }
        synchronized (this.urls) {
            if (url == null || this.path.contains(url)) {
                return;
            }
            this.urls.add(0, url);
            this.path.add(url);
            if (this.lookupCacheURLs != null) {
                disableAllLookupCaches();
            }
        }
    }
    
    public URL[] getURLs() {
        synchronized (this.urls) {
            return this.path.toArray(new URL[this.path.size()]);
        }
    }
    
    public URL findResource(final String s, final boolean b) {
        final int[] lookupCache = this.getLookupCache(s);
        Loader nextLoader;
        for (int n = 0; (nextLoader = this.getNextLoader(lookupCache, n)) != null; ++n) {
            final URL resource = nextLoader.findResource(s, b);
            if (resource != null) {
                return resource;
            }
        }
        return null;
    }
    
    public Resource getResource(final String s, final boolean b) {
        if (URLClassPath.DEBUG) {
            System.err.println("URLClassPath.getResource(\"" + s + "\")");
        }
        final int[] lookupCache = this.getLookupCache(s);
        Loader nextLoader;
        for (int n = 0; (nextLoader = this.getNextLoader(lookupCache, n)) != null; ++n) {
            final Resource resource = nextLoader.getResource(s, b);
            if (resource != null) {
                return resource;
            }
        }
        return null;
    }
    
    public Enumeration<URL> findResources(final String s, final boolean b) {
        return new Enumeration<URL>() {
            private int index = 0;
            private int[] cache = URLClassPath.this.getLookupCache(s);
            private URL url = null;
            
            private boolean next() {
                if (this.url != null) {
                    return true;
                }
                Loader access$100;
                while ((access$100 = URLClassPath.this.getNextLoader(this.cache, this.index++)) != null) {
                    this.url = access$100.findResource(s, b);
                    if (this.url != null) {
                        return true;
                    }
                }
                return false;
            }
            
            @Override
            public boolean hasMoreElements() {
                return this.next();
            }
            
            @Override
            public URL nextElement() {
                if (!this.next()) {
                    throw new NoSuchElementException();
                }
                final URL url = this.url;
                this.url = null;
                return url;
            }
        };
    }
    
    public Resource getResource(final String s) {
        return this.getResource(s, true);
    }
    
    public Enumeration<Resource> getResources(final String s, final boolean b) {
        return new Enumeration<Resource>() {
            private int index = 0;
            private int[] cache = URLClassPath.this.getLookupCache(s);
            private Resource res = null;
            
            private boolean next() {
                if (this.res != null) {
                    return true;
                }
                Loader access$100;
                while ((access$100 = URLClassPath.this.getNextLoader(this.cache, this.index++)) != null) {
                    this.res = access$100.getResource(s, b);
                    if (this.res != null) {
                        return true;
                    }
                }
                return false;
            }
            
            @Override
            public boolean hasMoreElements() {
                return this.next();
            }
            
            @Override
            public Resource nextElement() {
                if (!this.next()) {
                    throw new NoSuchElementException();
                }
                final Resource res = this.res;
                this.res = null;
                return res;
            }
        };
    }
    
    public Enumeration<Resource> getResources(final String s) {
        return this.getResources(s, true);
    }
    
    synchronized void initLookupCache(final ClassLoader lookupCacheLoader) {
        final URL[] lookupCacheURLs = getLookupCacheURLs(lookupCacheLoader);
        this.lookupCacheURLs = lookupCacheURLs;
        if (lookupCacheURLs != null) {
            this.lookupCacheLoader = lookupCacheLoader;
        }
        else {
            disableAllLookupCaches();
        }
    }
    
    static void disableAllLookupCaches() {
        URLClassPath.lookupCacheEnabled = false;
    }
    
    private static native URL[] getLookupCacheURLs(final ClassLoader p0);
    
    private static native int[] getLookupCacheForClassLoader(final ClassLoader p0, final String p1);
    
    private static native boolean knownToNotExist0(final ClassLoader p0, final String p1);
    
    synchronized boolean knownToNotExist(final String s) {
        return this.lookupCacheURLs != null && URLClassPath.lookupCacheEnabled && knownToNotExist0(this.lookupCacheLoader, s);
    }
    
    private synchronized int[] getLookupCache(final String s) {
        if (this.lookupCacheURLs == null || !URLClassPath.lookupCacheEnabled) {
            return null;
        }
        final int[] lookupCacheForClassLoader = getLookupCacheForClassLoader(this.lookupCacheLoader, s);
        if (lookupCacheForClassLoader != null && lookupCacheForClassLoader.length > 0) {
            final int n = lookupCacheForClassLoader[lookupCacheForClassLoader.length - 1];
            if (!this.ensureLoaderOpened(n)) {
                if (URLClassPath.DEBUG_LOOKUP_CACHE) {
                    System.out.println("Expanded loaders FAILED " + this.loaders.size() + " for maxindex=" + n);
                }
                return null;
            }
        }
        return lookupCacheForClassLoader;
    }
    
    private boolean ensureLoaderOpened(final int n) {
        if (this.loaders.size() <= n) {
            if (this.getLoader(n) == null) {
                return false;
            }
            if (!URLClassPath.lookupCacheEnabled) {
                return false;
            }
            if (URLClassPath.DEBUG_LOOKUP_CACHE) {
                System.out.println("Expanded loaders " + this.loaders.size() + " to index=" + n);
            }
        }
        return true;
    }
    
    private synchronized void validateLookupCache(final int n, final String s) {
        if (this.lookupCacheURLs != null && URLClassPath.lookupCacheEnabled) {
            if (n < this.lookupCacheURLs.length && s.equals(URLUtil.urlNoFragString(this.lookupCacheURLs[n]))) {
                return;
            }
            if (URLClassPath.DEBUG || URLClassPath.DEBUG_LOOKUP_CACHE) {
                System.out.println("WARNING: resource lookup cache invalidated for lookupCacheLoader at " + n);
            }
            disableAllLookupCaches();
        }
    }
    
    private synchronized Loader getNextLoader(final int[] array, final int n) {
        if (this.closed) {
            return null;
        }
        if (array == null) {
            return this.getLoader(n);
        }
        if (n < array.length) {
            final Loader loader = this.loaders.get(array[n]);
            if (URLClassPath.DEBUG_LOOKUP_CACHE) {
                System.out.println("HASCACHE: Loading from : " + array[n] + " = " + loader.getBaseURL());
            }
            return loader;
        }
        return null;
    }
    
    private synchronized Loader getLoader(final int n) {
        if (this.closed) {
            return null;
        }
        while (this.loaders.size() < n + 1) {
            final URL url;
            synchronized (this.urls) {
                if (this.urls.empty()) {
                    return null;
                }
                url = this.urls.pop();
            }
            final String urlNoFragString = URLUtil.urlNoFragString(url);
            if (this.lmap.containsKey(urlNoFragString)) {
                continue;
            }
            Loader loader;
            try {
                loader = this.getLoader(url);
                final URL[] classPath = loader.getClassPath();
                if (classPath != null) {
                    this.push(classPath);
                }
            }
            catch (final IOException ex) {
                continue;
            }
            catch (final SecurityException ex2) {
                if (!URLClassPath.DEBUG) {
                    continue;
                }
                System.err.println("Failed to access " + url + ", " + ex2);
                continue;
            }
            this.validateLookupCache(this.loaders.size(), urlNoFragString);
            this.loaders.add(loader);
            this.lmap.put(urlNoFragString, loader);
        }
        if (URLClassPath.DEBUG_LOOKUP_CACHE) {
            System.out.println("NOCACHE: Loading from : " + n);
        }
        return this.loaders.get(n);
    }
    
    private Loader getLoader(final URL url) throws IOException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Loader>)new PrivilegedExceptionAction<Loader>() {
                @Override
                public Loader run() throws IOException {
                    final String file = url.getFile();
                    if (file == null || !file.endsWith("/")) {
                        return new JarLoader(url, URLClassPath.this.jarHandler, URLClassPath.this.lmap, URLClassPath.this.acc);
                    }
                    if ("file".equals(url.getProtocol())) {
                        return new FileLoader(url);
                    }
                    return new Loader(url);
                }
            }, this.acc);
        }
        catch (final PrivilegedActionException ex) {
            throw (IOException)ex.getException();
        }
    }
    
    private void push(final URL[] array) {
        synchronized (this.urls) {
            for (int i = array.length - 1; i >= 0; --i) {
                this.urls.push(array[i]);
            }
        }
    }
    
    public static URL[] pathToURLs(final String s) {
        final StringTokenizer stringTokenizer = new StringTokenizer(s, File.pathSeparator);
        URL[] array = new URL[stringTokenizer.countTokens()];
        int n = 0;
        while (stringTokenizer.hasMoreTokens()) {
            File file = new File(stringTokenizer.nextToken());
            try {
                file = new File(file.getCanonicalPath());
            }
            catch (final IOException ex) {}
            try {
                array[n++] = ParseUtil.fileToEncodedURL(file);
            }
            catch (final IOException ex2) {}
        }
        if (array.length != n) {
            final URL[] array2 = new URL[n];
            System.arraycopy(array, 0, array2, 0, n);
            array = array2;
        }
        return array;
    }
    
    public URL checkURL(final URL url) {
        try {
            check(url);
        }
        catch (final Exception ex) {
            return null;
        }
        return url;
    }
    
    static void check(final URL url) throws IOException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            final URLConnection openConnection = url.openConnection();
            final Permission permission = openConnection.getPermission();
            if (permission != null) {
                try {
                    securityManager.checkPermission(permission);
                }
                catch (final SecurityException ex) {
                    if (permission instanceof FilePermission && permission.getActions().indexOf("read") != -1) {
                        securityManager.checkRead(permission.getName());
                    }
                    else {
                        if (!(permission instanceof SocketPermission) || permission.getActions().indexOf("connect") == -1) {
                            throw ex;
                        }
                        URL jarFileURL = url;
                        if (openConnection instanceof JarURLConnection) {
                            jarFileURL = ((JarURLConnection)openConnection).getJarFileURL();
                        }
                        securityManager.checkConnect(jarFileURL.getHost(), jarFileURL.getPort());
                    }
                }
            }
        }
    }
    
    static {
        JAVA_VERSION = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("java.version"));
        DEBUG = (AccessController.doPrivileged((PrivilegedAction<Object>)new GetPropertyAction("sun.misc.URLClassPath.debug")) != null);
        DEBUG_LOOKUP_CACHE = (AccessController.doPrivileged((PrivilegedAction<Object>)new GetPropertyAction("sun.misc.URLClassPath.debugLookupCache")) != null);
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.misc.URLClassPath.disableJarChecking"));
        DISABLE_JAR_CHECKING = (s != null && (s.equals("true") || s.equals("")));
        final String s2 = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jdk.net.URLClassPath.disableRestrictedPermissions"));
        DISABLE_ACC_CHECKING = (s2 != null && (s2.equals("true") || s2.equals("")));
        final String s3 = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jdk.net.URLClassPath.disableClassPathURLCheck", "true"));
        DISABLE_CP_URL_CHECK = (s3 != null && (s3.equals("true") || s3.isEmpty()));
        DEBUG_CP_URL_CHECK = "debug".equals(s3);
        URLClassPath.lookupCacheEnabled = "true".equals(VM.getSavedProperty("sun.cds.enableSharedLookupCache"));
    }
    
    private static class Loader implements Closeable
    {
        private final URL base;
        private JarFile jarfile;
        
        Loader(final URL base) {
            this.base = base;
        }
        
        URL getBaseURL() {
            return this.base;
        }
        
        URL findResource(final String s, final boolean b) {
            URL url;
            try {
                url = new URL(this.base, ParseUtil.encodePath(s, false));
            }
            catch (final MalformedURLException ex) {
                throw new IllegalArgumentException("name");
            }
            try {
                if (b) {
                    URLClassPath.check(url);
                }
                final URLConnection openConnection = url.openConnection();
                if (openConnection instanceof HttpURLConnection) {
                    final HttpURLConnection httpURLConnection = (HttpURLConnection)openConnection;
                    httpURLConnection.setRequestMethod("HEAD");
                    if (httpURLConnection.getResponseCode() >= 400) {
                        return null;
                    }
                }
                else {
                    openConnection.setUseCaches(false);
                    openConnection.getInputStream().close();
                }
                return url;
            }
            catch (final Exception ex2) {
                return null;
            }
        }
        
        Resource getResource(final String s, final boolean b) {
            URL url;
            try {
                url = new URL(this.base, ParseUtil.encodePath(s, false));
            }
            catch (final MalformedURLException ex) {
                throw new IllegalArgumentException("name");
            }
            URLConnection openConnection;
            try {
                if (b) {
                    URLClassPath.check(url);
                }
                openConnection = url.openConnection();
                openConnection.getInputStream();
                if (openConnection instanceof JarURLConnection) {
                    this.jarfile = JarLoader.checkJar(((JarURLConnection)openConnection).getJarFile());
                }
            }
            catch (final Exception ex2) {
                return null;
            }
            return new Resource() {
                @Override
                public String getName() {
                    return s;
                }
                
                @Override
                public URL getURL() {
                    return url;
                }
                
                @Override
                public URL getCodeSourceURL() {
                    return Loader.this.base;
                }
                
                @Override
                public InputStream getInputStream() throws IOException {
                    return openConnection.getInputStream();
                }
                
                @Override
                public int getContentLength() throws IOException {
                    return openConnection.getContentLength();
                }
            };
        }
        
        Resource getResource(final String s) {
            return this.getResource(s, true);
        }
        
        @Override
        public void close() throws IOException {
            if (this.jarfile != null) {
                this.jarfile.close();
            }
        }
        
        URL[] getClassPath() throws IOException {
            return null;
        }
    }
    
    static class JarLoader extends Loader
    {
        private JarFile jar;
        private final URL csu;
        private JarIndex index;
        private MetaIndex metaIndex;
        private URLStreamHandler handler;
        private final HashMap<String, Loader> lmap;
        private final AccessControlContext acc;
        private boolean closed;
        private static final JavaUtilZipFileAccess zipAccess;
        
        JarLoader(final URL csu, final URLStreamHandler handler, final HashMap<String, Loader> lmap, final AccessControlContext acc) throws IOException {
            super(new URL("jar", "", -1, csu + "!/", handler));
            this.closed = false;
            this.csu = csu;
            this.handler = handler;
            this.lmap = lmap;
            this.acc = acc;
            if (!this.isOptimizable(csu)) {
                this.ensureOpen();
            }
            else {
                final String file = csu.getFile();
                if (file != null) {
                    final File file2 = new File(ParseUtil.decode(file));
                    this.metaIndex = MetaIndex.forJar(file2);
                    if (this.metaIndex != null && !file2.exists()) {
                        this.metaIndex = null;
                    }
                }
                if (this.metaIndex == null) {
                    this.ensureOpen();
                }
            }
        }
        
        @Override
        public void close() throws IOException {
            if (!this.closed) {
                this.closed = true;
                this.ensureOpen();
                this.jar.close();
            }
        }
        
        JarFile getJarFile() {
            return this.jar;
        }
        
        private boolean isOptimizable(final URL url) {
            return "file".equals(url.getProtocol());
        }
        
        private void ensureOpen() throws IOException {
            if (this.jar == null) {
                try {
                    AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                        @Override
                        public Void run() throws IOException {
                            if (URLClassPath.DEBUG) {
                                System.err.println("Opening " + JarLoader.this.csu);
                                Thread.dumpStack();
                            }
                            JarLoader.this.jar = JarLoader.this.getJarFile(JarLoader.this.csu);
                            JarLoader.this.index = JarIndex.getJarIndex(JarLoader.this.jar, JarLoader.this.metaIndex);
                            if (JarLoader.this.index != null) {
                                final String[] jarFiles = JarLoader.this.index.getJarFiles();
                                for (int i = 0; i < jarFiles.length; ++i) {
                                    try {
                                        final String urlNoFragString = URLUtil.urlNoFragString(new URL(JarLoader.this.csu, jarFiles[i]));
                                        if (!JarLoader.this.lmap.containsKey(urlNoFragString)) {
                                            JarLoader.this.lmap.put(urlNoFragString, null);
                                        }
                                    }
                                    catch (final MalformedURLException ex) {}
                                }
                            }
                            return null;
                        }
                    }, this.acc);
                }
                catch (final PrivilegedActionException ex) {
                    throw (IOException)ex.getException();
                }
            }
        }
        
        static JarFile checkJar(final JarFile jarFile) throws IOException {
            if (System.getSecurityManager() != null && !URLClassPath.DISABLE_JAR_CHECKING && !JarLoader.zipAccess.startsWithLocHeader(jarFile)) {
                final IOException ex = new IOException("Invalid Jar file");
                try {
                    jarFile.close();
                }
                catch (final IOException ex2) {
                    ex.addSuppressed(ex2);
                }
                throw ex;
            }
            return jarFile;
        }
        
        private JarFile getJarFile(final URL url) throws IOException {
            if (!this.isOptimizable(url)) {
                final URLConnection openConnection = this.getBaseURL().openConnection();
                openConnection.setRequestProperty("UA-Java-Version", URLClassPath.JAVA_VERSION);
                return checkJar(((JarURLConnection)openConnection).getJarFile());
            }
            final FileURLMapper fileURLMapper = new FileURLMapper(url);
            if (!fileURLMapper.exists()) {
                throw new FileNotFoundException(fileURLMapper.getPath());
            }
            return checkJar(new JarFile(fileURLMapper.getPath()));
        }
        
        JarIndex getIndex() {
            try {
                this.ensureOpen();
            }
            catch (final IOException ex) {
                throw new InternalError(ex);
            }
            return this.index;
        }
        
        Resource checkResource(final String s, final boolean b, final JarEntry jarEntry) {
            URL url;
            try {
                url = new URL(this.getBaseURL(), ParseUtil.encodePath(s, false));
                if (b) {
                    URLClassPath.check(url);
                }
            }
            catch (final MalformedURLException ex) {
                return null;
            }
            catch (final IOException ex2) {
                return null;
            }
            catch (final AccessControlException ex3) {
                return null;
            }
            return new Resource() {
                @Override
                public String getName() {
                    return s;
                }
                
                @Override
                public URL getURL() {
                    return url;
                }
                
                @Override
                public URL getCodeSourceURL() {
                    return JarLoader.this.csu;
                }
                
                @Override
                public InputStream getInputStream() throws IOException {
                    return JarLoader.this.jar.getInputStream(jarEntry);
                }
                
                @Override
                public int getContentLength() {
                    return (int)jarEntry.getSize();
                }
                
                @Override
                public Manifest getManifest() throws IOException {
                    SharedSecrets.javaUtilJarAccess().ensureInitialization(JarLoader.this.jar);
                    return JarLoader.this.jar.getManifest();
                }
                
                @Override
                public Certificate[] getCertificates() {
                    return jarEntry.getCertificates();
                }
                
                @Override
                public CodeSigner[] getCodeSigners() {
                    return jarEntry.getCodeSigners();
                }
            };
        }
        
        boolean validIndex(final String s) {
            String substring = s;
            final int lastIndex;
            if ((lastIndex = s.lastIndexOf("/")) != -1) {
                substring = s.substring(0, lastIndex);
            }
            final Enumeration<JarEntry> entries = this.jar.entries();
            while (entries.hasMoreElements()) {
                String s2 = entries.nextElement().getName();
                final int lastIndex2;
                if ((lastIndex2 = s2.lastIndexOf("/")) != -1) {
                    s2 = s2.substring(0, lastIndex2);
                }
                if (s2.equals(substring)) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        URL findResource(final String s, final boolean b) {
            final Resource resource = this.getResource(s, b);
            if (resource != null) {
                return resource.getURL();
            }
            return null;
        }
        
        @Override
        Resource getResource(final String s, final boolean b) {
            if (this.metaIndex != null && !this.metaIndex.mayContain(s)) {
                return null;
            }
            try {
                this.ensureOpen();
            }
            catch (final IOException ex) {
                throw new InternalError(ex);
            }
            final JarEntry jarEntry = this.jar.getJarEntry(s);
            if (jarEntry != null) {
                return this.checkResource(s, b, jarEntry);
            }
            if (this.index == null) {
                return null;
            }
            return this.getResource(s, b, new HashSet<String>());
        }
        
        Resource getResource(final String s, final boolean b, final Set<String> set) {
            int i = 0;
            LinkedList<String> list;
            if ((list = this.index.get(s)) == null) {
                return null;
            }
            do {
                final int size = list.size();
                final String[] array = list.toArray(new String[size]);
                while (i < size) {
                    final String s2 = array[i++];
                    URL url;
                    JarLoader jarLoader;
                    try {
                        url = new URL(this.csu, s2);
                        final String urlNoFragString = URLUtil.urlNoFragString(url);
                        if ((jarLoader = this.lmap.get(urlNoFragString)) == null) {
                            jarLoader = AccessController.doPrivileged((PrivilegedExceptionAction<JarLoader>)new PrivilegedExceptionAction<JarLoader>() {
                                @Override
                                public JarLoader run() throws IOException {
                                    return new JarLoader(url, JarLoader.this.handler, JarLoader.this.lmap, JarLoader.this.acc);
                                }
                            }, this.acc);
                            final JarIndex index = jarLoader.getIndex();
                            if (index != null) {
                                final int lastIndex = s2.lastIndexOf("/");
                                index.merge(this.index, (lastIndex == -1) ? null : s2.substring(0, lastIndex + 1));
                            }
                            this.lmap.put(urlNoFragString, jarLoader);
                        }
                    }
                    catch (final PrivilegedActionException ex) {
                        continue;
                    }
                    catch (final MalformedURLException ex2) {
                        continue;
                    }
                    final boolean b2 = !set.add(URLUtil.urlNoFragString(url));
                    if (!b2) {
                        try {
                            jarLoader.ensureOpen();
                        }
                        catch (final IOException ex3) {
                            throw new InternalError(ex3);
                        }
                        final JarEntry jarEntry = jarLoader.jar.getJarEntry(s);
                        if (jarEntry != null) {
                            return jarLoader.checkResource(s, b, jarEntry);
                        }
                        if (!jarLoader.validIndex(s)) {
                            throw new InvalidJarIndexException("Invalid index");
                        }
                    }
                    if (!b2 && jarLoader != this) {
                        if (jarLoader.getIndex() == null) {
                            continue;
                        }
                        final Resource resource;
                        if ((resource = jarLoader.getResource(s, b, set)) != null) {
                            return resource;
                        }
                        continue;
                    }
                }
                list = this.index.get(s);
            } while (i < list.size());
            return null;
        }
        
        @Override
        URL[] getClassPath() throws IOException {
            if (this.index != null) {
                return null;
            }
            if (this.metaIndex != null) {
                return null;
            }
            this.ensureOpen();
            this.parseExtensionsDependencies();
            if (SharedSecrets.javaUtilJarAccess().jarFileHasClassPathAttribute(this.jar)) {
                final Manifest manifest = this.jar.getManifest();
                if (manifest != null) {
                    final Attributes mainAttributes = manifest.getMainAttributes();
                    if (mainAttributes != null) {
                        final String value = mainAttributes.getValue(Attributes.Name.CLASS_PATH);
                        if (value != null) {
                            return this.parseClassPath(this.csu, value);
                        }
                    }
                }
            }
            return null;
        }
        
        private void parseExtensionsDependencies() throws IOException {
            ExtensionDependency.checkExtensionsDependencies(this.jar);
        }
        
        private URL[] parseClassPath(final URL url, final String s) throws MalformedURLException {
            final StringTokenizer stringTokenizer = new StringTokenizer(s);
            URL[] array = new URL[stringTokenizer.countTokens()];
            int n = 0;
            while (stringTokenizer.hasMoreTokens()) {
                final String nextToken = stringTokenizer.nextToken();
                final URL url2 = URLClassPath.DISABLE_CP_URL_CHECK ? new URL(url, nextToken) : tryResolve(url, nextToken);
                if (url2 != null) {
                    array[n] = url2;
                    ++n;
                }
                else {
                    if (!URLClassPath.DEBUG_CP_URL_CHECK) {
                        continue;
                    }
                    System.err.println("Class-Path entry: \"" + nextToken + "\" ignored in JAR file " + url);
                }
            }
            if (n == 0) {
                array = null;
            }
            else if (n != array.length) {
                array = Arrays.copyOf(array, n);
            }
            return array;
        }
        
        static URL tryResolve(final URL url, final String s) throws MalformedURLException {
            if ("file".equalsIgnoreCase(url.getProtocol())) {
                return tryResolveFile(url, s);
            }
            return tryResolveNonFile(url, s);
        }
        
        static URL tryResolveFile(final URL url, final String s) throws MalformedURLException {
            final int index = s.indexOf(58);
            return (index < 0 || "file".equalsIgnoreCase(s.substring(0, index))) ? new URL(url, s) : null;
        }
        
        static URL tryResolveNonFile(final URL url, final String s) throws MalformedURLException {
            final String replace = s.replace(File.separatorChar, '/');
            if (isRelative(replace)) {
                final URL url2 = new URL(url, replace);
                final String path = url.getPath();
                final String path2 = url2.getPath();
                int lastIndex = path.lastIndexOf(47);
                if (lastIndex == -1) {
                    lastIndex = path.length() - 1;
                }
                if (path2.regionMatches(0, path, 0, lastIndex + 1) && path2.indexOf("..", lastIndex) == -1) {
                    return url2;
                }
            }
            return null;
        }
        
        static boolean isRelative(final String s) {
            try {
                return !URI.create(s).isAbsolute();
            }
            catch (final IllegalArgumentException ex) {
                return false;
            }
        }
        
        static {
            zipAccess = SharedSecrets.getJavaUtilZipFileAccess();
        }
    }
    
    private static class FileLoader extends Loader
    {
        private File dir;
        
        FileLoader(final URL url) throws IOException {
            super(url);
            if (!"file".equals(url.getProtocol())) {
                throw new IllegalArgumentException("url");
            }
            this.dir = new File(ParseUtil.decode(url.getFile().replace('/', File.separatorChar))).getCanonicalFile();
        }
        
        @Override
        URL findResource(final String s, final boolean b) {
            final Resource resource = this.getResource(s, b);
            if (resource != null) {
                return resource.getURL();
            }
            return null;
        }
        
        @Override
        Resource getResource(final String s, final boolean b) {
            try {
                final URL url = new URL(this.getBaseURL(), ".");
                final URL url2 = new URL(this.getBaseURL(), ParseUtil.encodePath(s, false));
                if (!url2.getFile().startsWith(url.getFile())) {
                    return null;
                }
                if (b) {
                    URLClassPath.check(url2);
                }
                File canonicalFile;
                if (s.indexOf("..") != -1) {
                    canonicalFile = new File(this.dir, s.replace('/', File.separatorChar)).getCanonicalFile();
                    if (!canonicalFile.getPath().startsWith(this.dir.getPath())) {
                        return null;
                    }
                }
                else {
                    canonicalFile = new File(this.dir, s.replace('/', File.separatorChar));
                }
                if (canonicalFile.exists()) {
                    return new Resource() {
                        @Override
                        public String getName() {
                            return s;
                        }
                        
                        @Override
                        public URL getURL() {
                            return url2;
                        }
                        
                        @Override
                        public URL getCodeSourceURL() {
                            return FileLoader.this.getBaseURL();
                        }
                        
                        @Override
                        public InputStream getInputStream() throws IOException {
                            return new FileInputStream(canonicalFile);
                        }
                        
                        @Override
                        public int getContentLength() throws IOException {
                            return (int)canonicalFile.length();
                        }
                    };
                }
            }
            catch (final Exception ex) {
                return null;
            }
            return null;
        }
    }
}
