package sun.rmi.server;

import java.util.Enumeration;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.rmi.server.LogStream;
import sun.security.action.GetPropertyAction;
import sun.reflect.misc.ReflectUtil;
import java.net.URLConnection;
import java.net.SocketPermission;
import java.net.JarURLConnection;
import java.io.File;
import java.io.FilePermission;
import java.security.ProtectionDomain;
import java.util.PropertyPermission;
import java.security.Policy;
import java.security.CodeSource;
import java.security.cert.Certificate;
import java.security.PermissionCollection;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.ref.SoftReference;
import java.util.StringTokenizer;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.security.Permission;
import java.io.IOException;
import java.security.Permissions;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.lang.ref.ReferenceQueue;
import java.util.HashMap;
import java.util.Map;
import java.net.URL;
import sun.rmi.runtime.Log;

public final class LoaderHandler
{
    static final int logLevel;
    static final Log loaderLog;
    private static String codebaseProperty;
    private static URL[] codebaseURLs;
    private static final Map<ClassLoader, Void> codebaseLoaders;
    private static final HashMap<LoaderKey, LoaderEntry> loaderTable;
    private static final ReferenceQueue<Loader> refQueue;
    private static final Map<String, Object[]> pathToURLsCache;
    
    private LoaderHandler() {
    }
    
    private static synchronized URL[] getDefaultCodebaseURLs() throws MalformedURLException {
        if (LoaderHandler.codebaseURLs == null) {
            if (LoaderHandler.codebaseProperty != null) {
                LoaderHandler.codebaseURLs = pathToURLs(LoaderHandler.codebaseProperty);
            }
            else {
                LoaderHandler.codebaseURLs = new URL[0];
            }
        }
        return LoaderHandler.codebaseURLs;
    }
    
    public static Class<?> loadClass(final String s, final String s2, final ClassLoader classLoader) throws MalformedURLException, ClassNotFoundException {
        if (LoaderHandler.loaderLog.isLoggable(Log.BRIEF)) {
            LoaderHandler.loaderLog.log(Log.BRIEF, "name = \"" + s2 + "\", codebase = \"" + ((s != null) ? s : "") + "\"" + ((classLoader != null) ? (", defaultLoader = " + classLoader) : ""));
        }
        URL[] array;
        if (s != null) {
            array = pathToURLs(s);
        }
        else {
            array = getDefaultCodebaseURLs();
        }
        if (classLoader != null) {
            try {
                final Class<?> loadClassForName = loadClassForName(s2, false, classLoader);
                if (LoaderHandler.loaderLog.isLoggable(Log.VERBOSE)) {
                    LoaderHandler.loaderLog.log(Log.VERBOSE, "class \"" + s2 + "\" found via defaultLoader, defined by " + loadClassForName.getClassLoader());
                }
                return loadClassForName;
            }
            catch (final ClassNotFoundException ex) {}
        }
        return loadClass(array, s2);
    }
    
    public static String getClassAnnotation(final Class<?> clazz) {
        final String name = clazz.getName();
        final int length = name.length();
        if (length > 0 && name.charAt(0) == '[') {
            int n;
            for (n = 1; length > n && name.charAt(n) == '['; ++n) {}
            if (length > n && name.charAt(n) != 'L') {
                return null;
            }
        }
        final ClassLoader classLoader = clazz.getClassLoader();
        if (classLoader == null || LoaderHandler.codebaseLoaders.containsKey(classLoader)) {
            return LoaderHandler.codebaseProperty;
        }
        String s = null;
        if (classLoader instanceof Loader) {
            s = ((Loader)classLoader).getClassAnnotation();
        }
        else if (classLoader instanceof URLClassLoader) {
            try {
                final URL[] urLs = ((URLClassLoader)classLoader).getURLs();
                if (urLs != null) {
                    final SecurityManager securityManager = System.getSecurityManager();
                    if (securityManager != null) {
                        final Permissions permissions = new Permissions();
                        for (int i = 0; i < urLs.length; ++i) {
                            final Permission permission = urLs[i].openConnection().getPermission();
                            if (permission != null && !permissions.implies(permission)) {
                                securityManager.checkPermission(permission);
                                permissions.add(permission);
                            }
                        }
                    }
                    s = urlsToPath(urLs);
                }
            }
            catch (final SecurityException | IOException ex) {}
        }
        if (s != null) {
            return s;
        }
        return LoaderHandler.codebaseProperty;
    }
    
