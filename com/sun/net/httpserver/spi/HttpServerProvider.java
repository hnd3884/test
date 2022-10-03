package com.sun.net.httpserver.spi;

import java.security.AccessController;
import sun.net.httpserver.DefaultHttpServerProvider;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.ServiceConfigurationError;
import java.security.Permission;
import com.sun.net.httpserver.HttpsServer;
import java.io.IOException;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import jdk.Exported;

@Exported
public abstract class HttpServerProvider
{
    private static final Object lock;
    private static HttpServerProvider provider;
    
    public abstract HttpServer createHttpServer(final InetSocketAddress p0, final int p1) throws IOException;
    
    public abstract HttpsServer createHttpsServer(final InetSocketAddress p0, final int p1) throws IOException;
    
    protected HttpServerProvider() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new RuntimePermission("httpServerProvider"));
        }
    }
    
    private static boolean loadProviderFromProperty() {
        final String property = System.getProperty("com.sun.net.httpserver.HttpServerProvider");
        if (property == null) {
            return false;
        }
        try {
            HttpServerProvider.provider = (HttpServerProvider)Class.forName(property, true, ClassLoader.getSystemClassLoader()).newInstance();
            return true;
        }
        catch (final ClassNotFoundException | IllegalAccessException | InstantiationException | SecurityException ex) {
            throw new ServiceConfigurationError(null, (Throwable)ex);
        }
    }
    
    private static boolean loadProviderAsService() {
        final Iterator<HttpServerProvider> iterator = ServiceLoader.load(HttpServerProvider.class, ClassLoader.getSystemClassLoader()).iterator();
        while (true) {
            try {
                if (!iterator.hasNext()) {
                    return false;
                }
                HttpServerProvider.provider = iterator.next();
                return true;
            }
            catch (final ServiceConfigurationError serviceConfigurationError) {
                if (serviceConfigurationError.getCause() instanceof SecurityException) {
                    continue;
                }
                throw serviceConfigurationError;
            }
            break;
        }
    }
    
    public static HttpServerProvider provider() {
        synchronized (HttpServerProvider.lock) {
            if (HttpServerProvider.provider != null) {
                return HttpServerProvider.provider;
            }
            return AccessController.doPrivileged((PrivilegedAction<HttpServerProvider>)new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    if (loadProviderFromProperty()) {
                        return HttpServerProvider.provider;
                    }
                    if (loadProviderAsService()) {
                        return HttpServerProvider.provider;
                    }
                    HttpServerProvider.provider = new DefaultHttpServerProvider();
                    return HttpServerProvider.provider;
                }
            });
        }
    }
    
    static {
        lock = new Object();
        HttpServerProvider.provider = null;
    }
}
