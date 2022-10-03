package com.sun.xml.internal.ws.api.server;

import com.sun.xml.internal.ws.resources.ServerMessages;
import java.lang.annotation.Annotation;
import java.security.AccessController;
import java.lang.reflect.InvocationTargetException;
import com.sun.xml.internal.ws.server.ServerRtException;
import java.security.PrivilegedAction;
import com.sun.istack.internal.Nullable;
import java.lang.reflect.Method;

public abstract class AbstractInstanceResolver<T> extends InstanceResolver<T>
{
    protected static ResourceInjector getResourceInjector(final WSEndpoint endpoint) {
        ResourceInjector ri = endpoint.getContainer().getSPI(ResourceInjector.class);
        if (ri == null) {
            ri = ResourceInjector.STANDALONE;
        }
        return ri;
    }
    
    protected static void invokeMethod(@Nullable final Method method, final Object instance, final Object... args) {
        if (method == null) {
            return;
        }
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                try {
                    if (!method.isAccessible()) {
                        method.setAccessible(true);
                    }
                    MethodUtil.invoke(instance, method, args);
                }
                catch (final IllegalAccessException e) {
                    throw new ServerRtException("server.rt.err", new Object[] { e });
                }
                catch (final InvocationTargetException e2) {
                    throw new ServerRtException("server.rt.err", new Object[] { e2 });
                }
                return null;
            }
        });
    }
    
    @Nullable
    protected final Method findAnnotatedMethod(final Class clazz, final Class<? extends Annotation> annType) {
        boolean once = false;
        Method r = null;
        for (final Method method : clazz.getDeclaredMethods()) {
            if (method.getAnnotation(annType) != null) {
                if (once) {
                    throw new ServerRtException(ServerMessages.ANNOTATION_ONLY_ONCE(annType), new Object[0]);
                }
                if (method.getParameterTypes().length != 0) {
                    throw new ServerRtException(ServerMessages.NOT_ZERO_PARAMETERS(method), new Object[0]);
                }
                r = method;
                once = true;
            }
        }
        return r;
    }
}
