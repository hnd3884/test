package sun.misc;

import java.util.HashSet;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.security.PermissionCollection;
import java.security.CodeSource;
import java.security.cert.Certificate;
import java.security.ProtectionDomain;
import java.security.AccessControlContext;
import java.net.URI;
import java.nio.file.Paths;
import java.net.URISyntaxException;
import java.util.Vector;
import java.util.StringTokenizer;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import sun.net.www.ParseUtil;
import java.net.URL;
import java.io.File;
import sun.nio.fs.DefaultFileSystemProvider;
import java.io.IOException;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

public class Launcher
{
    private static URLStreamHandlerFactory factory;
    private static Launcher launcher;
    private static String bootClassPath;
    private ClassLoader loader;
    private static URLStreamHandler fileHandler;
    
    public static Launcher getLauncher() {
        return Launcher.launcher;
    }
    
    public Launcher() {
        ExtClassLoader extClassLoader;
        try {
            extClassLoader = ExtClassLoader.getExtClassLoader();
        }
        catch (final IOException ex) {
            throw new InternalError("Could not create extension class loader", ex);
        }
        try {
            this.loader = AppClassLoader.getAppClassLoader(extClassLoader);
        }
        catch (final IOException ex2) {
            throw new InternalError("Could not create application class loader", ex2);
        }
        Thread.currentThread().setContextClassLoader(this.loader);
        final String property = System.getProperty("java.security.manager");
        if (property != null) {
            DefaultFileSystemProvider.create();
            SecurityManager securityManager = null;
            if ("".equals(property) || "default".equals(property)) {
                securityManager = new SecurityManager();
            }
            else {
                try {
                    securityManager = (SecurityManager)this.loader.loadClass(property).newInstance();
                }
                catch (final IllegalAccessException ex3) {}
                catch (final InstantiationException ex4) {}
                catch (final ClassNotFoundException ex5) {}
                catch (final ClassCastException ex6) {}
            }
            if (securityManager == null) {
                throw new InternalError("Could not create SecurityManager: " + property);
            }
            System.setSecurityManager(securityManager);
        }
    }
    
    public ClassLoader getClassLoader() {
        return this.loader;
    }
    
    public static URLClassPath getBootstrapClassPath() {
        return BootClassPathHolder.bcp;
    }
    
    private static URL[] pathToURLs(final File[] array) {
        final URL[] array2 = new URL[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = getFileURL(array[i]);
        }
        return array2;
    }
    
    private static File[] getClassPath(final String s) {
        File[] array;
        if (s != null) {
            int n = 0;
            int n2 = 1;
            int index;
            for (int n3 = 0; (index = s.indexOf(File.pathSeparator, n3)) != -1; n3 = index + 1) {
                ++n2;
            }
            array = new File[n2];
            int n4;
            int index2;
            for (n4 = 0; (index2 = s.indexOf(File.pathSeparator, n4)) != -1; n4 = index2 + 1) {
                if (index2 - n4 > 0) {
                    array[n++] = new File(s.substring(n4, index2));
                }
                else {
                    array[n++] = new File(".");
                }
            }
            if (n4 < s.length()) {
                array[n++] = new File(s.substring(n4));
            }
            else {
                array[n++] = new File(".");
            }
            if (n != n2) {
                final File[] array2 = new File[n];
                System.arraycopy(array, 0, array2, 0, n);
                array = array2;
            }
        }
        else {
            array = new File[0];
        }
        return array;
    }
    
    static URL getFileURL(File canonicalFile) {
        try {
            canonicalFile = canonicalFile.getCanonicalFile();
        }
        catch (final IOException ex) {}
        try {
            return ParseUtil.fileToEncodedURL(canonicalFile);
        }
        catch (final MalformedURLException ex2) {
            throw new InternalError(ex2);
        }
    }
    
