package org.msgpack.template;

import java.util.HashSet;
import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import java.util.Iterator;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;
import java.util.Set;

public class SetTemplate<E> extends AbstractTemplate<Set<E>>
{
    private Template<E> elementTemplate;
    
    public SetTemplate(final Template<E> elementTemplate) {
        this.elementTemplate = elementTemplate;
    }
    
    @Override
    public void write(final Packer pk, final Set<E> target, final boolean required) throws IOException {
        if (target instanceof Set) {
            pk.writeArrayBegin(target.size());
            for (final E e : target) {
                this.elementTemplate.write(pk, e);
            }
            pk.writeArrayEnd();
            return;
        }
        if (target != null) {
            throw new MessageTypeException("Target is not a List but " + target.getClass());
        }
        if (required) {
            throw new MessageTypeException("Attempted to write null");
        }
        pk.writeNil();
    }
    
    @Override
    public Set<E> read(final Unpacker u, Set<E> to, final boolean required) throws IOException {
        if (!required && u.trySkipNil()) {
            return null;
        }
        final int n = u.readArrayBegin();
        if (to == null) {
            to = new HashSet<E>(n);
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
