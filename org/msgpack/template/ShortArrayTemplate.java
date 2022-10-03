package org.msgpack.template;

import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;

public class ShortArrayTemplate extends AbstractTemplate<short[]>
{
    static final ShortArrayTemplate instance;
    
    private ShortArrayTemplate() {
    }
    
    @Override
    public void write(final Packer pk, final short[] target, final boolean required) throws IOException {
        if (target != null) {
            pk.writeArrayBegin(target.length);
            for (final short a : target) {
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
    public short[] read(final Unpacker u, short[] to, final boolean required) throws IOException {
        if (!required && u.trySkipNil()) {
            return null;
        }
        final int n = u.readArrayBegin();
        if (to == null || to.length != n) {
            to = new short[n];
        }
        for (int i = 0; i < n; ++i) {
            to[i] = u.readShort();
        }
        u.readArrayEnd();
        return to;
    }
    
    public static ShortArrayTemplate getInstance() {
        return ShortArrayTemplate.instance;
    }
    
    static {
        instance = new ShortArrayTemplate();
    }
}
