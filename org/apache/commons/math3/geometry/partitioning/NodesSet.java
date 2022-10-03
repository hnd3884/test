package org.apache.commons.math3.geometry.partitioning;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.geometry.Space;

public class NodesSet<S extends Space> implements Iterable<BSPTree<S>>
{
    private List<BSPTree<S>> list;
    
    public NodesSet() {
        this.list = new ArrayList<BSPTree<S>>();
    }
    
    public void add(final BSPTree<S> node) {
        for (final BSPTree<S> existing : this.list) {
            if (node == existing) {
                return;
            }
        }
        this.list.add(node);
    }
    
    public void addAll(final Iterable<BSPTree<S>> iterator) {
        for (final BSPTree<S> node : iterator) {
            this.add(node);
        }
    }
    
    public Iterator<BSPTree<S>> iterator() {
        return this.list.iterator();
    }
}
