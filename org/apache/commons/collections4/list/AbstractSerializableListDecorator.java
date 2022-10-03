package org.apache.commons.collections4.list;

import java.util.Collection;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public abstract class AbstractSerializableListDecorator<E> extends AbstractListDecorator<E>
{
    private static final long serialVersionUID = 2684959196747496299L;
    
    protected AbstractSerializableListDecorator(final List<E> list) {
        super(list);
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
