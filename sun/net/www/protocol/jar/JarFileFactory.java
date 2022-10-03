package sun.net.www.protocol.jar;

import java.security.Permission;
import java.net.SocketPermission;
import java.io.FilePermission;
import java.io.FileNotFoundException;
import sun.net.util.URLUtil;
import java.io.IOException;
import java.net.URLConnection;
import java.net.URL;
import java.util.jar.JarFile;
import java.util.HashMap;

class JarFileFactory implements URLJarFile.URLJarFileCloseController
{
    private static final HashMap<String, JarFile> fileCache;
    private static final HashMap<JarFile, URL> urlCache;
    private static final JarFileFactory instance;
    
    private JarFileFactory() {
    }
    
    public static JarFileFactory getInstance() {
        return JarFileFactory.instance;
    }
    
    URLConnection getConnection(final JarFile jarFile) throws IOException {
        final URL url;
        synchronized (JarFileFactory.instance) {
            url = JarFileFactory.urlCache.get(jarFile);
        }
        if (url != null) {
            return url.openConnection();
        }
        return null;
    }
    
    public JarFile get(final URL url) throws IOException {
        return this.get(url, true);
    }
    
    JarFile get(URL url, final boolean b) throws IOException {
        if (url.getProtocol().equalsIgnoreCase("file")) {
            final Object o = url.getHost();
            if (o != null && !((String)o).equals("") && !((String)o).equalsIgnoreCase("localhost")) {
                url = new URL("file", "", "//" + (String)o + url.getPath());
            }
        }
        Object o;
        if (b) {
            synchronized (JarFileFactory.instance) {
                o = this.getCachedJarFile(url);
            }
            if (o == null) {
                final JarFile jarFile = URLJarFile.getJarFile(url, this);
                synchronized (JarFileFactory.instance) {
                    o = this.getCachedJarFile(url);
                    if (o == null) {
                        JarFileFactory.fileCache.put(URLUtil.urlNoFragString(url), jarFile);
                        JarFileFactory.urlCache.put(jarFile, url);
                        o = jarFile;
                    }
                    else if (jarFile != null) {
                        jarFile.close();
                    }
                }
            }
        }
        else {
            o = URLJarFile.getJarFile(url, this);
        }
        if (o == null) {
            throw new FileNotFoundException(url.toString());
        }
        return (JarFile)o;
    }
    
    @Override
    public void close(final JarFile jarFile) {
        synchronized (JarFileFactory.instance) {
            final URL url = JarFileFactory.urlCache.remove(jarFile);
            if (url != null) {
                JarFileFactory.fileCache.remove(URLUtil.urlNoFragString(url));
            }
        }
    }
    
    private JarFile getCachedJarFile(final URL url) {
        assert Thread.holdsLock(JarFileFactory.instance);
        final JarFile jarFile = JarFileFactory.fileCache.get(URLUtil.urlNoFragString(url));
        if (jarFile != null) {
            final Permission permission = this.getPermission(jarFile);
            if (permission != null) {
                final SecurityManager securityManager = System.getSecurityManager();
                if (securityManager != null) {
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
                            securityManager.checkConnect(url.getHost(), url.getPort());
                        }
                    }
                }
            }
        }
        return jarFile;
    }
    
    private Permission getPermission(final JarFile jarFile) {
        try {
            final URLConnection connection = this.getConnection(jarFile);
            if (connection != null) {
                return connection.getPermission();
            }
        }
        catch (final IOException ex) {}
        return null;
    }
    
    static {
        fileCache = new HashMap<String, JarFile>();
        urlCache = new HashMap<JarFile, URL>();
        instance = new JarFileFactory();
    }
}
