package com.sun.xml.internal.ws.api.client;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.util.ArrayList;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.WSService;
import java.util.Set;

public abstract class ServiceInterceptorFactory
{
    private static ThreadLocal<Set<ServiceInterceptorFactory>> threadLocalFactories;
    
    public abstract ServiceInterceptor create(@NotNull final WSService p0);
    
    @NotNull
    public static ServiceInterceptor load(@NotNull final WSService service, @Nullable final ClassLoader cl) {
        final List<ServiceInterceptor> l = new ArrayList<ServiceInterceptor>();
        for (final ServiceInterceptorFactory f : ServiceFinder.find(ServiceInterceptorFactory.class)) {
            l.add(f.create(service));
        }
        for (final ServiceInterceptorFactory f : ServiceInterceptorFactory.threadLocalFactories.get()) {
            l.add(f.create(service));
        }
        return ServiceInterceptor.aggregate((ServiceInterceptor[])l.toArray(new ServiceInterceptor[l.size()]));
    }
    
    public static boolean registerForThread(final ServiceInterceptorFactory factory) {
        return ServiceInterceptorFactory.threadLocalFactories.get().add(factory);
    }
    
    public static boolean unregisterForThread(final ServiceInterceptorFactory factory) {
        return ServiceInterceptorFactory.threadLocalFactories.get().remove(factory);
    }
    
    static {
        ServiceInterceptorFactory.threadLocalFactories = new ThreadLocal<Set<ServiceInterceptorFactory>>() {
            @Override
            protected Set<ServiceInterceptorFactory> initialValue() {
                return new HashSet<ServiceInterceptorFactory>();
            }
        };
    }
}
