package sun.misc;

import java.util.jar.JarFile;
import java.util.Iterator;
import java.util.HashMap;
import java.net.URL;
import java.util.Stack;
import java.util.ArrayList;
import java.util.LinkedList;
import java.io.IOException;
import java.util.List;
import java.net.URLClassLoader;

public class ClassLoaderUtil
{
    public static void releaseLoader(final URLClassLoader urlClassLoader) {
        releaseLoader(urlClassLoader, null);
    }
    
    public static List<IOException> releaseLoader(final URLClassLoader urlClassLoader, final List<String> list) {
        final LinkedList list2 = new LinkedList();
        try {
            if (list != null) {
                list.clear();
            }
            final URLClassPath urlClassPath = SharedSecrets.getJavaNetAccess().getURLClassPath(urlClassLoader);
            final ArrayList<URLClassPath.Loader> loaders = urlClassPath.loaders;
            final Stack<URL> urls = urlClassPath.urls;
            final HashMap<String, URLClassPath.Loader> lmap = urlClassPath.lmap;
            synchronized (urls) {
                urls.clear();
            }
            synchronized (lmap) {
                lmap.clear();
            }
            synchronized (urlClassPath) {
                for (final URLClassPath.JarLoader next : loaders) {
                    if (next != null && next instanceof URLClassPath.JarLoader) {
                        final JarFile jarFile = next.getJarFile();
                        try {
                            if (jarFile == null) {
                                continue;
                            }
                            jarFile.close();
                            if (list == null) {
                                continue;
                            }
                            list.add(jarFile.getName());
                        }
                        catch (final IOException ex) {
                            final IOException ex2 = new IOException("Error closing JAR file: " + ((jarFile == null) ? "filename not available" : jarFile.getName()));
                            ex2.initCause(ex);
                            list2.add(ex2);
                        }
                    }
                }
                loaders.clear();
            }
        }
        catch (final Throwable t) {
            throw new RuntimeException(t);
        }
        return list2;
    }
}
