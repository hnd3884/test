package org.apache.commons.collections4.multiset;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;

public class HashMultiSet<E> extends AbstractMapMultiSet<E> implements Serializable
{
    private static final long serialVersionUID = 20150610L;
    
    public HashMultiSet() {
        super(new HashMap());
    }
    
    public HashMultiSet(final Collection<? extends E> coll) {
        this();
        this.addAll(coll);
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        super.doWriteObject(out);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.setMap(new HashMap<E, MutableInteger>());
        super.doReadObject(in);
    }
}
