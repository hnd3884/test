package org.apache.commons.collections4.comparators;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.io.Serializable;
import java.util.Comparator;

public class ComparatorChain<E> implements Comparator<E>, Serializable
{
    private static final long serialVersionUID = -721644942746081630L;
    private final List<Comparator<E>> comparatorChain;
    private BitSet orderingBits;
    private boolean isLocked;
    
    public ComparatorChain() {
        this((List)new ArrayList(), new BitSet());
    }
    
    public ComparatorChain(final Comparator<E> comparator) {
        this(comparator, false);
    }
    
    public ComparatorChain(final Comparator<E> comparator, final boolean reverse) {
        this.orderingBits = null;
        this.isLocked = false;
        (this.comparatorChain = new ArrayList<Comparator<E>>(1)).add(comparator);
        this.orderingBits = new BitSet(1);
        if (reverse) {
            this.orderingBits.set(0);
        }
    }
    
    public ComparatorChain(final List<Comparator<E>> list) {
        this(list, new BitSet(list.size()));
    }
    
    public ComparatorChain(final List<Comparator<E>> list, final BitSet bits) {
        this.orderingBits = null;
        this.isLocked = false;
        this.comparatorChain = list;
        this.orderingBits = bits;
    }
    
    public void addComparator(final Comparator<E> comparator) {
        this.addComparator(comparator, false);
    }
    
    public void addComparator(final Comparator<E> comparator, final boolean reverse) {
        this.checkLocked();
        this.comparatorChain.add(comparator);
        if (reverse) {
            this.orderingBits.set(this.comparatorChain.size() - 1);
        }
    }
    
    public void setComparator(final int index, final Comparator<E> comparator) throws IndexOutOfBoundsException {
        this.setComparator(index, comparator, false);
    }
    
    public void setComparator(final int index, final Comparator<E> comparator, final boolean reverse) {
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
    
    @Override
    public int compare(final E o1, final E o2) throws UnsupportedOperationException {
        if (!this.isLocked) {
            this.checkChainIntegrity();
            this.isLocked = true;
        }
        final Iterator<Comparator<E>> comparators = this.comparatorChain.iterator();
        int comparatorIndex = 0;
        while (comparators.hasNext()) {
            final Comparator<? super E> comparator = comparators.next();
            int retval = comparator.compare((Object)o1, (Object)o2);
            if (retval != 0) {
                if (this.orderingBits.get(comparatorIndex)) {
                    if (retval > 0) {
                        retval = -1;
                    }
                    else {
                        retval = 1;
                    }
                }
                return retval;
            }
            ++comparatorIndex;
        }
        return 0;
    }
    
    @Override
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
    
    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (null == object) {
            return false;
        }
        if (object.getClass().equals(this.getClass())) {
            final ComparatorChain<?> chain = (ComparatorChain<?>)object;
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
