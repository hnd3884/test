package org.msgpack.template;

import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;

public class IntegerArrayTemplate extends AbstractTemplate<int[]>
{
    static final IntegerArrayTemplate instance;
    
    private IntegerArrayTemplate() {
    }
    
    @Override
    public void write(final Packer pk, final int[] target, final boolean required) throws IOException {
        if (target != null) {
            pk.writeArrayBegin(target.length);
            for (final int a : target) {
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
    public int[] read(final Unpacker u, final int[] to, final boolean required) throws IOException {
        if (!required && u.trySkipNil()) {
            return null;
        }
        final int n = u.readArrayBegin();
        int[] array;
        if (to != null && to.length == n) {
            array = to;
        }
        else {
            array = new int[n];
        }
        for (int i = 0; i < n; ++i) {
            array[i] = u.readInt();
        }
        u.readArrayEnd();
        return array;
    }
    
    public static IntegerArrayTemplate getInstance() {
        return IntegerArrayTemplate.instance;
    }
    
    static {
        instance = new IntegerArrayTemplate();
    }
}
