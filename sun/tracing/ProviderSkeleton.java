package sun.tracing;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import com.sun.tracing.ProviderName;
import com.sun.tracing.Probe;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.reflect.Method;
import java.util.HashMap;
import com.sun.tracing.Provider;
import java.lang.reflect.InvocationHandler;

public abstract class ProviderSkeleton implements InvocationHandler, Provider
{
    protected boolean active;
    protected Class<? extends Provider> providerType;
    protected HashMap<Method, ProbeSkeleton> probes;
    
    protected abstract ProbeSkeleton createProbe(final Method p0);
    
    protected ProviderSkeleton(final Class<? extends Provider> providerType) {
        this.active = false;
        this.providerType = providerType;
        this.probes = new HashMap<Method, ProbeSkeleton>();
    }
    
    public void init() {
        for (final Method method : AccessController.doPrivileged((PrivilegedAction<Method[]>)new PrivilegedAction<Method[]>() {
            @Override
            public Method[] run() {
                return ProviderSkeleton.this.providerType.getDeclaredMethods();
            }
        })) {
            if (method.getReturnType() != Void.TYPE) {
                throw new IllegalArgumentException("Return value of method is not void");
            }
            this.probes.put(method, this.createProbe(method));
        }
        this.active = true;
    }
    
    public <T extends Provider> T newProxyInstance() {
        return AccessController.doPrivileged((PrivilegedAction<T>)new PrivilegedAction<T>() {
            final /* synthetic */ InvocationHandler val$ih;
            
            @Override
            public T run() {
                return (T)Proxy.newProxyInstance(ProviderSkeleton.this.providerType.getClassLoader(), new Class[] { ProviderSkeleton.this.providerType }, this.val$ih);
            }
        });
    }
    
    @Override
    public Object invoke(final Object o, final Method method, final Object[] array) {
        final Class<?> declaringClass = method.getDeclaringClass();
        if (declaringClass != this.providerType) {
            try {
                if (declaringClass == Provider.class || declaringClass == Object.class) {
                    return method.invoke(this, array);
                }
                throw new SecurityException();
            }
            catch (final IllegalAccessException ex) {
                assert false;
                return null;
            }
            catch (final InvocationTargetException ex2) {
                assert false;
                return null;
            }
        }
        this.triggerProbe(method, array);
        return null;
    }
    
    @Override
    public Probe getProbe(final Method method) {
        return this.active ? ((Probe)this.probes.get(method)) : null;
    }
    
    @Override
    public void dispose() {
        this.active = false;
        this.probes.clear();
    }
    
    protected String getProviderName() {
        return getAnnotationString(this.providerType, ProviderName.class, this.providerType.getSimpleName());
    }
    
    protected static String getAnnotationString(final AnnotatedElement annotatedElement, final Class<? extends Annotation> clazz, final String s) {
        final String s2 = (String)getAnnotationValue(annotatedElement, clazz, "value", s);
        return s2.isEmpty() ? s : s2;
    }
    
    protected static Object getAnnotationValue(final AnnotatedElement annotatedElement, final Class<? extends Annotation> clazz, final String s, final Object o) {
        Object invoke = o;
        try {
            invoke = clazz.getMethod(s, (Class[])new Class[0]).invoke(annotatedElement.getAnnotation(clazz), new Object[0]);
        }
        catch (final NoSuchMethodException ex) {
            assert false;
        }
        catch (final IllegalAccessException ex2) {
            assert false;
        }
        catch (final InvocationTargetException ex3) {
            assert false;
        }
        catch (final NullPointerException ex4) {
            assert false;
        }
        return invoke;
    }
    
    protected void triggerProbe(final Method method, final Object[] array) {
        if (this.active) {
            final ProbeSkeleton probeSkeleton = this.probes.get(method);
            if (probeSkeleton != null) {
                probeSkeleton.uncheckedTrigger(array);
            }
        }
    }
}
