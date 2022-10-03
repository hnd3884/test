package javax.security.auth.login;

import java.security.Provider;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import sun.security.jca.GetInstance;
import java.security.PrivilegedActionException;
import java.util.Objects;
import java.security.PrivilegedExceptionAction;
import java.security.Security;
import java.security.PrivilegedAction;
import java.security.AccessController;
import java.security.Permission;
import javax.security.auth.AuthPermission;
import java.security.AccessControlContext;

public abstract class Configuration
{
    private static Configuration configuration;
    private final AccessControlContext acc;
    
    private static void checkPermission(final String s) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new AuthPermission("createLoginConfiguration." + s));
        }
    }
    
    protected Configuration() {
        this.acc = AccessController.getContext();
    }
    
    public static Configuration getConfiguration() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new AuthPermission("getLoginConfiguration"));
        }
        synchronized (Configuration.class) {
            if (Configuration.configuration == null) {
                String s = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
                    @Override
                    public String run() {
                        return Security.getProperty("login.configuration.provider");
                    }
                });
                if (s == null) {
                    s = "sun.security.provider.ConfigFile";
                }
                try {
                    final Configuration configuration = AccessController.doPrivileged((PrivilegedExceptionAction<Configuration>)new PrivilegedExceptionAction<Configuration>() {
                        @Override
                        public Configuration run() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
                            return (Configuration)Class.forName(s, false, Thread.currentThread().getContextClassLoader()).asSubclass(Configuration.class).newInstance();
                        }
                    });
                    AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                        @Override
                        public Void run() {
                            Configuration.setConfiguration(configuration);
                            return null;
                        }
                    }, Objects.requireNonNull(configuration.acc));
                }
                catch (final PrivilegedActionException ex) {
                    final Exception exception = ex.getException();
                    if (exception instanceof InstantiationException) {
                        throw (SecurityException)new SecurityException("Configuration error:" + exception.getCause().getMessage() + "\n").initCause(exception.getCause());
                    }
                    throw (SecurityException)new SecurityException("Configuration error: " + exception.toString() + "\n").initCause(exception);
                }
            }
            return Configuration.configuration;
        }
    }
    
    public static void setConfiguration(final Configuration configuration) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new AuthPermission("setLoginConfiguration"));
        }
        Configuration.configuration = configuration;
    }
    
    public static Configuration getInstance(final String s, final Parameters parameters) throws NoSuchAlgorithmException {
        checkPermission(s);
        try {
            final GetInstance.Instance instance = GetInstance.getInstance("Configuration", ConfigurationSpi.class, s, parameters);
            return new ConfigDelegate((ConfigurationSpi)instance.impl, instance.provider, s, parameters);
        }
        catch (final NoSuchAlgorithmException ex) {
            return handleException(ex);
        }
    }
    
    public static Configuration getInstance(final String s, final Parameters parameters, final String s2) throws NoSuchProviderException, NoSuchAlgorithmException {
        if (s2 == null || s2.length() == 0) {
            throw new IllegalArgumentException("missing provider");
        }
        checkPermission(s);
        try {
            final GetInstance.Instance instance = GetInstance.getInstance("Configuration", ConfigurationSpi.class, s, parameters, s2);
            return new ConfigDelegate((ConfigurationSpi)instance.impl, instance.provider, s, parameters);
        }
        catch (final NoSuchAlgorithmException ex) {
            return handleException(ex);
        }
    }
    
    public static Configuration getInstance(final String s, final Parameters parameters, final Provider provider) throws NoSuchAlgorithmException {
        if (provider == null) {
            throw new IllegalArgumentException("missing provider");
        }
        checkPermission(s);
        try {
            final GetInstance.Instance instance = GetInstance.getInstance("Configuration", ConfigurationSpi.class, s, parameters, provider);
            return new ConfigDelegate((ConfigurationSpi)instance.impl, instance.provider, s, parameters);
        }
        catch (final NoSuchAlgorithmException ex) {
            return handleException(ex);
        }
    }
    
    private static Configuration handleException(final NoSuchAlgorithmException ex) throws NoSuchAlgorithmException {
        final Throwable cause = ex.getCause();
        if (cause instanceof IllegalArgumentException) {
            throw (IllegalArgumentException)cause;
        }
        throw ex;
    }
    
    public Provider getProvider() {
        return null;
    }
    
    public String getType() {
        return null;
    }
    
    public Parameters getParameters() {
        return null;
    }
    
    public abstract AppConfigurationEntry[] getAppConfigurationEntry(final String p0);
    
    public void refresh() {
    }
    
    private static class ConfigDelegate extends Configuration
    {
        private ConfigurationSpi spi;
        private Provider p;
        private String type;
        private Parameters params;
        
        private ConfigDelegate(final ConfigurationSpi spi, final Provider p4, final String type, final Parameters params) {
            this.spi = spi;
            this.p = p4;
            this.type = type;
            this.params = params;
        }
        
        @Override
        public String getType() {
            return this.type;
        }
        
        @Override
        public Parameters getParameters() {
            return this.params;
        }
        
        @Override
        public Provider getProvider() {
            return this.p;
        }
        
        @Override
        public AppConfigurationEntry[] getAppConfigurationEntry(final String s) {
            return this.spi.engineGetAppConfigurationEntry(s);
        }
        
        @Override
        public void refresh() {
            this.spi.engineRefresh();
        }
    }
    
    public interface Parameters
    {
    }
}
