package javax.imageio.spi;

import java.util.ArrayList;
import java.util.ServiceLoader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ServiceRegistry
{
    private Map categoryMap;
    
    public ServiceRegistry(final Iterator<Class<?>> iterator) {
        this.categoryMap = new HashMap();
        if (iterator == null) {
            throw new IllegalArgumentException("categories == null!");
        }
        while (iterator.hasNext()) {
            final Class clazz = iterator.next();
            this.categoryMap.put(clazz, new SubRegistry(this, clazz));
        }
    }
    
    public static <T> Iterator<T> lookupProviders(final Class<T> clazz, final ClassLoader classLoader) {
        if (clazz == null) {
            throw new IllegalArgumentException("providerClass == null!");
        }
        return ServiceLoader.load(clazz, classLoader).iterator();
    }
    
    public static <T> Iterator<T> lookupProviders(final Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("providerClass == null!");
        }
        return ServiceLoader.load(clazz).iterator();
    }
    
    public Iterator<Class<?>> getCategories() {
        return this.categoryMap.keySet().iterator();
    }
    
    private Iterator getSubRegistries(final Object o) {
        final ArrayList list = new ArrayList();
        for (final Class clazz : this.categoryMap.keySet()) {
            if (clazz.isAssignableFrom(o.getClass())) {
                list.add(this.categoryMap.get(clazz));
            }
        }
        return list.iterator();
    }
    
    public <T> boolean registerServiceProvider(final T t, final Class<T> clazz) {
        if (t == null) {
            throw new IllegalArgumentException("provider == null!");
        }
        final SubRegistry subRegistry = this.categoryMap.get(clazz);
        if (subRegistry == null) {
            throw new IllegalArgumentException("category unknown!");
        }
        if (!clazz.isAssignableFrom(t.getClass())) {
            throw new ClassCastException();
        }
        return subRegistry.registerServiceProvider(t);
    }
    
    public void registerServiceProvider(final Object o) {
        if (o == null) {
            throw new IllegalArgumentException("provider == null!");
        }
        final Iterator subRegistries = this.getSubRegistries(o);
        while (subRegistries.hasNext()) {
            ((SubRegistry)subRegistries.next()).registerServiceProvider(o);
        }
    }
    
    public void registerServiceProviders(final Iterator<?> iterator) {
        if (iterator == null) {
            throw new IllegalArgumentException("provider == null!");
        }
        while (iterator.hasNext()) {
            this.registerServiceProvider(iterator.next());
        }
    }
    
    public <T> boolean deregisterServiceProvider(final T t, final Class<T> clazz) {
        if (t == null) {
            throw new IllegalArgumentException("provider == null!");
        }
        final SubRegistry subRegistry = this.categoryMap.get(clazz);
        if (subRegistry == null) {
            throw new IllegalArgumentException("category unknown!");
        }
        if (!clazz.isAssignableFrom(t.getClass())) {
            throw new ClassCastException();
        }
        return subRegistry.deregisterServiceProvider(t);
    }
    
    public void deregisterServiceProvider(final Object o) {
        if (o == null) {
            throw new IllegalArgumentException("provider == null!");
        }
        final Iterator subRegistries = this.getSubRegistries(o);
        while (subRegistries.hasNext()) {
            ((SubRegistry)subRegistries.next()).deregisterServiceProvider(o);
        }
    }
    
    public boolean contains(final Object o) {
        if (o == null) {
            throw new IllegalArgumentException("provider == null!");
        }
        final Iterator subRegistries = this.getSubRegistries(o);
        while (subRegistries.hasNext()) {
            if (((SubRegistry)subRegistries.next()).contains(o)) {
                return true;
            }
        }
        return false;
    }
    
    public <T> Iterator<T> getServiceProviders(final Class<T> clazz, final boolean b) {
        final SubRegistry subRegistry = this.categoryMap.get(clazz);
        if (subRegistry == null) {
            throw new IllegalArgumentException("category unknown!");
        }
        return subRegistry.getServiceProviders(b);
    }
    
    public <T> Iterator<T> getServiceProviders(final Class<T> clazz, final Filter filter, final boolean b) {
        if (this.categoryMap.get(clazz) == null) {
            throw new IllegalArgumentException("category unknown!");
        }
        return new FilterIterator<T>(this.getServiceProviders(clazz, b), filter);
    }
    
    public <T> T getServiceProviderByClass(final Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("providerClass == null!");
        }
        for (final Class clazz2 : this.categoryMap.keySet()) {
            if (clazz2.isAssignableFrom(clazz)) {
                final T serviceProviderByClass = this.categoryMap.get(clazz2).getServiceProviderByClass(clazz);
                if (serviceProviderByClass != null) {
                    return serviceProviderByClass;
                }
                continue;
            }
        }
        return null;
    }
    
    public <T> boolean setOrdering(final Class<T> clazz, final T t, final T t2) {
        if (t == null || t2 == null) {
            throw new IllegalArgumentException("provider is null!");
        }
        if (t == t2) {
            throw new IllegalArgumentException("providers are the same!");
        }
        final SubRegistry subRegistry = this.categoryMap.get(clazz);
        if (subRegistry == null) {
            throw new IllegalArgumentException("category unknown!");
        }
        return subRegistry.contains(t) && subRegistry.contains(t2) && subRegistry.setOrdering(t, t2);
    }
    
    public <T> boolean unsetOrdering(final Class<T> clazz, final T t, final T t2) {
        if (t == null || t2 == null) {
            throw new IllegalArgumentException("provider is null!");
        }
        if (t == t2) {
            throw new IllegalArgumentException("providers are the same!");
        }
        final SubRegistry subRegistry = this.categoryMap.get(clazz);
        if (subRegistry == null) {
            throw new IllegalArgumentException("category unknown!");
        }
        return subRegistry.contains(t) && subRegistry.contains(t2) && subRegistry.unsetOrdering(t, t2);
    }
    
    public void deregisterAll(final Class<?> clazz) {
        final SubRegistry subRegistry = this.categoryMap.get(clazz);
        if (subRegistry == null) {
            throw new IllegalArgumentException("category unknown!");
        }
        subRegistry.clear();
    }
    
    public void deregisterAll() {
        final Iterator iterator = this.categoryMap.values().iterator();
        while (iterator.hasNext()) {
            ((SubRegistry)iterator.next()).clear();
        }
    }
    
    public void finalize() throws Throwable {
        this.deregisterAll();
        super.finalize();
    }
    
    public interface Filter
    {
        boolean filter(final Object p0);
    }
}
