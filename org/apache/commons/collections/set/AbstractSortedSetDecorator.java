package org.apache.commons.collections.set;

import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;

public abstract class AbstractSortedSetDecorator extends AbstractSetDecorator implements SortedSet
{
    protected AbstractSortedSetDecorator() {
    }
    
    protected AbstractSortedSetDecorator(final Set set) {
        super(set);
    }
    
    protected SortedSet getSortedSet() {
        return (SortedSet)this.getCollection();
    }
    
    public SortedSet subSet(final Object fromElement, final Object toElement) {
        return this.getSortedSet().subSet(fromElement, toElement);
    }
    
    public SortedSet headSet(final Object toElement) {
        return this.getSortedSet().headSet(toElement);
    }
    
    public SortedSet tailSet(final Object fromElement) {
        return this.getSortedSet().tailSet(fromElement);
    }
    
    public Object first() {
        return this.getSortedSet().first();
    }
    
    public Object last() {
        return this.getSortedSet().last();
    }
    
    public Comparator comparator() {
        return this.getSortedSet().comparator();
    }
}
