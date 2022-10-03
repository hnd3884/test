package org.msgpack.template;

import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;

public class FloatArrayTemplate extends AbstractTemplate<float[]>
{
    static final FloatArrayTemplate instance;
    
    private FloatArrayTemplate() {
    }
    
    @Override
    public void write(final Packer pk, final float[] target, final boolean required) throws IOException {
        if (target != null) {
            pk.writeArrayBegin(target.length);
            for (final float a : target) {
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
    public float[] read(final Unpacker u, float[] to, final boolean required) throws IOException {
        if (!required && u.trySkipNil()) {
            return null;
        }
        final int n = u.readArrayBegin();
        if (to == null || to.length != n) {
            to = new float[n];
        }
        for (int i = 0; i < n; ++i) {
            to[i] = u.readFloat();
        }
        u.readArrayEnd();
        return to;
    }
    
    public static FloatArrayTemplate getInstance() {
        return FloatArrayTemplate.instance;
    }
    
    static {
        instance = new FloatArrayTemplate();
    }
}
