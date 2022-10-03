package org.jvnet.hk2.internal;

import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;
import org.glassfish.hk2.api.ProxyCtl;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceHandle;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.glassfish.hk2.api.Injectee;
import java.lang.ref.WeakReference;
import org.glassfish.hk2.api.ActiveDescriptor;
import javassist.util.proxy.MethodHandler;

public class MethodInterceptorImpl implements MethodHandler
{
    private static final String PROXY_MORE_METHOD_NAME = "__make";
    private final ServiceLocatorImpl locator;
    private final ActiveDescriptor<?> descriptor;
    private final ServiceHandleImpl<?> root;
    private final WeakReference<Injectee> myInjectee;
    private static final String EQUALS_NAME = "equals";
    
    MethodInterceptorImpl(final ServiceLocatorImpl sli, final ActiveDescriptor<?> descriptor, final ServiceHandleImpl<?> root, final Injectee injectee) {
        this.locator = sli;
        this.descriptor = descriptor;
        this.root = root;
        if (injectee != null) {
            this.myInjectee = new WeakReference<Injectee>(injectee);
        }
        else {
            this.myInjectee = null;
        }
    }
    
    private Object internalInvoke(final Object target, final Method method, final Method proceed, Object[] params) throws Throwable {
        final Context<?> context = this.locator.resolveContext(this.descriptor.getScopeAnnotation());
        final Object service = context.findOrCreate((ActiveDescriptor)this.descriptor, (ServiceHandle)this.root);
        if (service == null) {
            throw new MultiException((Throwable)new IllegalStateException("Proxiable context " + context + " findOrCreate returned a null for descriptor " + this.descriptor + " and handle " + this.root));
        }
        if (method.getName().equals("__make")) {
            return service;
        }
        if (isEquals(method) && params.length == 1 && params[0] != null && params[0] instanceof ProxyCtl) {
            final ProxyCtl equalsProxy = (ProxyCtl)params[0];
            params = new Object[] { equalsProxy.__make() };
        }
        return ReflectionHelper.invoke(service, method, params, this.locator.getNeutralContextClassLoader());
    }
    
    public Object invoke(final Object target, final Method method, final Method proceed, final Object[] params) throws Throwable {
        boolean pushed = false;
        if (this.root != null && this.myInjectee != null) {
            final Injectee ref = this.myInjectee.get();
            if (ref != null) {
                this.root.pushInjectee(ref);
                pushed = true;
            }
        }
        try {
            return this.internalInvoke(target, method, proceed, params);
        }
        finally {
            if (pushed) {
                this.root.popInjectee();
            }
        }
    }
    
    private static boolean isEquals(final Method m) {
        if (!m.getName().equals("equals")) {
            return false;
        }
        final Class<?>[] params = m.getParameterTypes();
        return params != null && params.length == 1 && Object.class.equals(params[0]);
    }
}
