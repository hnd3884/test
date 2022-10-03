package org.msgpack.template;

import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;
import java.util.HashMap;

public class OrdinalEnumTemplate<T> extends AbstractTemplate<T>
{
    protected T[] entries;
    protected HashMap<T, Integer> reverse;
    
    public OrdinalEnumTemplate(final Class<T> targetClass) {
        this.entries = targetClass.getEnumConstants();
        this.reverse = new HashMap<T, Integer>();
        for (int i = 0; i < this.entries.length; ++i) {
            this.reverse.put(this.entries[i], i);
        }
    }
    
    @Override
    public void write(final Packer pk, final T target, final boolean required) throws IOException {
        if (target == null) {
            if (required) {
                throw new MessageTypeException("Attempted to write null");
            }
            pk.writeNil();
        }
        else {
            final Integer ordinal = this.reverse.get(target);
            if (ordinal == null) {
                throw new MessageTypeException(new IllegalArgumentException("ordinal: " + ordinal));
            }
            pk.write((int)ordinal);
        }
    }
    
    @Override
    public T read(final Unpacker pac, final T to, final boolean required) throws IOException, MessageTypeException {
        if (!required && pac.trySkipNil()) {
            return null;
        }
        final int ordinal = pac.readInt();
        if (this.entries.length <= ordinal) {
            throw new MessageTypeException(new IllegalArgumentException("ordinal: " + ordinal));
        }
        return this.entries[ordinal];
    }
}
