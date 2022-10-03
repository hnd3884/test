package org.apache.commons.collections;

import java.util.SortedMap;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class TreeBag extends DefaultMapBag implements SortedBag
{
    public TreeBag() {
        super(new TreeMap());
    }
    
    public TreeBag(final Comparator comparator) {
        super(new TreeMap(comparator));
    }
    
    public TreeBag(final Collection coll) {
        this();
        this.addAll(coll);
    }
    
    public Object first() {
        return ((SortedMap)this.getMap()).firstKey();
    }
    
    public Object last() {
        return ((SortedMap)this.getMap()).lastKey();
    }
    
    public Comparator comparator() {
        return ((SortedMap)this.getMap()).comparator();
    }
}