    static {
        Launcher.factory = new Factory();
        Launcher.launcher = new Launcher();
        Launcher.bootClassPath = System.getProperty("sun.boot.class.path");
    }
    
    static class ExtClassLoader extends URLClassLoader
    {
        private static volatile ExtClassLoader instance;
        
        public static ExtClassLoader getExtClassLoader() throws IOException {
            if (ExtClassLoader.instance == null) {
                synchronized (ExtClassLoader.class) {
                    if (ExtClassLoader.instance == null) {
                        ExtClassLoader.instance = createExtClassLoader();
                    }
                }
            }
            return ExtClassLoader.instance;
        }
        
        private static ExtClassLoader createExtClassLoader() throws IOException {
            try {
                return AccessController.doPrivileged((PrivilegedExceptionAction<ExtClassLoader>)new PrivilegedExceptionAction<ExtClassLoader>() {
                    @Override
                    public ExtClassLoader run() throws IOException {
                        final File[] access$100 = getExtDirs();
                        for (int length = access$100.length, i = 0; i < length; ++i) {
                            MetaIndex.registerDirectory(access$100[i]);
                        }
                        return new ExtClassLoader(access$100);
                    }
                });
            }
            catch (final PrivilegedActionException ex) {
                throw (IOException)ex.getException();
            }
        }
        
        void addExtURL(final URL url) {
            super.addURL(url);
        }
        
        public ExtClassLoader(final File[] array) throws IOException {
            super(getExtURLs(array), null, Launcher.factory);
            SharedSecrets.getJavaNetAccess().getURLClassPath(this).initLookupCache(this);
        }
        
        private static File[] getExtDirs() {
            final String property = System.getProperty("java.ext.dirs");
            File[] array;
            if (property != null) {
                final StringTokenizer stringTokenizer = new StringTokenizer(property, File.pathSeparator);
                final int countTokens = stringTokenizer.countTokens();
                array = new File[countTokens];
                for (int i = 0; i < countTokens; ++i) {
                    array[i] = new File(stringTokenizer.nextToken());
                }
            }
            else {
                array = new File[0];
            }
            return array;
        }
        
        private static URL[] getExtURLs(final File[] array) throws IOException {
            final Vector vector = new Vector();
            for (int i = 0; i < array.length; ++i) {
                final String[] list = array[i].list();
                if (list != null) {
                    for (int j = 0; j < list.length; ++j) {
                        if (!list[j].equals("meta-index")) {
                            vector.add(Launcher.getFileURL(new File(array[i], list[j])));
                        }
                    }
                }
            }
            final URL[] array2 = new URL[vector.size()];
            vector.copyInto(array2);
            return array2;
        }
        
        public String findLibrary(String mapLibraryName) {
            mapLibraryName = System.mapLibraryName(mapLibraryName);
            final URL[] urLs = super.getURLs();
            Object o = null;
            for (int i = 0; i < urLs.length; ++i) {
                URI uri;
                try {
                    uri = urLs[i].toURI();
                }
                catch (final URISyntaxException ex) {
                    continue;
                }
                final File parentFile = Paths.get(uri).toFile().getParentFile();
                if (parentFile != null && !parentFile.equals(o)) {
                    final String savedProperty = VM.getSavedProperty("os.arch");
                    if (savedProperty != null) {
                        final File file = new File(new File(parentFile, savedProperty), mapLibraryName);
                        if (file.exists()) {
                            return file.getAbsolutePath();
                        }
                    }
                    final File file2 = new File(parentFile, mapLibraryName);
                    if (file2.exists()) {
                        return file2.getAbsolutePath();
                    }
                }
                o = parentFile;
            }
            return null;
        }
        
