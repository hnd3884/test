package jdk.jfr.consumer;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.ArrayList;

final class ConstantMap
{
    private final ObjectFactory<?> factory;
    private final LongMap<Object> objects;
    private LongMap<Boolean> isResolving;
    private boolean allResolved;
    private String name;
    
    ConstantMap(final ObjectFactory<?> factory, final String name) {
        this.name = name;
        this.objects = new LongMap<Object>();
        this.factory = factory;
    }
    
    Object get(final long n) {
        if (this.allResolved) {
            return this.objects.get(n);
        }
        if (this.isResolving == null) {
            return new Reference(this, n);
        }
        final Boolean b = this.isResolving.get(n);
        if (Boolean.FALSE.equals(b)) {
            return this.objects.get(n);
        }
        if (Boolean.TRUE.equals(b)) {
            return null;
        }
        this.isResolving.put(n, Boolean.TRUE);
        final Object resolve = resolve(this.objects.get(n));
        this.isResolving.put(n, Boolean.FALSE);
        if (this.factory != null) {
            final Object object = this.factory.createObject(n, resolve);
            this.objects.put(n, object);
            return object;
        }
        this.objects.put(n, resolve);
        return resolve;
    }
    
    private static Object resolve(final Object o) {
        if (o instanceof Reference) {
            return resolve(((Reference)o).resolve());
        }
        if (o != null && o.getClass().isArray()) {
            final Object[] array = (Object[])o;
            for (int i = 0; i < array.length; ++i) {
                array[i] = resolve(array[i]);
            }
            return array;
        }
        return o;
    }
    
    public void resolve() {
        final ArrayList list = new ArrayList();
        this.objects.keys().forEachRemaining(list::add);
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            this.get((long)iterator.next());
        }
    }
    
    public void put(final long n, final Object o) {
        this.objects.put(n, o);
    }
    
    public void setIsResolving() {
        this.isResolving = new LongMap<Boolean>();
    }
    
    public void setResolved() {
        this.allResolved = true;
        this.isResolving = null;
    }
    
    public String getName() {
        return this.name;
    }
    
    private static final class Reference
    {
        private final long key;
        private final ConstantMap pool;
        
        Reference(final ConstantMap pool, final long key) {
            this.pool = pool;
            this.key = key;
        }
        
        Object resolve() {
            return this.pool.get(this.key);
        }
    }
}
