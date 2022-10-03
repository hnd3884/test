package sun.net.ftp;

import java.security.AccessController;
import sun.net.ftp.impl.DefaultFtpClientProvider;
import java.security.PrivilegedAction;
import java.util.ServiceConfigurationError;
import java.security.Permission;

public abstract class FtpClientProvider
{
    private static final Object lock;
    private static FtpClientProvider provider;
    
    public abstract FtpClient createFtpClient();
    
    protected FtpClientProvider() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new RuntimePermission("ftpClientProvider"));
        }
    }
    
    private static boolean loadProviderFromProperty() {
        final String property = System.getProperty("sun.net.ftpClientProvider");
        if (property == null) {
            return false;
        }
        try {
            FtpClientProvider.provider = (FtpClientProvider)Class.forName(property, true, null).newInstance();
            return true;
        }
        catch (final ClassNotFoundException | IllegalAccessException | InstantiationException | SecurityException ex) {
            throw new ServiceConfigurationError(((Throwable)ex).toString());
        }
    }
    
    private static boolean loadProviderAsService() {
        return false;
    }
    
    public static FtpClientProvider provider() {
        synchronized (FtpClientProvider.lock) {
            if (FtpClientProvider.provider != null) {
                return FtpClientProvider.provider;
            }
            return AccessController.doPrivileged((PrivilegedAction<FtpClientProvider>)new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    if (loadProviderFromProperty()) {
                        return FtpClientProvider.provider;
                    }
                    if (loadProviderAsService()) {
                        return FtpClientProvider.provider;
                    }
                    FtpClientProvider.provider = new DefaultFtpClientProvider();
                    return FtpClientProvider.provider;
                }
            });
        }
    }
    
    static {
        lock = new Object();
        FtpClientProvider.provider = null;
    }
}
