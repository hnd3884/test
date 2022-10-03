package javax.net.ssl;

import java.security.NoSuchAlgorithmException;
import javax.net.ServerSocketFactory;

public abstract class SSLServerSocketFactory extends ServerSocketFactory
{
    private static SSLServerSocketFactory theFactory;
    private static boolean propertyChecked;
    
    private static void log(final String s) {
        if (SSLSocketFactory.DEBUG) {
            System.out.println(s);
        }
    }
    
    protected SSLServerSocketFactory() {
    }
    
    public static synchronized ServerSocketFactory getDefault() {
        if (SSLServerSocketFactory.theFactory != null) {
            return SSLServerSocketFactory.theFactory;
        }
        if (!SSLServerSocketFactory.propertyChecked) {
            SSLServerSocketFactory.propertyChecked = true;
            final String securityProperty = SSLSocketFactory.getSecurityProperty("ssl.ServerSocketFactory.provider");
            if (securityProperty != null) {
                log("setting up default SSLServerSocketFactory");
                try {
                    Class<?> clazz = null;
                    try {
                        clazz = Class.forName(securityProperty);
                    }
                    catch (final ClassNotFoundException ex) {
                        final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
                        if (systemClassLoader != null) {
                            clazz = systemClassLoader.loadClass(securityProperty);
                        }
                    }
                    log("class " + securityProperty + " is loaded");
                    final SSLServerSocketFactory theFactory = (SSLServerSocketFactory)clazz.newInstance();
                    log("instantiated an instance of class " + securityProperty);
                    return SSLServerSocketFactory.theFactory = theFactory;
                }
                catch (final Exception ex2) {
                    log("SSLServerSocketFactory instantiation failed: " + ex2);
                    return SSLServerSocketFactory.theFactory = new DefaultSSLServerSocketFactory(ex2);
                }
            }
        }
        try {
            return SSLContext.getDefault().getServerSocketFactory();
        }
        catch (final NoSuchAlgorithmException ex3) {
            return new DefaultSSLServerSocketFactory(ex3);
        }
    }
    
    public abstract String[] getDefaultCipherSuites();
    
    public abstract String[] getSupportedCipherSuites();
}
