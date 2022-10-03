package javax.imageio.spi;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import java.util.AbstractSet;

class PartiallyOrderedSet extends AbstractSet
{
    private Map poNodes;
    private Set nodes;
    
    public PartiallyOrderedSet() {
        this.poNodes = new HashMap();
        this.nodes = this.poNodes.keySet();
    }
    
    @Override
    public int size() {
        return this.nodes.size();
    }
    
    @Override
    public boolean contains(final Object o) {
        return this.nodes.contains(o);
    }
    
    @Override
    public Iterator iterator() {
        return new PartialOrderIterator(this.poNodes.values().iterator());
    }
    
    @Override
    public boolean add(final Object o) {
        if (this.nodes.contains(o)) {
            return false;
        }
        this.poNodes.put(o, new DigraphNode(o));
        return true;
    }
    
    @Override
    public boolean remove(final Object o) {
        final DigraphNode digraphNode = this.poNodes.get(o);
        if (digraphNode == null) {
            return false;
        }
        this.poNodes.remove(o);
        digraphNode.dispose();
        return true;
    }
    
    @Override
    public void clear() {
        this.poNodes.clear();
    }
    
    public boolean setOrdering(final Object o, final Object o2) {
        final DigraphNode digraphNode = this.poNodes.get(o);
        final DigraphNode digraphNode2 = this.poNodes.get(o2);
        digraphNode2.removeEdge(digraphNode);
        return digraphNode.addEdge(digraphNode2);
    }
    
    public boolean unsetOrdering(final Object o, final Object o2) {
        final DigraphNode digraphNode = this.poNodes.get(o);
        final DigraphNode digraphNode2 = this.poNodes.get(o2);
        return digraphNode.removeEdge(digraphNode2) || digraphNode2.removeEdge(digraphNode);
    }
    
    public boolean hasOrdering(final Object o, final Object o2) {
        return this.poNodes.get(o).hasEdge(this.poNodes.get(o2));
    }
}
