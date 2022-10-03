package org.glassfish.jersey.server.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Arrays;
import java.security.PrivilegedAction;
import java.security.AccessController;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Method;
import java.util.Collection;

public final class MethodList implements Iterable<AnnotatedMethod>
{
    private AnnotatedMethod[] methods;
    
    public MethodList(final Class<?> c) {
        this(c, false);
    }
    
    public MethodList(final Class<?> c, final boolean declaredMethods) {
        this(declaredMethods ? getAllDeclaredMethods(c) : getMethods(c));
    }
    
    private static List<Method> getAllDeclaredMethods(Class<?> c) {
        final List<Method> l = new ArrayList<Method>();
        while (c != null && c != Object.class) {
            l.addAll(AccessController.doPrivileged((PrivilegedAction<Collection<? extends Method>>)ReflectionHelper.getDeclaredMethodsPA((Class)c)));
            c = c.getSuperclass();
        }
        return l;
    }
    
    private static List<Method> getMethods(final Class<?> c) {
        return Arrays.asList(c.getMethods());
    }
    
    public MethodList(final Collection<Method> methods) {
        final List<AnnotatedMethod> l = new ArrayList<AnnotatedMethod>(methods.size());
        for (final Method m : methods) {
            if (!m.isBridge() && m.getDeclaringClass() != Object.class) {
                l.add(new AnnotatedMethod(m));
            }
        }
        this.methods = new AnnotatedMethod[l.size()];
        this.methods = l.toArray(this.methods);
    }
    
    public MethodList(final Method... methods) {
        this(Arrays.asList(methods));
    }
    
    public MethodList(final AnnotatedMethod... methods) {
        this.methods = methods;
    }
    
    @Override
    public Iterator<AnnotatedMethod> iterator() {
        return Arrays.asList(this.methods).iterator();
    }
    
    public MethodList isNotPublic() {
        return this.filter(new Filter() {
            @Override
            public boolean keep(final AnnotatedMethod m) {
                return !Modifier.isPublic(m.getMethod().getModifiers());
            }
        });
    }
    
    public MethodList hasNumParams(final int paramCount) {
        return this.filter(new Filter() {
            @Override
            public boolean keep(final AnnotatedMethod m) {
                return m.getParameterTypes().length == paramCount;
            }
        });
    }
    
    public MethodList hasReturnType(final Class<?> returnType) {
        return this.filter(new Filter() {
            @Override
            public boolean keep(final AnnotatedMethod m) {
                return m.getMethod().getReturnType() == returnType;
            }
        });
    }
    
    public MethodList nameStartsWith(final String prefix) {
        return this.filter(new Filter() {
            @Override
            public boolean keep(final AnnotatedMethod m) {
                return m.getMethod().getName().startsWith(prefix);
            }
        });
    }
    
    public <T extends Annotation> MethodList withAnnotation(final Class<T> annotation) {
        return this.filter(new Filter() {
            @Override
            public boolean keep(final AnnotatedMethod m) {
                return m.getAnnotation((Class<Annotation>)annotation) != null;
            }
        });
    }
    
    public <T extends Annotation> MethodList withMetaAnnotation(final Class<T> annotation) {
        return this.filter(new Filter() {
            @Override
            public boolean keep(final AnnotatedMethod m) {
                for (final Annotation a : m.getAnnotations()) {
                    if (a.annotationType().getAnnotation((Class<Annotation>)annotation) != null) {
                        return true;
                    }
                }
                return false;
            }
        });
    }
    
    public <T extends Annotation> MethodList withoutAnnotation(final Class<T> annotation) {
        return this.filter(new Filter() {
            @Override
            public boolean keep(final AnnotatedMethod m) {
                return m.getAnnotation((Class<Annotation>)annotation) == null;
            }
        });
    }
    
    public <T extends Annotation> MethodList withoutMetaAnnotation(final Class<T> annotation) {
        return this.filter(new Filter() {
            @Override
            public boolean keep(final AnnotatedMethod m) {
                for (final Annotation a : m.getAnnotations()) {
                    if (a.annotationType().getAnnotation((Class<Annotation>)annotation) != null) {
                        return false;
                    }
                }
                return true;
            }
        });
    }
    
    public MethodList filter(final Filter filter) {
        final List<AnnotatedMethod> result = new ArrayList<AnnotatedMethod>();
        for (final AnnotatedMethod m : this.methods) {
            if (filter.keep(m)) {
                result.add(m);
            }
        }
        return new MethodList((AnnotatedMethod[])result.toArray(new AnnotatedMethod[result.size()]));
    }
    
    public interface Filter
    {
        boolean keep(final AnnotatedMethod p0);
    }
}
