package org.glassfish.jersey.model.internal;

import java.util.Comparator;

public class RankedComparator<T> implements Comparator<RankedProvider<T>>
{
    private final Order order;
    
    public RankedComparator() {
        this(Order.ASCENDING);
    }
    
    public RankedComparator(final Order order) {
        this.order = order;
    }
    
    @Override
    public int compare(final RankedProvider<T> o1, final RankedProvider<T> o2) {
        return (this.getPriority(o1) > this.getPriority(o2)) ? this.order.ordering : (-this.order.ordering);
    }
    
    protected int getPriority(final RankedProvider<T> rankedProvider) {
        return rankedProvider.getRank();
    }
    
    public enum Order
    {
        ASCENDING(1), 
        DESCENDING(-1);
        
        private final int ordering;
        
        private Order(final int ordering) {
            this.ordering = ordering;
        }
    }
}