        private static AccessControlContext getContext(final File[] array) throws IOException {
            final PathPermissions pathPermissions = new PathPermissions(array);
            return new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(new CodeSource(pathPermissions.getCodeBase(), (Certificate[])null), pathPermissions) });
        }
        
        static {
            ClassLoader.registerAsParallelCapable();
            ExtClassLoader.instance = null;
        }
    }
    
    static class AppClassLoader extends URLClassLoader
    {
        final URLClassPath ucp;
        
        public static ClassLoader getAppClassLoader(final ClassLoader classLoader) throws IOException {
            final String property = System.getProperty("java.class.path");
            return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<AppClassLoader>() {
                final /* synthetic */ File[] val$path = (property == null) ? new File[0] : getClassPath(property);
                
                @Override
                public AppClassLoader run() {
                    return new AppClassLoader((property == null) ? new URL[0] : pathToURLs(this.val$path), classLoader);
                }
            });
        }
        
        AppClassLoader(final URL[] array, final ClassLoader classLoader) {
            super(array, classLoader, Launcher.factory);
            (this.ucp = SharedSecrets.getJavaNetAccess().getURLClassPath(this)).initLookupCache(this);
        }
        
        public Class<?> loadClass(final String s, final boolean b) throws ClassNotFoundException {
            final int lastIndex = s.lastIndexOf(46);
            if (lastIndex != -1) {
                final SecurityManager securityManager = System.getSecurityManager();
                if (securityManager != null) {
                    securityManager.checkPackageAccess(s.substring(0, lastIndex));
                }
            }
            if (!this.ucp.knownToNotExist(s)) {
                return super.loadClass(s, b);
            }
            final Class<?> loadedClass = this.findLoadedClass(s);
            if (loadedClass != null) {
                if (b) {
                    this.resolveClass(loadedClass);
                }
                return loadedClass;
            }
            throw new ClassNotFoundException(s);
        }
        
        @Override
        protected PermissionCollection getPermissions(final CodeSource codeSource) {
            final PermissionCollection permissions = super.getPermissions(codeSource);
            permissions.add(new RuntimePermission("exitVM"));
            return permissions;
        }
        
        private void appendToClassPathForInstrumentation(final String s) {
            assert Thread.holdsLock(this);
            super.addURL(Launcher.getFileURL(new File(s)));
        }
        
        private static AccessControlContext getContext(final File[] array) throws MalformedURLException {
            final PathPermissions pathPermissions = new PathPermissions(array);
            return new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(new CodeSource(pathPermissions.getCodeBase(), (Certificate[])null), pathPermissions) });
        }
        
        static {
            ClassLoader.registerAsParallelCapable();
        }
    }
    
    private static class BootClassPathHolder
    {
        static final URLClassPath bcp;
        
        static {
            URL[] array;
            if (Launcher.bootClassPath != null) {
                array = AccessController.doPrivileged((PrivilegedAction<URL[]>)new PrivilegedAction<URL[]>() {
                    @Override
                    public URL[] run() {
                        final File[] access$300 = getClassPath(Launcher.bootClassPath);
                        final int length = access$300.length;
                        final HashSet set = new HashSet();
                        for (File parentFile : access$300) {
                            if (!parentFile.isDirectory()) {
                                parentFile = parentFile.getParentFile();
                            }
                            if (parentFile != null && set.add(parentFile)) {
                                MetaIndex.registerDirectory(parentFile);
                            }
                        }
                        return pathToURLs(access$300);
                    }
                });
            }
            else {
                array = new URL[0];
            }
            (bcp = new URLClassPath(array, Launcher.factory, null)).initLookupCache(null);
        }
    }
    
    private static class Factory implements URLStreamHandlerFactory
    {
        private static String PREFIX;
        
        @Override
        public URLStreamHandler createURLStreamHandler(final String s) {
            final String string = Factory.PREFIX + "." + s + ".Handler";
            try {
                return (URLStreamHandler)Class.forName(string).newInstance();
            }
            catch (final ReflectiveOperationException ex) {
                throw new InternalError("could not load " + s + "system protocol handler", ex);
            }
        }
        
        static {
            Factory.PREFIX = "sun.net.www.protocol";
        }
    }
}
