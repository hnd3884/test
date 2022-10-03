package org.apache.commons.collections.comparators;

import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Comparator;

public class FixedOrderComparator implements Comparator
{
    public static final int UNKNOWN_BEFORE = 0;
    public static final int UNKNOWN_AFTER = 1;
    public static final int UNKNOWN_THROW_EXCEPTION = 2;
    private final Map map;
    private int counter;
    private boolean isLocked;
    private int unknownObjectBehavior;
    
    public FixedOrderComparator() {
        this.map = new HashMap();
        this.counter = 0;
        this.isLocked = false;
        this.unknownObjectBehavior = 2;
    }
    
    public FixedOrderComparator(final Object[] items) {
        this.map = new HashMap();
        this.counter = 0;
        this.isLocked = false;
        this.unknownObjectBehavior = 2;
        if (items == null) {
            throw new IllegalArgumentException("The list of items must not be null");
        }
        for (int i = 0; i < items.length; ++i) {
            this.add(items[i]);
        }
    }
    
    public FixedOrderComparator(final List items) {
        this.map = new HashMap();
        this.counter = 0;
        this.isLocked = false;
        this.unknownObjectBehavior = 2;
        if (items == null) {
            throw new IllegalArgumentException("The list of items must not be null");
        }
        final Iterator it = items.iterator();
        while (it.hasNext()) {
            this.add(it.next());
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
    
    public int getUnknownObjectBehavior() {
        return this.unknownObjectBehavior;
    }
    
    public void setUnknownObjectBehavior(final int unknownObjectBehavior) {
        this.checkLocked();
        if (unknownObjectBehavior != 1 && unknownObjectBehavior != 0 && unknownObjectBehavior != 2) {
            throw new IllegalArgumentException("Unrecognised value for unknown behaviour flag");
        }
        this.unknownObjectBehavior = unknownObjectBehavior;
    }
    
    public boolean add(final Object obj) {
        this.checkLocked();
        final Object position = this.map.put(obj, new Integer(this.counter++));
        return position == null;
    }
    
    public boolean addAsEqual(final Object existingObj, final Object newObj) {
        this.checkLocked();
        final Integer position = this.map.get(existingObj);
        if (position == null) {
            throw new IllegalArgumentException(existingObj + " not known to " + this);
        }
        final Object result = this.map.put(newObj, position);
        return result == null;
    }
    
    public int compare(final Object obj1, final Object obj2) {
        this.isLocked = true;
        final Integer position1 = this.map.get(obj1);
        final Integer position2 = this.map.get(obj2);
        if (position1 != null && position2 != null) {
            return position1.compareTo(position2);
        }
        switch (this.unknownObjectBehavior) {
            case 0: {
                if (position1 == null) {
                    return (position2 == null) ? 0 : -1;
                }
                return 1;
            }
            case 1: {
                if (position1 == null) {
                    return (position2 != null) ? 1 : 0;
                }
                return -1;
            }
            case 2: {
                final Object unknownObj = (position1 == null) ? obj1 : obj2;
                throw new IllegalArgumentException("Attempting to compare unknown object " + unknownObj);
            }
            default: {
                throw new UnsupportedOperationException("Unknown unknownObjectBehavior: " + this.unknownObjectBehavior);
            }
        }
    }
}
