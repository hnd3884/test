package javax.activation;

import java.net.URL;
import java.io.IOException;
import java.io.InputStream;

class SecuritySupport
{
    private static final Object securitySupport;
    
    static {
        Object securitySupport2 = null;
        try {
            Class.forName("java.security.AccessController");
            securitySupport2 = new SecuritySupport12();
        }
        catch (final Exception ex) {}
        finally {
            if (securitySupport2 == null) {
                securitySupport2 = new SecuritySupport();
            }
            securitySupport = securitySupport2;
        }
    }
    
    public ClassLoader getContextClassLoader() {
        return null;
    }
    
    public static SecuritySupport getInstance() {
        return (SecuritySupport)SecuritySupport.securitySupport;
    }
    
    public InputStream getResourceAsStream(final Class clazz, final String s) throws IOException {
        return clazz.getResourceAsStream(s);
    }
    
    public URL[] getResources(final ClassLoader classLoader, final String s) {
        return null;
    }
    
    public URL[] getSystemResources(final String s) {
        return null;
    }
    
    public InputStream openStream(final URL url) throws IOException {
        return url.openStream();
    }
}