    public static ClassLoader getClassLoader(final String s) throws MalformedURLException {
        final ClassLoader rmiContextClassLoader = getRMIContextClassLoader();
        URL[] array;
        if (s != null) {
            array = pathToURLs(s);
        }
        else {
            array = getDefaultCodebaseURLs();
        }
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new RuntimePermission("getClassLoader"));
            final Loader lookupLoader = lookupLoader(array, rmiContextClassLoader);
            if (lookupLoader != null) {
                lookupLoader.checkPermissions();
            }
            return lookupLoader;
        }
        return rmiContextClassLoader;
    }
    
    public static Object getSecurityContext(final ClassLoader classLoader) {
        if (classLoader instanceof Loader) {
            final URL[] urLs = ((Loader)classLoader).getURLs();
            if (urLs.length > 0) {
                return urLs[0];
            }
        }
        return null;
    }
    
    public static void registerCodebaseLoader(final ClassLoader classLoader) {
        LoaderHandler.codebaseLoaders.put(classLoader, null);
    }
    
    private static Class<?> loadClass(final URL[] array, final String s) throws ClassNotFoundException {
        final ClassLoader rmiContextClassLoader = getRMIContextClassLoader();
        if (LoaderHandler.loaderLog.isLoggable(Log.VERBOSE)) {
            LoaderHandler.loaderLog.log(Log.VERBOSE, "(thread context class loader: " + rmiContextClassLoader + ")");
        }
        if (System.getSecurityManager() == null) {
            try {
                final Class<?> forName = Class.forName(s, false, rmiContextClassLoader);
                if (LoaderHandler.loaderLog.isLoggable(Log.VERBOSE)) {
                    LoaderHandler.loaderLog.log(Log.VERBOSE, "class \"" + s + "\" found via thread context class loader (no security manager: codebase disabled), defined by " + forName.getClassLoader());
                }
                return forName;
            }
            catch (final ClassNotFoundException ex) {
                if (LoaderHandler.loaderLog.isLoggable(Log.BRIEF)) {
                    LoaderHandler.loaderLog.log(Log.BRIEF, "class \"" + s + "\" not found via thread context class loader (no security manager: codebase disabled)", ex);
                }
                throw new ClassNotFoundException(ex.getMessage() + " (no security manager: RMI class loader disabled)", ex.getException());
            }
        }
        final Loader lookupLoader = lookupLoader(array, rmiContextClassLoader);
        try {
            if (lookupLoader != null) {
                lookupLoader.checkPermissions();
            }
        }
        catch (final SecurityException ex2) {
            try {
                final Class<?> loadClassForName = loadClassForName(s, false, rmiContextClassLoader);
                if (LoaderHandler.loaderLog.isLoggable(Log.VERBOSE)) {
                    LoaderHandler.loaderLog.log(Log.VERBOSE, "class \"" + s + "\" found via thread context class loader (access to codebase denied), defined by " + loadClassForName.getClassLoader());
                }
                return loadClassForName;
            }
            catch (final ClassNotFoundException ex3) {
                if (LoaderHandler.loaderLog.isLoggable(Log.BRIEF)) {
                    LoaderHandler.loaderLog.log(Log.BRIEF, "class \"" + s + "\" not found via thread context class loader (access to codebase denied)", ex2);
                }
                throw new ClassNotFoundException("access to class loader denied", ex2);
            }
        }
        try {
            final Class<?> loadClassForName2 = loadClassForName(s, false, lookupLoader);
            if (LoaderHandler.loaderLog.isLoggable(Log.VERBOSE)) {
                LoaderHandler.loaderLog.log(Log.VERBOSE, "class \"" + s + "\" found via codebase, defined by " + loadClassForName2.getClassLoader());
            }
            return loadClassForName2;
        }
        catch (final ClassNotFoundException ex4) {
            if (LoaderHandler.loaderLog.isLoggable(Log.BRIEF)) {
                LoaderHandler.loaderLog.log(Log.BRIEF, "class \"" + s + "\" not found via codebase", ex4);
            }
            throw ex4;
        }
    }
    
    public static Class<?> loadProxyClass(final String s, final String[] array, final ClassLoader classLoader) throws MalformedURLException, ClassNotFoundException {
        if (LoaderHandler.loaderLog.isLoggable(Log.BRIEF)) {
            LoaderHandler.loaderLog.log(Log.BRIEF, "interfaces = " + Arrays.asList(array) + ", codebase = \"" + ((s != null) ? s : "") + "\"" + ((classLoader != null) ? (", defaultLoader = " + classLoader) : ""));
        }
        final ClassLoader rmiContextClassLoader = getRMIContextClassLoader();
        if (LoaderHandler.loaderLog.isLoggable(Log.VERBOSE)) {
            LoaderHandler.loaderLog.log(Log.VERBOSE, "(thread context class loader: " + rmiContextClassLoader + ")");
        }
        URL[] array2;
        if (s != null) {
            array2 = pathToURLs(s);
        }
        else {
            array2 = getDefaultCodebaseURLs();
        }
        if (System.getSecurityManager() == null) {
            try {
                final Class<?> loadProxyClass = loadProxyClass(array, classLoader, rmiContextClassLoader, false);
                if (LoaderHandler.loaderLog.isLoggable(Log.VERBOSE)) {
                    LoaderHandler.loaderLog.log(Log.VERBOSE, "(no security manager: codebase disabled) proxy class defined by " + loadProxyClass.getClassLoader());
                }
                return loadProxyClass;
            }
            catch (final ClassNotFoundException ex) {
                if (LoaderHandler.loaderLog.isLoggable(Log.BRIEF)) {
                    LoaderHandler.loaderLog.log(Log.BRIEF, "(no security manager: codebase disabled) proxy class resolution failed", ex);
                }
                throw new ClassNotFoundException(ex.getMessage() + " (no security manager: RMI class loader disabled)", ex.getException());
            }
        }
        final Loader lookupLoader = lookupLoader(array2, rmiContextClassLoader);
        try {
            if (lookupLoader != null) {
                lookupLoader.checkPermissions();
            }
        }
        catch (final SecurityException ex2) {
            try {
                final Class<?> loadProxyClass2 = loadProxyClass(array, classLoader, rmiContextClassLoader, false);
                if (LoaderHandler.loaderLog.isLoggable(Log.VERBOSE)) {
                    LoaderHandler.loaderLog.log(Log.VERBOSE, "(access to codebase denied) proxy class defined by " + loadProxyClass2.getClassLoader());
                }
                return loadProxyClass2;
            }
            catch (final ClassNotFoundException ex3) {
                if (LoaderHandler.loaderLog.isLoggable(Log.BRIEF)) {
                    LoaderHandler.loaderLog.log(Log.BRIEF, "(access to codebase denied) proxy class resolution failed", ex2);
                }
                throw new ClassNotFoundException("access to class loader denied", ex2);
            }
        }
        try {
            final Class<?> loadProxyClass3 = loadProxyClass(array, classLoader, lookupLoader, true);
            if (LoaderHandler.loaderLog.isLoggable(Log.VERBOSE)) {
                LoaderHandler.loaderLog.log(Log.VERBOSE, "proxy class defined by " + loadProxyClass3.getClassLoader());
            }
            return loadProxyClass3;
        }
        catch (final ClassNotFoundException ex4) {
            if (LoaderHandler.loaderLog.isLoggable(Log.BRIEF)) {
                LoaderHandler.loaderLog.log(Log.BRIEF, "proxy class resolution failed", ex4);
            }
            throw ex4;
        }
    }
    
    private static Class<?> loadProxyClass(final String[] array, final ClassLoader classLoader, final ClassLoader classLoader2, final boolean b) throws ClassNotFoundException {
        final Class[] array2 = new Class[array.length];
        final boolean[] array3 = { false };
        Label_0155: {
            if (classLoader != null) {
                ClassLoader loadProxyInterfaces;
                try {
                    loadProxyInterfaces = loadProxyInterfaces(array, classLoader, array2, array3);
                    if (LoaderHandler.loaderLog.isLoggable(Log.VERBOSE)) {
                        final ClassLoader[] array4 = new ClassLoader[array2.length];
                        for (int i = 0; i < array4.length; ++i) {
                            array4[i] = array2[i].getClassLoader();
                        }
                        LoaderHandler.loaderLog.log(Log.VERBOSE, "proxy interfaces found via defaultLoader, defined by " + Arrays.asList(array4));
                    }
                }
                catch (final ClassNotFoundException ex) {
                    break Label_0155;
                }
                if (!array3[0]) {
                    if (b) {
                        try {
                            return Proxy.getProxyClass(classLoader2, (Class<?>[])array2);
                        }
                        catch (final IllegalArgumentException ex2) {}
                    }
                    loadProxyInterfaces = classLoader;
                }
                return loadProxyClass(loadProxyInterfaces, array2);
            }
        }
        array3[0] = false;
        ClassLoader loadProxyInterfaces2 = loadProxyInterfaces(array, classLoader2, array2, array3);
        if (LoaderHandler.loaderLog.isLoggable(Log.VERBOSE)) {
            final ClassLoader[] array5 = new ClassLoader[array2.length];
            for (int j = 0; j < array5.length; ++j) {
                array5[j] = array2[j].getClassLoader();
            }
            LoaderHandler.loaderLog.log(Log.VERBOSE, "proxy interfaces found via codebase, defined by " + Arrays.asList(array5));
        }
        if (!array3[0]) {
            loadProxyInterfaces2 = classLoader2;
        }
        return loadProxyClass(loadProxyInterfaces2, array2);
    }
    
    private static Class<?> loadProxyClass(final ClassLoader classLoader, final Class<?>[] array) throws ClassNotFoundException {
        try {
            return Proxy.getProxyClass(classLoader, array);
        }
        catch (final IllegalArgumentException ex) {
            throw new ClassNotFoundException("error creating dynamic proxy class", ex);
        }
    }
    
    private static ClassLoader loadProxyInterfaces(final String[] array, final ClassLoader classLoader, final Class<?>[] array2, final boolean[] array3) throws ClassNotFoundException {
        ClassLoader classLoader2 = null;
        for (int i = 0; i < array.length; ++i) {
            final int n = i;
            final Class<?> loadClassForName = loadClassForName(array[i], false, classLoader);
            array2[n] = loadClassForName;
            final Class<?> clazz = loadClassForName;
            if (!Modifier.isPublic(clazz.getModifiers())) {
                final ClassLoader classLoader3 = clazz.getClassLoader();
                if (LoaderHandler.loaderLog.isLoggable(Log.VERBOSE)) {
                    LoaderHandler.loaderLog.log(Log.VERBOSE, "non-public interface \"" + array[i] + "\" defined by " + classLoader3);
                }
                if (!array3[0]) {
                    classLoader2 = classLoader3;
                    array3[0] = true;
                }
                else if (classLoader3 != classLoader2) {
                    throw new IllegalAccessError("non-public interfaces defined in different class loaders");
                }
            }
        }
        return classLoader2;
    }
    
    private static URL[] pathToURLs(final String s) throws MalformedURLException {
        synchronized (LoaderHandler.pathToURLsCache) {
            final Object[] array = LoaderHandler.pathToURLsCache.get(s);
            if (array != null) {
                return (URL[])array[0];
            }
        }
        final StringTokenizer stringTokenizer = new StringTokenizer(s);
        final URL[] array2 = new URL[stringTokenizer.countTokens()];
        int n = 0;
        while (stringTokenizer.hasMoreTokens()) {
            array2[n] = new URL(stringTokenizer.nextToken());
            ++n;
        }
        synchronized (LoaderHandler.pathToURLsCache) {
            LoaderHandler.pathToURLsCache.put(s, new Object[] { array2, new SoftReference(s) });
        }
        return array2;
    }
    
    private static String urlsToPath(final URL[] array) {
        if (array.length == 0) {
            return null;
        }
        if (array.length == 1) {
            return array[0].toExternalForm();
        }
        final StringBuffer sb = new StringBuffer(array[0].toExternalForm());
        for (int i = 1; i < array.length; ++i) {
            sb.append(' ');
            sb.append(array[i].toExternalForm());
        }
        return sb.toString();
    }
    
    private static ClassLoader getRMIContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
    
    private static Loader lookupLoader(final URL[] array, final ClassLoader classLoader) {
        Loader loader;
        synchronized (LoaderHandler.class) {
            LoaderEntry loaderEntry;
            while ((loaderEntry = (LoaderEntry)LoaderHandler.refQueue.poll()) != null) {
                if (!loaderEntry.removed) {
                    LoaderHandler.loaderTable.remove(loaderEntry.key);
                }
            }
            final LoaderKey loaderKey = new LoaderKey(array, classLoader);
            final LoaderEntry loaderEntry2 = LoaderHandler.loaderTable.get(loaderKey);
            if (loaderEntry2 == null || (loader = loaderEntry2.get()) == null) {
                if (loaderEntry2 != null) {
                    LoaderHandler.loaderTable.remove(loaderKey);
                    loaderEntry2.removed = true;
                }
                loader = AccessController.doPrivileged((PrivilegedAction<Loader>)new PrivilegedAction<Loader>() {
                    @Override
                    public Loader run() {
                        return new Loader(array, classLoader);
                    }
                }, getLoaderAccessControlContext(array));
                LoaderHandler.loaderTable.put(loaderKey, new LoaderEntry(loaderKey, loader));
            }
        }
        return loader;
    }
    
    private static AccessControlContext getLoaderAccessControlContext(final URL[] array) {
        final PermissionCollection collection = AccessController.doPrivileged((PrivilegedAction<PermissionCollection>)new PrivilegedAction<PermissionCollection>() {
            @Override
            public PermissionCollection run() {
                final CodeSource codeSource = new CodeSource(null, (Certificate[])null);
                final Policy policy = Policy.getPolicy();
                if (policy != null) {
                    return policy.getPermissions(codeSource);
                }
                return new Permissions();
            }
        });
        collection.add(new RuntimePermission("createClassLoader"));
        collection.add(new PropertyPermission("java.*", "read"));
        addPermissionsForURLs(array, collection, true);
        return new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(new CodeSource((array.length > 0) ? array[0] : null, (Certificate[])null), collection) });
    }
    
    private static void addPermissionsForURLs(final URL[] array, final PermissionCollection collection, final boolean b) {
        for (int i = 0; i < array.length; ++i) {
            final URL url = array[i];
            try {
                final URLConnection openConnection = url.openConnection();
                final Permission permission = openConnection.getPermission();
                if (permission != null) {
                    if (permission instanceof FilePermission) {
                        final String name = permission.getName();
                        final int lastIndex = name.lastIndexOf(File.separatorChar);
                        if (lastIndex != -1) {
                            String s = name.substring(0, lastIndex + 1);
                            if (s.endsWith(File.separator)) {
                                s += "-";
                            }
                            final FilePermission filePermission = new FilePermission(s, "read");
                            if (!collection.implies(filePermission)) {
                                collection.add(filePermission);
                            }
                            collection.add(new FilePermission(s, "read"));
                        }
                        else if (!collection.implies(permission)) {
                            collection.add(permission);
                        }
                    }
                    else {
                        if (!collection.implies(permission)) {
                            collection.add(permission);
                        }
                        if (b) {
                            URL jarFileURL = url;
                            for (URLConnection openConnection2 = openConnection; openConnection2 instanceof JarURLConnection; openConnection2 = jarFileURL.openConnection()) {
                                jarFileURL = ((JarURLConnection)openConnection2).getJarFileURL();
                            }
                            final String host = jarFileURL.getHost();
                            if (host != null && permission.implies(new SocketPermission(host, "resolve"))) {
                                final SocketPermission socketPermission = new SocketPermission(host, "connect,accept");
                                if (!collection.implies(socketPermission)) {
                                    collection.add(socketPermission);
                                }
                            }
                        }
                    }
                }
            }
            catch (final IOException ex) {}
        }
    }
    
    private static Class<?> loadClassForName(final String s, final boolean b, final ClassLoader classLoader) throws ClassNotFoundException {
        if (classLoader == null) {
            ReflectUtil.checkPackageAccess(s);
        }
        return Class.forName(s, b, classLoader);
    }
    
    static {
        logLevel = LogStream.parseLevel(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.rmi.loader.logLevel")));
        loaderLog = Log.getLog("sun.rmi.loader", "loader", LoaderHandler.logLevel);
        LoaderHandler.codebaseProperty = null;
        final String codebaseProperty = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("java.rmi.server.codebase"));
        if (codebaseProperty != null && codebaseProperty.trim().length() > 0) {
            LoaderHandler.codebaseProperty = codebaseProperty;
        }
        LoaderHandler.codebaseURLs = null;
        codebaseLoaders = Collections.synchronizedMap(new IdentityHashMap<ClassLoader, Void>(5));
        for (ClassLoader classLoader = ClassLoader.getSystemClassLoader(); classLoader != null; classLoader = classLoader.getParent()) {
            LoaderHandler.codebaseLoaders.put(classLoader, null);
        }
        loaderTable = new HashMap<LoaderKey, LoaderEntry>(5);
        refQueue = new ReferenceQueue<Loader>();
        pathToURLsCache = new WeakHashMap<String, Object[]>(5);
    }
    
    private static class LoaderKey
    {
        private URL[] urls;
        private ClassLoader parent;
        private int hashValue;
        
        public LoaderKey(final URL[] urls, final ClassLoader parent) {
            this.urls = urls;
            this.parent = parent;
            if (parent != null) {
                this.hashValue = parent.hashCode();
            }
            for (int i = 0; i < urls.length; ++i) {
                this.hashValue ^= urls[i].hashCode();
            }
        }
        
        @Override
        public int hashCode() {
            return this.hashValue;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof LoaderKey)) {
                return false;
            }
            final LoaderKey loaderKey = (LoaderKey)o;
            if (this.parent != loaderKey.parent) {
                return false;
            }
            if (this.urls == loaderKey.urls) {
                return true;
            }
            if (this.urls.length != loaderKey.urls.length) {
                return false;
            }
            for (int i = 0; i < this.urls.length; ++i) {
                if (!this.urls[i].equals(loaderKey.urls[i])) {
                    return false;
                }
            }
            return true;
        }
    }
    
    private static class LoaderEntry extends WeakReference<Loader>
    {
        public LoaderKey key;
        public boolean removed;
        
        public LoaderEntry(final LoaderKey key, final Loader loader) {
            super(loader, LoaderHandler.refQueue);
            this.removed = false;
            this.key = key;
        }
    }
    
    private static class Loader extends URLClassLoader
    {
        private ClassLoader parent;
        private String annotation;
        private Permissions permissions;
        
        private Loader(final URL[] array, final ClassLoader parent) {
            super(array, parent);
            this.parent = parent;
            addPermissionsForURLs(array, this.permissions = new Permissions(), false);
            this.annotation = urlsToPath(array);
        }
        
        public String getClassAnnotation() {
            return this.annotation;
        }
        
        private void checkPermissions() {
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                final Enumeration<Permission> elements = this.permissions.elements();
                while (elements.hasMoreElements()) {
                    securityManager.checkPermission(elements.nextElement());
                }
            }
        }
        
        @Override
        protected PermissionCollection getPermissions(final CodeSource codeSource) {
            return super.getPermissions(codeSource);
        }
        
        @Override
        public String toString() {
            return super.toString() + "[\"" + this.annotation + "\"]";
        }
        
        @Override
        protected Class<?> loadClass(final String s, final boolean b) throws ClassNotFoundException {
            if (this.parent == null) {
                ReflectUtil.checkPackageAccess(s);
            }
            return super.loadClass(s, b);
        }
    }
}
