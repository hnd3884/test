package org.msgpack.template;

import java.util.LinkedList;
import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import java.util.Iterator;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;
import java.util.Collection;

public class CollectionTemplate<E> extends AbstractTemplate<Collection<E>>
{
    private Template<E> elementTemplate;
    
    public CollectionTemplate(final Template<E> elementTemplate) {
        this.elementTemplate = elementTemplate;
    }
    
    @Override
    public void write(final Packer pk, final Collection<E> target, final boolean required) throws IOException {
        if (target != null) {
            final Collection<E> col = target;
            pk.writeArrayBegin(col.size());
            for (final E e : col) {
                this.elementTemplate.write(pk, e);
            }
            pk.writeArrayEnd();
            return;
        }
        if (required) {
            throw new MessageTypeException("Attempted to write null");
        }
        pk.writeNil();
    }
    
    @Override
    public Collection<E> read(final Unpacker u, Collection<E> to, final boolean required) throws IOException {
        if (!required && u.trySkipNil()) {
            return null;
        }
        final int n = u.readArrayBegin();
        if (to == null) {
            to = new LinkedList<E>();
        }
        else {
            to.clear();
        }
        for (int i = 0; i < n; ++i) {
            final E e = this.elementTemplate.read(u, null);
            to.add(e);
        }
        u.readArrayEnd();
        return to;
    }
}
