package javax.imageio.spi;

import java.util.Iterator;
import java.security.AccessController;
import java.util.HashMap;
import java.security.AccessControlContext;
import java.util.Map;

class SubRegistry
{
    ServiceRegistry registry;
    Class category;
    final PartiallyOrderedSet poset;
    final Map<Class<?>, Object> map;
    final Map<Class<?>, AccessControlContext> accMap;
    
    public SubRegistry(final ServiceRegistry registry, final Class category) {
        this.poset = new PartiallyOrderedSet();
        this.map = new HashMap<Class<?>, Object>();
        this.accMap = new HashMap<Class<?>, AccessControlContext>();
        this.registry = registry;
        this.category = category;
    }
    
    public boolean registerServiceProvider(final Object o) {
        final Object value = this.map.get(o.getClass());
        final boolean b = value != null;
        if (b) {
            this.deregisterServiceProvider(value);
        }
        this.map.put(o.getClass(), o);
        this.accMap.put(o.getClass(), AccessController.getContext());
        this.poset.add(o);
        if (o instanceof RegisterableService) {
            ((RegisterableService)o).onRegistration(this.registry, this.category);
        }
        return !b;
    }
    
    public boolean deregisterServiceProvider(final Object o) {
        if (o == this.map.get(o.getClass())) {
            this.map.remove(o.getClass());
            this.accMap.remove(o.getClass());
            this.poset.remove(o);
            if (o instanceof RegisterableService) {
                ((RegisterableService)o).onDeregistration(this.registry, this.category);
            }
            return true;
        }
        return false;
    }
    
    public boolean contains(final Object o) {
        return this.map.get(o.getClass()) == o;
    }
    
    public boolean setOrdering(final Object o, final Object o2) {
        return this.poset.setOrdering(o, o2);
    }
    
    public boolean unsetOrdering(final Object o, final Object o2) {
        return this.poset.unsetOrdering(o, o2);
    }
    
    public Iterator getServiceProviders(final boolean b) {
        if (b) {
            return this.poset.iterator();
        }
        return this.map.values().iterator();
    }
    
    public <T> T getServiceProviderByClass(final Class<T> clazz) {
        return (T)this.map.get(clazz);
    }
    
    public void clear() {
        final Iterator<Object> iterator = (Iterator<Object>)this.map.values().iterator();
        while (iterator.hasNext()) {
            final RegisterableService next = iterator.next();
            iterator.remove();
            if (next instanceof RegisterableService) {
                final RegisterableService registerableService = next;
                final AccessControlContext accessControlContext = this.accMap.get(next.getClass());
                if (accessControlContext == null && System.getSecurityManager() != null) {
                    continue;
                }
                AccessController.doPrivileged(() -> {
                    registerableService2.onDeregistration(this.registry, this.category);
                    return null;
                }, accessControlContext);
            }
        }
        this.poset.clear();
        this.accMap.clear();
    }
    
    public void finalize() {
        this.clear();
    }
}
