package javax.crypto;

import java.security.cert.Certificate;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.zip.ZipEntry;
import java.util.jar.JarException;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.net.JarURLConnection;
import java.security.PrivilegedExceptionAction;
import java.util.jar.JarFile;
import java.net.URL;

final class JarVerifier
{
    private URL jarURL;
    private boolean savePerms;
    private CryptoPermissions appPerms;
    
    JarVerifier(final URL jarURL, final boolean savePerms) {
        this.appPerms = null;
        this.jarURL = jarURL;
        this.savePerms = savePerms;
    }
    
    void verify() throws JarException, IOException {
        if (!this.savePerms) {
            return;
        }
        final URL url = this.jarURL.getProtocol().equalsIgnoreCase("jar") ? this.jarURL : new URL("jar:" + this.jarURL.toString() + "!/");
        JarFile jarFile = null;
        try {
            try {
                jarFile = AccessController.doPrivileged((PrivilegedExceptionAction<JarFile>)new PrivilegedExceptionAction<JarFile>() {
                    @Override
                    public JarFile run() throws Exception {
                        final JarURLConnection jarURLConnection = (JarURLConnection)url.openConnection();
                        jarURLConnection.setUseCaches(false);
                        return jarURLConnection.getJarFile();
                    }
                });
            }
            catch (final PrivilegedActionException ex) {
                throw new SecurityException("Cannot load " + url.toString(), ex);
            }
            if (jarFile != null) {
                final JarEntry jarEntry = jarFile.getJarEntry("cryptoPerms");
                if (jarEntry == null) {
                    throw new JarException("Can not find cryptoPerms");
                }
                try {
                    (this.appPerms = new CryptoPermissions()).load(jarFile.getInputStream(jarEntry));
                }
                catch (final Exception ex2) {
                    final JarException ex3 = new JarException("Cannot load/parse" + this.jarURL.toString());
                    ex3.initCause(ex2);
                    throw ex3;
                }
            }
        }
        finally {
            if (jarFile != null) {
                jarFile.close();
            }
        }
    }
    
    static void verifyPolicySigned(final Certificate[] array) throws Exception {
    }
    
    CryptoPermissions getPermissions() {
        return this.appPerms;
    }
}
