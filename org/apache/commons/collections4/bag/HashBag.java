package org.apache.commons.collections4.bag;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;

public class HashBag<E> extends AbstractMapBag<E> implements Serializable
{
    private static final long serialVersionUID = -6561115435802554013L;
    
    public HashBag() {
        super(new HashMap());
    }
    
    public HashBag(final Collection<? extends E> coll) {
        this();
        this.addAll(coll);
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        super.doWriteObject(out);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        super.doReadObject(new HashMap<E, MutableInteger>(), in);
    }
}
