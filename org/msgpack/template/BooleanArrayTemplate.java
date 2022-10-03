package org.msgpack.template;

import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;

public class BooleanArrayTemplate extends AbstractTemplate<boolean[]>
{
    static final BooleanArrayTemplate instance;
    
    private BooleanArrayTemplate() {
    }
    
    @Override
    public void write(final Packer pk, final boolean[] target, final boolean required) throws IOException {
        if (target != null) {
            pk.writeArrayBegin(target.length);
            for (final boolean a : target) {
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
    public boolean[] read(final Unpacker u, boolean[] to, final boolean required) throws IOException {
        if (!required && u.trySkipNil()) {
            return null;
        }
        final int n = u.readArrayBegin();
        if (to == null || to.length != n) {
            to = new boolean[n];
        }
        for (int i = 0; i < n; ++i) {
            to[i] = u.readBoolean();
        }
        u.readArrayEnd();
        return to;
    }
    
    public static BooleanArrayTemplate getInstance() {
        return BooleanArrayTemplate.instance;
    }
    
    static {
        instance = new BooleanArrayTemplate();
    }
}
