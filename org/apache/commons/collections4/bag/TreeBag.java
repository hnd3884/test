package org.apache.commons.collections4.bag;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.SortedMap;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.io.Serializable;
import org.apache.commons.collections4.SortedBag;

public class TreeBag<E> extends AbstractMapBag<E> implements SortedBag<E>, Serializable
{
    private static final long serialVersionUID = -7740146511091606676L;
    
    public TreeBag() {
        super(new TreeMap());
    }
    
    public TreeBag(final Comparator<? super E> comparator) {
        super(new TreeMap(comparator));
    }
    
    public TreeBag(final Collection<? extends E> coll) {
        this();
        this.addAll(coll);
    }
    
    @Override
    public boolean add(final E object) {
        if (this.comparator() != null || object instanceof Comparable) {
            return super.add(object);
        }
        if (object == null) {
            throw new NullPointerException();
        }
        throw new IllegalArgumentException("Objects of type " + object.getClass() + " cannot be added to " + "a naturally ordered TreeBag as it does not implement Comparable");
    }
    
    @Override
    public E first() {
        return this.getMap().firstKey();
    }
    
    @Override
    public E last() {
        return this.getMap().lastKey();
    }
    
    @Override
    public Comparator<? super E> comparator() {
        return this.getMap().comparator();
    }
    
    @Override
    protected SortedMap<E, MutableInteger> getMap() {
        return (SortedMap)super.getMap();
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.comparator());
        super.doWriteObject(out);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        final Comparator<? super E> comp = (Comparator<? super E>)in.readObject();
        super.doReadObject(new TreeMap<E, MutableInteger>(comp), in);
    }
}
