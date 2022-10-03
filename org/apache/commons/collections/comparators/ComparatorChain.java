package org.apache.commons.collections.comparators;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.io.Serializable;
import java.util.Comparator;

public class ComparatorChain implements Comparator, Serializable
{
    private static final long serialVersionUID = -721644942746081630L;
    protected List comparatorChain;
    protected BitSet orderingBits;
    protected boolean isLocked;
    
    public ComparatorChain() {
        this(new ArrayList(), new BitSet());
    }
    
    public ComparatorChain(final Comparator comparator) {
        this(comparator, false);
    }
    
    public ComparatorChain(final Comparator comparator, final boolean reverse) {
        this.comparatorChain = null;
        this.orderingBits = null;
        this.isLocked = false;
        (this.comparatorChain = new ArrayList()).add(comparator);
        this.orderingBits = new BitSet(1);
        if (reverse) {
            this.orderingBits.set(0);
        }
    }
    
    public ComparatorChain(final List list) {
        this(list, new BitSet(list.size()));
    }
    
    public ComparatorChain(final List list, final BitSet bits) {
        this.comparatorChain = null;
        this.orderingBits = null;
        this.isLocked = false;
        this.comparatorChain = list;
        this.orderingBits = bits;
    }
    
    public void addComparator(final Comparator comparator) {
        this.addComparator(comparator, false);
    }
    
    public void addComparator(final Comparator comparator, final boolean reverse) {
        this.checkLocked();
        this.comparatorChain.add(comparator);
        if (reverse) {
            this.orderingBits.set(this.comparatorChain.size() - 1);
        }
    }
    
    public void setComparator(final int index, final Comparator comparator) throws IndexOutOfBoundsException {
        this.setComparator(index, comparator, false);
    }
    
    public void setComparator(final int index, final Comparator comparator, final boolean reverse) {
        this.checkLocked();
        this.comparatorChain.set(index, comparator);
        if (reverse) {
            this.orderingBits.set(index);
        }
        else {
            this.orderingBits.clear(index);
        }
    }
    
    public void setForwardSort(final int index) {
        this.checkLocked();
        this.orderingBits.clear(index);
    }
    
    public void setReverseSort(final int index) {
        this.checkLocked();
        this.orderingBits.set(index);
    }
    
    public int size() {
        return this.comparatorChain.size();
    }
    
    public boolean isLocked() {
        return this.isLocked;
    }
    
    private void checkLocked() {
        if (this.isLocked) {
            throw new UnsupportedOperationException("Comparator ordering cannot be changed after the first comparison is performed");
        }
    }
    
    private void checkChainIntegrity() {
        if (this.comparatorChain.size() == 0) {
            throw new UnsupportedOperationException("ComparatorChains must contain at least one Comparator");
        }
    }
    
    public int compare(final Object o1, final Object o2) throws UnsupportedOperationException {
        if (!this.isLocked) {
            this.checkChainIntegrity();
            this.isLocked = true;
        }
        final Iterator comparators = this.comparatorChain.iterator();
        int comparatorIndex = 0;
        while (comparators.hasNext()) {
            final Comparator comparator = comparators.next();
            int retval = comparator.compare(o1, o2);
            if (retval != 0) {
                if (this.orderingBits.get(comparatorIndex)) {
                    if (Integer.MIN_VALUE == retval) {
                        retval = Integer.MAX_VALUE;
                    }
                    else {
                        retval *= -1;
                    }
                }
                return retval;
            }
            ++comparatorIndex;
        }
        return 0;
    }
    
    public int hashCode() {
        int hash = 0;
        if (null != this.comparatorChain) {
            hash ^= this.comparatorChain.hashCode();
        }
        if (null != this.orderingBits) {
            hash ^= this.orderingBits.hashCode();
        }
        return hash;
    }
    
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (null == object) {
            return false;
        }
        if (object.getClass().equals(this.getClass())) {
            final ComparatorChain chain = (ComparatorChain)object;
            if (null == this.orderingBits) {
                if (null != chain.orderingBits) {
                    return false;
                }
            }
            else if (!this.orderingBits.equals(chain.orderingBits)) {
                return false;
            }
            if ((null != this.comparatorChain) ? this.comparatorChain.equals(chain.comparatorChain) : (null == chain.comparatorChain)) {
                return true;
            }
            return false;
        }
        return false;
    }
}
