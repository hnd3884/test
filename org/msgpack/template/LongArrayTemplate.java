package org.msgpack.template;

import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;

public class LongArrayTemplate extends AbstractTemplate<long[]>
{
    static final LongArrayTemplate instance;
    
    private LongArrayTemplate() {
    }
    
    @Override
    public void write(final Packer pk, final long[] target, final boolean required) throws IOException {
        if (target != null) {
            pk.writeArrayBegin(target.length);
            for (final long a : target) {
                pk.write(a);
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
    public long[] read(final Unpacker u, long[] to, final boolean required) throws IOException {
        if (!required && u.trySkipNil()) {
            return null;
        }
        final int n = u.readArrayBegin();
        if (to == null || to.length != n) {
            to = new long[n];
        }
        for (int i = 0; i < n; ++i) {
            to[i] = u.readLong();
        }
        u.readArrayEnd();
        return to;
    }
    
    public static LongArrayTemplate getInstance() {
        return LongArrayTemplate.instance;
    }
    
    static {
        instance = new LongArrayTemplate();
    }
}
