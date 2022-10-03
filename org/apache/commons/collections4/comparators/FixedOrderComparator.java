package org.apache.commons.collections4.comparators;

import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;
import java.util.Comparator;

public class FixedOrderComparator<T> implements Comparator<T>, Serializable
{
    private static final long serialVersionUID = 82794675842863201L;
    private final Map<T, Integer> map;
    private int counter;
    private boolean isLocked;
    private UnknownObjectBehavior unknownObjectBehavior;
    
    public FixedOrderComparator() {
        this.map = new HashMap<T, Integer>();
        this.counter = 0;
        this.isLocked = false;
        this.unknownObjectBehavior = UnknownObjectBehavior.EXCEPTION;
    }
    
    public FixedOrderComparator(final T... items) {
        this.map = new HashMap<T, Integer>();
        this.counter = 0;
        this.isLocked = false;
        this.unknownObjectBehavior = UnknownObjectBehavior.EXCEPTION;
        if (items == null) {
            throw new NullPointerException("The list of items must not be null");
        }
        for (final T item : items) {
            this.add(item);
        }
    }
    
    public FixedOrderComparator(final List<T> items) {
        this.map = new HashMap<T, Integer>();
        this.counter = 0;
        this.isLocked = false;
        this.unknownObjectBehavior = UnknownObjectBehavior.EXCEPTION;
        if (items == null) {
            throw new NullPointerException("The list of items must not be null");
        }
        for (final T t : items) {
            this.add(t);
        }
    }
    
    public boolean isLocked() {
        return this.isLocked;
    }
    
    protected void checkLocked() {
        if (this.isLocked()) {
            throw new UnsupportedOperationException("Cannot modify a FixedOrderComparator after a comparison");
        }
    }
    
    public UnknownObjectBehavior getUnknownObjectBehavior() {
        return this.unknownObjectBehavior;
    }
    
    public void setUnknownObjectBehavior(final UnknownObjectBehavior unknownObjectBehavior) {
        this.checkLocked();
        if (unknownObjectBehavior == null) {
            throw new NullPointerException("Unknown object behavior must not be null");
        }
        this.unknownObjectBehavior = unknownObjectBehavior;
    }
    
    public boolean add(final T obj) {
        this.checkLocked();
        final Integer position = this.map.put(obj, this.counter++);
        return position == null;
    }
    
    public boolean addAsEqual(final T existingObj, final T newObj) {
        this.checkLocked();
        final Integer position = this.map.get(existingObj);
        if (position == null) {
            throw new IllegalArgumentException(existingObj + " not known to " + this);
        }
        final Integer result = this.map.put(newObj, position);
        return result == null;
    }
    
    @Override
    public int compare(final T obj1, final T obj2) {
        this.isLocked = true;
        final Integer position1 = this.map.get(obj1);
        final Integer position2 = this.map.get(obj2);
        if (position1 != null && position2 != null) {
            return position1.compareTo(position2);
        }
        switch (this.unknownObjectBehavior) {
            case BEFORE: {
                return (position1 == null) ? ((position2 == null) ? 0 : -1) : 1;
            }
            case AFTER: {
                return (position1 == null) ? ((position2 == null) ? 0 : 1) : -1;
            }
            case EXCEPTION: {
                final Object unknownObj = (position1 == null) ? obj1 : obj2;
                throw new IllegalArgumentException("Attempting to compare unknown object " + unknownObj);
            }
            default: {
                throw new UnsupportedOperationException("Unknown unknownObjectBehavior: " + this.unknownObjectBehavior);
            }
        }
    }
    
    @Override
    public int hashCode() {
        int total = 17;
        total = total * 37 + ((this.map == null) ? 0 : this.map.hashCode());
        total = total * 37 + ((this.unknownObjectBehavior == null) ? 0 : this.unknownObjectBehavior.hashCode());
        total = total * 37 + this.counter;
        total = total * 37 + (this.isLocked ? 0 : 1);
        return total;
    }
    
    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (null == object) {
            return false;
        }
        if (object.getClass().equals(this.getClass())) {
            final FixedOrderComparator<?> comp = (FixedOrderComparator<?>)object;
            if (null == this.map) {
                if (null != comp.map) {
                    return false;
                }
            }
            else if (!this.map.equals(comp.map)) {
                return false;
            }
            if ((null != this.unknownObjectBehavior) ? (this.unknownObjectBehavior == comp.unknownObjectBehavior && this.counter == comp.counter && this.isLocked == comp.isLocked && this.unknownObjectBehavior == comp.unknownObjectBehavior) : (null == comp.unknownObjectBehavior)) {
                return true;
            }
            return false;
        }
        return false;
    }
    
    public enum UnknownObjectBehavior
    {
        BEFORE, 
        AFTER, 
        EXCEPTION;
    }
}
