package org.owasp.esapi.reference;

import org.owasp.esapi.errors.AccessControlException;
import java.util.Collection;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import org.owasp.esapi.AccessReferenceMap;

public abstract class AbstractAccessReferenceMap<K> implements AccessReferenceMap<K>
{
    private static final long serialVersionUID = 238742764284682230L;
    protected Map<K, Object> itod;
    protected Map<Object, K> dtoi;
    
    public AbstractAccessReferenceMap() {
        this.itod = new ConcurrentHashMap<K, Object>();
        this.dtoi = new ConcurrentHashMap<Object, K>();
    }
    
    public AbstractAccessReferenceMap(final int initialSize) {
        this.itod = new ConcurrentHashMap<K, Object>(initialSize);
        this.dtoi = new ConcurrentHashMap<Object, K>(initialSize);
    }
    
    @Deprecated
    public AbstractAccessReferenceMap(final Set<Object> directReferences) {
        this.itod = new ConcurrentHashMap<K, Object>(directReferences.size());
        this.dtoi = new ConcurrentHashMap<Object, K>(directReferences.size());
        this.update(directReferences);
    }
    
    @Deprecated
    public AbstractAccessReferenceMap(final Set<Object> directReferences, final int initialSize) {
        this.itod = new ConcurrentHashMap<K, Object>(initialSize);
        this.dtoi = new ConcurrentHashMap<Object, K>(initialSize);
        this.update(directReferences);
    }
    
    protected abstract K getUniqueReference();
    
    @Override
    public synchronized Iterator iterator() {
        final TreeSet sorted = new TreeSet((Collection<? extends E>)this.dtoi.keySet());
        return sorted.iterator();
    }
    
    @Override
    public <T> K addDirectReference(final T direct) {
        if (this.dtoi.keySet().contains(direct)) {
            return this.dtoi.get(direct);
        }
        final K indirect = this.getUniqueReference();
        this.itod.put(indirect, direct);
        this.dtoi.put(direct, indirect);
        return indirect;
    }
    
    @Override
    public <T> K removeDirectReference(final T direct) throws AccessControlException {
        final K indirect = this.dtoi.get(direct);
        if (indirect != null) {
            this.itod.remove(indirect);
            this.dtoi.remove(direct);
        }
        return indirect;
    }
    
    @Override
    public final synchronized void update(final Set directReferences) {
        final Map<Object, K> new_dtoi = new ConcurrentHashMap<Object, K>(directReferences.size());
        final Map<K, Object> new_itod = new ConcurrentHashMap<K, Object>(directReferences.size());
        for (final Object o : directReferences) {
            K indirect = this.dtoi.get(o);
            if (indirect == null) {
                indirect = this.getUniqueReference();
            }
            new_dtoi.put(o, indirect);
            new_itod.put(indirect, o);
        }
        this.dtoi = new_dtoi;
        this.itod = new_itod;
    }
    
    @Override
    public <T> K getIndirectReference(final T directReference) {
        return this.dtoi.get(directReference);
    }
    
    @Override
    public <T> T getDirectReference(final K indirectReference) throws AccessControlException {
        if (this.itod.containsKey(indirectReference)) {
            try {
                return (T)this.itod.get(indirectReference);
            }
            catch (final ClassCastException e) {
                throw new AccessControlException("Access denied.", "Request for incorrect type reference: " + indirectReference);
            }
        }
        throw new AccessControlException("Access denied", "Request for invalid indirect reference: " + indirectReference);
    }
}
