package com.sun.xml.internal.ws.api.server;

import javax.xml.ws.Provider;
import java.lang.reflect.Method;
import com.sun.xml.internal.ws.server.ServerRtException;
import com.sun.xml.internal.ws.resources.WsservletMessages;
import java.util.logging.Level;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.sun.xml.internal.ws.server.SingletonResolver;
import javax.xml.ws.WebServiceContext;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;
import java.util.logging.Logger;

public abstract class InstanceResolver<T>
{
    private static final Logger logger;
    
    @NotNull
    public abstract T resolve(@NotNull final Packet p0);
    
    public void postInvoke(@NotNull final Packet request, @NotNull final T servant) {
    }
    
    public void start(@NotNull final WSWebServiceContext wsc, @NotNull final WSEndpoint endpoint) {
        this.start(wsc);
    }
    
    @Deprecated
    public void start(@NotNull final WebServiceContext wsc) {
    }
    
    public void dispose() {
    }
    
    public static <T> InstanceResolver<T> createSingleton(final T singleton) {
        assert singleton != null;
        InstanceResolver ir = createFromInstanceResolverAnnotation(singleton.getClass());
        if (ir == null) {
            ir = new SingletonResolver(singleton);
        }
        return ir;
    }
    
    @Deprecated
    public static <T> InstanceResolver<T> createDefault(@NotNull final Class<T> clazz, final boolean bool) {
        return createDefault(clazz);
    }
    
    public static <T> InstanceResolver<T> createDefault(@NotNull final Class<T> clazz) {
        InstanceResolver<T> ir = (InstanceResolver<T>)createFromInstanceResolverAnnotation((Class<Object>)clazz);
        if (ir == null) {
            ir = new SingletonResolver<T>(createNewInstance(clazz));
        }
        return ir;
    }
    
    public static <T> InstanceResolver<T> createFromInstanceResolverAnnotation(@NotNull final Class<T> clazz) {
        for (final Annotation a : clazz.getAnnotations()) {
            final InstanceResolverAnnotation ira = a.annotationType().getAnnotation(InstanceResolverAnnotation.class);
            if (ira != null) {
                final Class<? extends InstanceResolver> ir = ira.value();
                try {
                    return (InstanceResolver<T>)ir.getConstructor(Class.class).newInstance(clazz);
                }
                catch (final InstantiationException e) {
                    throw new WebServiceException(ServerMessages.FAILED_TO_INSTANTIATE_INSTANCE_RESOLVER(ir.getName(), a.annotationType(), clazz.getName()));
                }
                catch (final IllegalAccessException e2) {
                    throw new WebServiceException(ServerMessages.FAILED_TO_INSTANTIATE_INSTANCE_RESOLVER(ir.getName(), a.annotationType(), clazz.getName()));
                }
                catch (final InvocationTargetException e3) {
                    throw new WebServiceException(ServerMessages.FAILED_TO_INSTANTIATE_INSTANCE_RESOLVER(ir.getName(), a.annotationType(), clazz.getName()));
                }
                catch (final NoSuchMethodException e4) {
                    throw new WebServiceException(ServerMessages.FAILED_TO_INSTANTIATE_INSTANCE_RESOLVER(ir.getName(), a.annotationType(), clazz.getName()));
                }
            }
        }
        return null;
    }
    
    protected static <T> T createNewInstance(final Class<T> cl) {
        try {
            return cl.newInstance();
        }
        catch (final InstantiationException e) {
            InstanceResolver.logger.log(Level.SEVERE, e.getMessage(), e);
            throw new ServerRtException(WsservletMessages.ERROR_IMPLEMENTOR_FACTORY_NEW_INSTANCE_FAILED(cl), new Object[0]);
        }
        catch (final IllegalAccessException e2) {
            InstanceResolver.logger.log(Level.SEVERE, e2.getMessage(), e2);
            throw new ServerRtException(WsservletMessages.ERROR_IMPLEMENTOR_FACTORY_NEW_INSTANCE_FAILED(cl), new Object[0]);
        }
    }
    
    @NotNull
    public Invoker createInvoker() {
        return new Invoker() {
            @Override
            public void start(@NotNull final WSWebServiceContext wsc, @NotNull final WSEndpoint endpoint) {
                InstanceResolver.this.start(wsc, endpoint);
            }
            
            @Override
            public void dispose() {
                InstanceResolver.this.dispose();
            }
            
            @Override
            public Object invoke(final Packet p, final Method m, final Object... args) throws InvocationTargetException, IllegalAccessException {
                final T t = InstanceResolver.this.resolve(p);
                try {
                    return MethodUtil.invoke(t, m, args);
                }
                finally {
                    InstanceResolver.this.postInvoke(p, t);
                }
            }
            
            @Override
            public <U> U invokeProvider(@NotNull final Packet p, final U arg) {
                final T t = InstanceResolver.this.resolve(p);
                try {
                    return ((Provider)t).invoke(arg);
                }
                finally {
                    InstanceResolver.this.postInvoke(p, t);
                }
            }
            
            @Override
            public String toString() {
                return "Default Invoker over " + InstanceResolver.this.toString();
            }
        };
    }
    
    static {
        logger = Logger.getLogger("com.sun.xml.internal.ws.server");
    }
}
