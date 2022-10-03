package org.apache.commons.collections4.set;

import java.util.Collection;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Set;

public abstract class AbstractSerializableSetDecorator<E> extends AbstractSetDecorator<E>
{
    private static final long serialVersionUID = 1229469966212206107L;
    
    protected AbstractSerializableSetDecorator(final Set<E> set) {
        super(set);
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.decorated());
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.setCollection((Collection<E>)in.readObject());
    }
}
