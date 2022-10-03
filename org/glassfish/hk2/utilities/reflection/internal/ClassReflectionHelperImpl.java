package org.glassfish.hk2.utilities.reflection.internal;

import java.util.Iterator;
import org.glassfish.hk2.utilities.cache.ComputationErrorException;
import org.glassfish.hk2.utilities.cache.HybridCacheEntry;
import org.glassfish.hk2.utilities.cache.Computable;
import java.lang.reflect.Field;
import org.glassfish.hk2.utilities.reflection.MethodWrapper;
import java.util.Set;
import java.lang.reflect.Method;
import org.glassfish.hk2.utilities.cache.LRUHybridCache;
import org.glassfish.hk2.utilities.reflection.ClassReflectionHelper;

public class ClassReflectionHelperImpl implements ClassReflectionHelper
{
    private final int MAX_CACHE_SIZE = 20000;
    private final LRUHybridCache<LifecycleKey, Method> postConstructCache;
    private final LRUHybridCache<LifecycleKey, Method> preDestroyCache;
    private final LRUHybridCache<Class<?>, Set<MethodWrapper>> methodCache;
    private final LRUHybridCache<Class<?>, Set<Field>> fieldCache;
    
    public ClassReflectionHelperImpl() {
        this.postConstructCache = new LRUHybridCache<LifecycleKey, Method>(20000, new Computable<LifecycleKey, HybridCacheEntry<Method>>() {
            @Override
            public HybridCacheEntry<Method> compute(final LifecycleKey key) {
                return ClassReflectionHelperImpl.this.postConstructCache.createCacheEntry(key, ClassReflectionHelperImpl.this.getPostConstructMethod(key.clazz, key.matchingClass), false);
            }
        });
        this.preDestroyCache = new LRUHybridCache<LifecycleKey, Method>(20000, new Computable<LifecycleKey, HybridCacheEntry<Method>>() {
            @Override
            public HybridCacheEntry<Method> compute(final LifecycleKey key) {
                return ClassReflectionHelperImpl.this.preDestroyCache.createCacheEntry(key, ClassReflectionHelperImpl.this.getPreDestroyMethod(key.clazz, key.matchingClass), false);
            }
        });
        this.methodCache = new LRUHybridCache<Class<?>, Set<MethodWrapper>>(20000, new Computable<Class<?>, HybridCacheEntry<Set<MethodWrapper>>>() {
            @Override
            public HybridCacheEntry<Set<MethodWrapper>> compute(final Class<?> key) {
                return ClassReflectionHelperImpl.this.methodCache.createCacheEntry(key, ClassReflectionHelperUtilities.getAllMethodWrappers(key), false);
            }
        });
        this.fieldCache = new LRUHybridCache<Class<?>, Set<Field>>(20000, new Computable<Class<?>, HybridCacheEntry<Set<Field>>>() {
            @Override
            public HybridCacheEntry<Set<Field>> compute(final Class<?> key) {
                return ClassReflectionHelperImpl.this.fieldCache.createCacheEntry(key, ClassReflectionHelperUtilities.getAllFieldWrappers(key), false);
            }
        });
    }
    
    @Override
    public Set<MethodWrapper> getAllMethods(final Class<?> clazz) {
        return this.methodCache.compute(clazz).getValue();
    }
    
    @Override
    public Set<Field> getAllFields(final Class<?> clazz) {
        return this.fieldCache.compute(clazz).getValue();
    }
    
    @Override
    public Method findPostConstruct(final Class<?> clazz, final Class<?> matchingClass) throws IllegalArgumentException {
        return this.postConstructCache.compute(new LifecycleKey((Class)clazz, (Class)matchingClass)).getValue();
    }
    
    @Override
    public Method findPreDestroy(final Class<?> clazz, final Class<?> matchingClass) throws IllegalArgumentException {
        return this.preDestroyCache.compute(new LifecycleKey((Class)clazz, (Class)matchingClass)).getValue();
    }
    
    @Override
    public void clean(Class<?> clazz) {
        while (clazz != null && !Object.class.equals(clazz)) {
            this.postConstructCache.remove(new LifecycleKey((Class)clazz, (Class)null));
            this.preDestroyCache.remove(new LifecycleKey((Class)clazz, (Class)null));
            this.methodCache.remove(clazz);
            this.fieldCache.remove(clazz);
            clazz = clazz.getSuperclass();
        }
    }
    
    @Override
    public MethodWrapper createMethodWrapper(final Method m) {
        return new MethodWrapperImpl(m);
    }
    
    @Override
    public void dispose() {
        this.postConstructCache.clear();
        this.preDestroyCache.clear();
        this.methodCache.clear();
        this.fieldCache.clear();
    }
    
    @Override
    public int size() {
        return this.postConstructCache.size() + this.preDestroyCache.size() + this.methodCache.size() + this.fieldCache.size();
    }
    
    private Method getPostConstructMethod(final Class<?> clazz, final Class<?> matchingClass) {
        if (clazz == null || Object.class.equals(clazz)) {
            return null;
        }
        if (matchingClass.isAssignableFrom(clazz)) {
            Method retVal;
            try {
                retVal = clazz.getMethod("postConstruct", (Class<?>[])new Class[0]);
            }
            catch (final NoSuchMethodException e) {
                retVal = null;
            }
            return retVal;
        }
        for (final MethodWrapper wrapper : this.getAllMethods(clazz)) {
            final Method m = wrapper.getMethod();
            if (ClassReflectionHelperUtilities.isPostConstruct(m)) {
                return m;
            }
        }
        return null;
    }
    
    private Method getPreDestroyMethod(final Class<?> clazz, final Class<?> matchingClass) {
        if (clazz == null || Object.class.equals(clazz)) {
            return null;
        }
        if (matchingClass.isAssignableFrom(clazz)) {
            Method retVal;
            try {
                retVal = clazz.getMethod("preDestroy", (Class<?>[])new Class[0]);
            }
            catch (final NoSuchMethodException e) {
                retVal = null;
            }
            return retVal;
        }
        for (final MethodWrapper wrapper : this.getAllMethods(clazz)) {
            final Method m = wrapper.getMethod();
            if (ClassReflectionHelperUtilities.isPreDestroy(m)) {
                return m;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "ClassReflectionHelperImpl(" + System.identityHashCode(this) + ")";
    }
    
    private static final class LifecycleKey
    {
        private final Class<?> clazz;
        private final Class<?> matchingClass;
        private final int hash;
        
        private LifecycleKey(final Class<?> clazz, final Class<?> matchingClass) {
            this.clazz = clazz;
            this.matchingClass = matchingClass;
            this.hash = clazz.hashCode();
        }
        
        @Override
        public int hashCode() {
            return this.hash;
        }
        
        @Override
        public boolean equals(final Object o) {
            return o != null && o instanceof LifecycleKey && this.clazz.equals(((LifecycleKey)o).clazz);
        }
    }
}
