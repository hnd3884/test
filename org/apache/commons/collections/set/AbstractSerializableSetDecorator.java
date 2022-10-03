package org.apache.commons.collections.set;

import java.util.Collection;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Set;
import java.io.Serializable;

public abstract class AbstractSerializableSetDecorator extends AbstractSetDecorator implements Serializable
{
    private static final long serialVersionUID = 1229469966212206107L;
    
    protected AbstractSerializableSetDecorator(final Set set) {
        super(set);
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.collection);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.collection = (Collection)in.readObject();
    }
}
