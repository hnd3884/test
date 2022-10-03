package sun.security.jca;

import java.security.GeneralSecurityException;
import sun.security.util.PropertyExpander;
import java.security.ProviderException;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.io.File;
import java.security.PrivilegedAction;
import java.security.Provider;
import sun.security.util.Debug;

final class ProviderConfig
{
    private static final Debug debug;
    private static final String P11_SOL_NAME = "sun.security.pkcs11.SunPKCS11";
    private static final String P11_SOL_ARG = "${java.home}/lib/security/sunpkcs11-solaris.cfg";
    private static final int MAX_LOAD_TRIES = 30;
    private static final Class[] CL_STRING;
    private final String className;
    private final String argument;
    private int tries;
    private volatile Provider provider;
    private boolean isLoading;
    
    ProviderConfig(final String className, final String s) {
        if (className.equals("sun.security.pkcs11.SunPKCS11") && s.equals("${java.home}/lib/security/sunpkcs11-solaris.cfg")) {
            this.checkSunPKCS11Solaris();
        }
        this.className = className;
        this.argument = expand(s);
    }
    
    ProviderConfig(final String s) {
        this(s, "");
    }
    
    ProviderConfig(final Provider provider) {
        this.className = provider.getClass().getName();
        this.argument = "";
        this.provider = provider;
    }
    
    private void checkSunPKCS11Solaris() {
        if (AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                if (!new File("/usr/lib/libpkcs11.so").exists()) {
                    return Boolean.FALSE;
                }
                if ("false".equalsIgnoreCase(System.getProperty("sun.security.pkcs11.enable-solaris"))) {
                    return Boolean.FALSE;
                }
                return Boolean.TRUE;
            }
        }) == Boolean.FALSE) {
            this.tries = 30;
        }
    }
    
    private boolean hasArgument() {
        return this.argument.length() != 0;
    }
    
    private boolean shouldLoad() {
        return this.tries < 30;
    }
    
    private void disableLoad() {
        this.tries = 30;
    }
    
    boolean isLoaded() {
        return this.provider != null;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProviderConfig)) {
            return false;
        }
        final ProviderConfig providerConfig = (ProviderConfig)o;
        return this.className.equals(providerConfig.className) && this.argument.equals(providerConfig.argument);
    }
    
    @Override
    public int hashCode() {
        return this.className.hashCode() + this.argument.hashCode();
    }
    
    @Override
    public String toString() {
        if (this.hasArgument()) {
            return this.className + "('" + this.argument + "')";
        }
        return this.className;
    }
    
    synchronized Provider getProvider() {
        Provider provider = this.provider;
        if (provider != null) {
            return provider;
        }
        if (!this.shouldLoad()) {
            return null;
        }
        if (this.isLoading) {
            if (ProviderConfig.debug != null) {
                ProviderConfig.debug.println("Recursion loading provider: " + this);
                new Exception("Call trace").printStackTrace();
            }
            return null;
        }
        try {
            this.isLoading = true;
            ++this.tries;
            provider = this.doLoadProvider();
        }
        finally {
            this.isLoading = false;
        }
        return this.provider = provider;
    }
    
    private Provider doLoadProvider() {
        return AccessController.doPrivileged((PrivilegedAction<Provider>)new PrivilegedAction<Provider>() {
            @Override
            public Provider run() {
                if (ProviderConfig.debug != null) {
                    ProviderConfig.debug.println("Loading provider: " + ProviderConfig.this);
                }
                try {
                    final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
                    Class<?> clazz;
                    if (systemClassLoader != null) {
                        clazz = systemClassLoader.loadClass(ProviderConfig.this.className);
                    }
                    else {
                        clazz = Class.forName(ProviderConfig.this.className);
                    }
                    Object o;
                    if (!ProviderConfig.this.hasArgument()) {
                        o = clazz.newInstance();
                    }
                    else {
                        o = clazz.getConstructor((Class<?>[])ProviderConfig.CL_STRING).newInstance(ProviderConfig.this.argument);
                    }
                    if (o instanceof Provider) {
                        if (ProviderConfig.debug != null) {
                            ProviderConfig.debug.println("Loaded provider " + o);
                        }
                        return (Provider)o;
                    }
                    if (ProviderConfig.debug != null) {
                        ProviderConfig.debug.println(ProviderConfig.this.className + " is not a provider");
                    }
                    ProviderConfig.this.disableLoad();
                    return null;
                }
                catch (final Exception ex) {
                    Throwable cause;
                    if (ex instanceof InvocationTargetException) {
                        cause = ((InvocationTargetException)ex).getCause();
                    }
                    else {
                        cause = ex;
                    }
                    if (ProviderConfig.debug != null) {
                        ProviderConfig.debug.println("Error loading provider " + ProviderConfig.this);
                        cause.printStackTrace();
                    }
                    if (cause instanceof ProviderException) {
                        throw (ProviderException)cause;
                    }
                    if (cause instanceof UnsupportedOperationException) {
                        ProviderConfig.this.disableLoad();
                    }
                    return null;
                }
                catch (final ExceptionInInitializerError exceptionInInitializerError) {
                    if (ProviderConfig.debug != null) {
                        ProviderConfig.debug.println("Error loading provider " + ProviderConfig.this);
                        exceptionInInitializerError.printStackTrace();
                    }
                    ProviderConfig.this.disableLoad();
                    return null;
                }
            }
        });
    }
    
    private static String expand(final String s) {
        if (!s.contains("${")) {
            return s;
        }
        return AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                try {
                    return PropertyExpander.expand(s);
                }
                catch (final GeneralSecurityException ex) {
                    throw new ProviderException(ex);
                }
            }
        });
    }
    
    static {
        debug = Debug.getInstance("jca", "ProviderConfig");
        CL_STRING = new Class[] { String.class };
    }
}
