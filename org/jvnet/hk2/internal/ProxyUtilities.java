package org.jvnet.hk2.internal;

import org.glassfish.hk2.api.MultiException;
import java.lang.reflect.Type;
import java.util.Set;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.ActiveDescriptor;
import javassist.util.proxy.ProxyObject;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import org.glassfish.hk2.api.ProxyCtl;
import javassist.util.proxy.ProxyFactory;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.glassfish.hk2.api.ServiceLocator;
import javassist.util.proxy.MethodHandler;
import java.util.HashMap;

public class ProxyUtilities
{
    private static final Object proxyCreationLock;
    private final HashMap<ClassLoader, DelegatingClassLoader> superClassToDelegator;
    
    public ProxyUtilities() {
        this.superClassToDelegator = new HashMap<ClassLoader, DelegatingClassLoader>();
    }
    
    private <T> T secureCreate(final Class<?> superclass, final Class<?>[] interfaces, final MethodHandler callback, final boolean useJDKProxy, final ServiceLocator anchor) {
        final ClassLoader loader = AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                ClassLoader retVal = superclass.getClassLoader();
                if (retVal == null) {
                    try {
                        retVal = ClassLoader.getSystemClassLoader();
                    }
                    catch (final SecurityException se) {
                        throw new IllegalStateException("Insufficient privilege to get system classloader while looking for classloader of " + superclass.getName(), se);
                    }
                }
                if (retVal == null) {
                    throw new IllegalStateException("Could not find system classloader or classloader of " + superclass.getName());
                }
                return retVal;
            }
        });
        DelegatingClassLoader initDelegatingLoader;
        synchronized (this.superClassToDelegator) {
            initDelegatingLoader = this.superClassToDelegator.get(loader);
            if (initDelegatingLoader == null) {
                initDelegatingLoader = AccessController.doPrivileged((PrivilegedAction<DelegatingClassLoader>)new PrivilegedAction<DelegatingClassLoader>() {
                    @Override
                    public DelegatingClassLoader run() {
                        return new DelegatingClassLoader(loader, new ClassLoader[] { ProxyFactory.class.getClassLoader(), ProxyCtl.class.getClassLoader() });
                    }
                });
                this.superClassToDelegator.put(loader, initDelegatingLoader);
            }
        }
        final DelegatingClassLoader delegatingLoader = initDelegatingLoader;
        if (useJDKProxy) {
            return AccessController.doPrivileged((PrivilegedAction<T>)new PrivilegedAction<T>() {
                @Override
                public T run() {
                    return (T)Proxy.newProxyInstance(delegatingLoader, interfaces, new MethodInterceptorInvocationHandler(callback));
                }
            });
        }
        return AccessController.doPrivileged((PrivilegedAction<T>)new PrivilegedAction<T>() {
            @Override
            public T run() {
                synchronized (ProxyUtilities.proxyCreationLock) {
                    final ProxyFactory.ClassLoaderProvider originalProvider = ProxyFactory.classLoaderProvider;
                    ProxyFactory.classLoaderProvider = (ProxyFactory.ClassLoaderProvider)new ProxyFactory.ClassLoaderProvider() {
                        public ClassLoader get(final ProxyFactory arg0) {
                            return delegatingLoader;
                        }
                    };
                    try {
                        final ProxyFactory proxyFactory = new ProxyFactory();
                        proxyFactory.setInterfaces(interfaces);
                        proxyFactory.setSuperclass(superclass);
                        final Class<?> proxyClass = proxyFactory.createClass();
                        try {
                            final T proxy = (T)proxyClass.newInstance();
                            ((ProxyObject)proxy).setHandler(callback);
                            return proxy;
                        }
                        catch (final Exception e1) {
                            throw new RuntimeException(e1);
                        }
                    }
                    finally {
                        ProxyFactory.classLoaderProvider = originalProvider;
                    }
                }
            }
        });
    }
    
    public <T> T generateProxy(final Class<?> requestedClass, final ServiceLocatorImpl locator, final ActiveDescriptor<T> root, final ServiceHandleImpl<T> handle, final Injectee injectee) {
        final boolean isInterface = requestedClass != null && requestedClass.isInterface();
        Class<?> proxyClass;
        Class<?>[] iFaces;
        if (isInterface) {
            proxyClass = requestedClass;
            iFaces = new Class[] { proxyClass, ProxyCtl.class };
        }
        else {
            proxyClass = Utilities.getFactoryAwareImplementationClass(root);
            iFaces = Utilities.getInterfacesForProxy(root.getContractTypes());
        }
        T proxy;
        try {
            proxy = this.secureCreate(proxyClass, iFaces, (MethodHandler)new MethodInterceptorImpl(locator, root, handle, injectee), isInterface, (ServiceLocator)locator);
        }
        catch (final Throwable th) {
            final Exception addMe = new IllegalArgumentException("While attempting to create a Proxy for " + proxyClass.getName() + " in scope " + root.getScope() + " an error occured while creating the proxy");
            if (th instanceof MultiException) {
                final MultiException me = (MultiException)th;
                me.addError((Throwable)addMe);
                throw me;
            }
            final MultiException me = new MultiException(th);
            me.addError((Throwable)addMe);
            throw me;
        }
        return proxy;
    }
    
    public void releaseCache() {
        synchronized (this.superClassToDelegator) {
            this.superClassToDelegator.clear();
        }
    }
    
    static {
        proxyCreationLock = new Object();
    }
}
