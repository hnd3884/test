package org.msgpack.template;

import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;

public class DoubleArrayTemplate extends AbstractTemplate<double[]>
{
    static final DoubleArrayTemplate instance;
    
    private DoubleArrayTemplate() {
    }
    
    @Override
    public void write(final Packer pk, final double[] target, final boolean required) throws IOException {
        if (target != null) {
            pk.writeArrayBegin(target.length);
            for (final double a : target) {
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
    public double[] read(final Unpacker u, double[] to, final boolean required) throws IOException {
        if (!required && u.trySkipNil()) {
            return null;
        }
        final int n = u.readArrayBegin();
        if (to == null || to.length != n) {
            to = new double[n];
        }
        for (int i = 0; i < n; ++i) {
            to[i] = u.readDouble();
        }
        u.readArrayEnd();
        return to;
    }
    
    public static DoubleArrayTemplate getInstance() {
        return DoubleArrayTemplate.instance;
    }
    
    static {
        instance = new DoubleArrayTemplate();
    }
}
