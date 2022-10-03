package org.apache.commons.collections.bag;

import java.util.Comparator;
import org.apache.commons.collections.Bag;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.SortedBag;

public class TransformedSortedBag extends TransformedBag implements SortedBag
{
    private static final long serialVersionUID = -251737742649401930L;
    
    public static SortedBag decorate(final SortedBag bag, final Transformer transformer) {
        return new TransformedSortedBag(bag, transformer);
    }
    
    protected TransformedSortedBag(final SortedBag bag, final Transformer transformer) {
        super(bag, transformer);
    }
    
    protected SortedBag getSortedBag() {
        return (SortedBag)this.collection;
    }
    
    public Object first() {
        return this.getSortedBag().first();
    }
    
    public Object last() {
        return this.getSortedBag().last();
    }
    
    public Comparator comparator() {
        return this.getSortedBag().comparator();
    }
}
