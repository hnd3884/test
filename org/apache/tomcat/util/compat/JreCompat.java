package org.apache.tomcat.util.compat;

import java.lang.reflect.AccessibleObject;
import java.io.File;
import java.util.jar.JarFile;
import java.util.Deque;
import java.io.IOException;
import java.net.URLConnection;
import java.net.URL;
import javax.net.ssl.SSLEngine;
import java.security.KeyStore;
import java.net.URI;
import javax.net.ssl.SSLParameters;
import org.apache.tomcat.util.res.StringManager;

public class JreCompat
{
    private static final int RUNTIME_MAJOR_VERSION = 7;
    private static final JreCompat instance;
    private static StringManager sm;
    private static final boolean jre9Available;
    private static final boolean jre8Available;
    
    public static JreCompat getInstance() {
        return JreCompat.instance;
    }
    
    public static boolean isJre8Available() {
        return JreCompat.jre8Available;
    }
    
    public void setUseServerCipherSuitesOrder(final SSLParameters engine, final boolean useCipherSuitesOrder) {
        throw new UnsupportedOperationException(JreCompat.sm.getString("jreCompat.noServerCipherSuiteOrder"));
    }
    
    public KeyStore.LoadStoreParameter getDomainLoadStoreParameter(final URI uri) {
        throw new UnsupportedOperationException(JreCompat.sm.getString("jreCompat.noDomainLoadStoreParameter"));
    }
    
    public static boolean isAlpnSupported() {
        return isJre9Available() || (isJre8Available() && Jre8Compat.isAlpnSupported());
    }
    
    public static boolean isJre9Available() {
        return JreCompat.jre9Available;
    }
    
    public boolean isInstanceOfInaccessibleObjectException(final Throwable t) {
        return false;
    }
    
    public void setApplicationProtocols(final SSLParameters sslParameters, final String[] protocols) {
        throw new UnsupportedOperationException(JreCompat.sm.getString("jreCompat.noApplicationProtocols"));
    }
    
    public String getApplicationProtocol(final SSLEngine sslEngine) {
        throw new UnsupportedOperationException(JreCompat.sm.getString("jreCompat.noApplicationProtocol"));
    }
    
    public void disableCachingForJarUrlConnections() throws IOException {
        final URL url = new URL("jar:file://dummy.jar!/");
        final URLConnection uConn = url.openConnection();
        uConn.setDefaultUseCaches(false);
    }
    
    public void addBootModulePath(final Deque<URL> classPathUrlsToProcess) {
    }
    
    public final JarFile jarFileNewInstance(final String s) throws IOException {
        return this.jarFileNewInstance(new File(s));
    }
    
    public JarFile jarFileNewInstance(final File f) throws IOException {
        return new JarFile(f);
    }
    
    public boolean jarFileIsMultiRelease(final JarFile jarFile) {
        return false;
    }
    
    public int jarFileRuntimeMajorVersion() {
        return 7;
    }
    
    public boolean canAccess(final Object base, final AccessibleObject accessibleObject) {
        return true;
    }
    
    public boolean isExported(final Class<?> type) {
        return true;
    }
    
    public String getModuleName(final Class<?> type) {
        return "NO_MODULE_JAVA_8";
    }
    
    static {
        JreCompat.sm = StringManager.getManager(JreCompat.class.getPackage().getName());
        if (Jre9Compat.isSupported()) {
            instance = new Jre9Compat();
            jre9Available = true;
            jre8Available = true;
        }
        else if (Jre8Compat.isSupported()) {
            instance = new Jre8Compat();
            jre9Available = false;
            jre8Available = true;
        }
        else {
            instance = new JreCompat();
            jre9Available = false;
            jre8Available = false;
        }
    }
}
