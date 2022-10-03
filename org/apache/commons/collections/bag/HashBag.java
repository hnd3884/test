package org.apache.commons.collections.bag;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;
import org.apache.commons.collections.Bag;

public class HashBag extends AbstractMapBag implements Bag, Serializable
{
    private static final long serialVersionUID = -6561115435802554013L;
    
    public HashBag() {
        super(new HashMap());
    }
    
    public HashBag(final Collection coll) {
        this();
        this.addAll(coll);
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        super.doWriteObject(out);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        super.doReadObject(new HashMap(), in);
    }
}
