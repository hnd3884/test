package org.apache.lucene.util;

import java.util.Collections;
import java.util.HashSet;
import java.lang.reflect.Method;
import java.util.Set;

public final class VirtualMethod<C>
{
    private static final Set<Method> singletonSet;
    private final Class<C> baseClass;
    private final String method;
    private final Class<?>[] parameters;
    private final ClassValue<Integer> distanceOfClass;
    
    public VirtualMethod(final Class<C> baseClass, final String method, final Class<?>... parameters) {
        this.distanceOfClass = new ClassValue<Integer>() {
            @Override
            protected Integer computeValue(final Class<?> subclazz) {
                return VirtualMethod.this.reflectImplementationDistance(subclazz);
            }
        };
        this.baseClass = baseClass;
        this.method = method;
        this.parameters = parameters;
        try {
            if (!VirtualMethod.singletonSet.add(baseClass.getDeclaredMethod(method, parameters))) {
                throw new UnsupportedOperationException("VirtualMethod instances must be singletons and therefore assigned to static final members in the same class, they use as baseClass ctor param.");
            }
        }
        catch (final NoSuchMethodException nsme) {
            throw new IllegalArgumentException(baseClass.getName() + " has no such method: " + nsme.getMessage());
        }
    }
    
    public int getImplementationDistance(final Class<? extends C> subclazz) {
        return this.distanceOfClass.get(subclazz);
    }
    
    public boolean isOverriddenAsOf(final Class<? extends C> subclazz) {
        return this.getImplementationDistance(subclazz) > 0;
    }
    
    int reflectImplementationDistance(final Class<?> subclazz) {
        if (!this.baseClass.isAssignableFrom(subclazz)) {
            throw new IllegalArgumentException(subclazz.getName() + " is not a subclass of " + this.baseClass.getName());
        }
        boolean overridden = false;
        int distance = 0;
        for (Class<?> clazz = subclazz; clazz != this.baseClass && clazz != null; clazz = clazz.getSuperclass()) {
            if (!overridden) {
                try {
                    clazz.getDeclaredMethod(this.method, this.parameters);
                    overridden = true;
                }
                catch (final NoSuchMethodException ex) {}
            }
            if (overridden) {
                ++distance;
            }
        }
        return distance;
    }
    
    public static <C> int compareImplementationDistance(final Class<? extends C> clazz, final VirtualMethod<C> m1, final VirtualMethod<C> m2) {
        return Integer.valueOf(m1.getImplementationDistance(clazz)).compareTo(m2.getImplementationDistance(clazz));
    }
    
    static {
        singletonSet = Collections.synchronizedSet(new HashSet<Method>());
    }
}
