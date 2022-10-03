package org.apache.commons.collections.set;

import java.util.Comparator;
import java.util.Set;
import org.apache.commons.collections.Predicate;
import java.util.SortedSet;

public class PredicatedSortedSet extends PredicatedSet implements SortedSet
{
    private static final long serialVersionUID = -9110948148132275052L;
    
    public static SortedSet decorate(final SortedSet set, final Predicate predicate) {
        return new PredicatedSortedSet(set, predicate);
    }
    
    protected PredicatedSortedSet(final SortedSet set, final Predicate predicate) {
        super(set, predicate);
    }
    
    private SortedSet getSortedSet() {
        return (SortedSet)this.getCollection();
    }
    
    public SortedSet subSet(final Object fromElement, final Object toElement) {
        final SortedSet sub = this.getSortedSet().subSet(fromElement, toElement);
        return new PredicatedSortedSet(sub, this.predicate);
    }
    
    public SortedSet headSet(final Object toElement) {
        final SortedSet sub = this.getSortedSet().headSet(toElement);
        return new PredicatedSortedSet(sub, this.predicate);
    }
    
    public SortedSet tailSet(final Object fromElement) {
        final SortedSet sub = this.getSortedSet().tailSet(fromElement);
        return new PredicatedSortedSet(sub, this.predicate);
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
