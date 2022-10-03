package org.apache.commons.collections.set;

import java.util.Iterator;
import org.apache.commons.collections.CollectionUtils;
import java.util.Collection;
import java.util.Set;
import org.apache.commons.collections.collection.CompositeCollection;

public class CompositeSet extends CompositeCollection implements Set
{
    public CompositeSet() {
    }
    
    public CompositeSet(final Set set) {
        super(set);
    }
    
    public CompositeSet(final Set[] sets) {
        super(sets);
    }
    
    public synchronized void addComposited(final Collection c) {
        if (!(c instanceof Set)) {
            throw new IllegalArgumentException("Collections added must implement java.util.Set");
        }
        final Iterator i = this.getCollections().iterator();
        while (i.hasNext()) {
            final Set set = i.next();
            final Collection intersects = CollectionUtils.intersection(set, c);
            if (intersects.size() > 0) {
                if (this.mutator == null) {
                    throw new UnsupportedOperationException("Collision adding composited collection with no SetMutator set");
                }
                if (!(this.mutator instanceof SetMutator)) {
                    throw new UnsupportedOperationException("Collision adding composited collection to a CompositeSet with a CollectionMutator instead of a SetMutator");
                }
                ((SetMutator)this.mutator).resolveCollision(this, set, (Set)c, intersects);
                if (CollectionUtils.intersection(set, c).size() > 0) {
                    throw new IllegalArgumentException("Attempt to add illegal entry unresolved by SetMutator.resolveCollision()");
                }
                continue;
            }
        }
        super.addComposited(new Collection[] { c });
    }
    
    public synchronized void addComposited(final Collection c, final Collection d) {
        if (!(c instanceof Set)) {
            throw new IllegalArgumentException("Argument must implement java.util.Set");
        }
        if (!(d instanceof Set)) {
            throw new IllegalArgumentException("Argument must implement java.util.Set");
        }
        this.addComposited(new Set[] { (Set)c, (Set)d });
    }
    
    public synchronized void addComposited(final Collection[] comps) {
        for (int i = comps.length - 1; i >= 0; --i) {
            this.addComposited(comps[i]);
        }
    }
    
    public void setMutator(final CollectionMutator mutator) {
        super.setMutator(mutator);
    }
    
    public boolean remove(final Object obj) {
        final Iterator i = this.getCollections().iterator();
        while (i.hasNext()) {
            final Set set = i.next();
            if (set.contains(obj)) {
                return set.remove(obj);
            }
        }
        return false;
    }
    
    public boolean equals(final Object obj) {
        if (obj instanceof Set) {
            final Set set = (Set)obj;
            if (set.containsAll(this) && set.size() == this.size()) {
                return true;
            }
        }
        return false;
    }
    
    public int hashCode() {
        int code = 0;
        final Iterator i = this.iterator();
        while (i.hasNext()) {
            final Object next = i.next();
            code += ((next != null) ? next.hashCode() : 0);
        }
        return code;
    }
    
    public interface SetMutator extends CollectionMutator
    {
        void resolveCollision(final CompositeSet p0, final Set p1, final Set p2, final Collection p3);
    }
}
