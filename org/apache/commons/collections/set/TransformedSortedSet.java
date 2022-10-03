package org.apache.commons.collections.set;

import java.util.Comparator;
import java.util.Set;
import org.apache.commons.collections.Transformer;
import java.util.SortedSet;

public class TransformedSortedSet extends TransformedSet implements SortedSet
{
    private static final long serialVersionUID = -1675486811351124386L;
    
    public static SortedSet decorate(final SortedSet set, final Transformer transformer) {
        return new TransformedSortedSet(set, transformer);
    }
    
    protected TransformedSortedSet(final SortedSet set, final Transformer transformer) {
        super(set, transformer);
    }
    
    protected SortedSet getSortedSet() {
        return (SortedSet)this.collection;
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
    
    public SortedSet subSet(final Object fromElement, final Object toElement) {
        final SortedSet set = this.getSortedSet().subSet(fromElement, toElement);
        return new TransformedSortedSet(set, this.transformer);
    }
    
    public SortedSet headSet(final Object toElement) {
        final SortedSet set = this.getSortedSet().headSet(toElement);
        return new TransformedSortedSet(set, this.transformer);
    }
    
    public SortedSet tailSet(final Object fromElement) {
        final SortedSet set = this.getSortedSet().tailSet(fromElement);
        return new TransformedSortedSet(set, this.transformer);
    }
}
