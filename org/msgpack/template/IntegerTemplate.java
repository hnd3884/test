package org.msgpack.template;

import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;

public class IntegerTemplate extends AbstractTemplate<Integer>
{
    static final IntegerTemplate instance;
    
    private IntegerTemplate() {
    }
    
    @Override
    public void write(final Packer pk, final Integer target, final boolean required) throws IOException {
        if (target != null) {
            pk.write((int)target);
            return;
        }
        if (required) {
            throw new MessageTypeException("Attempted to write null");
        }
        pk.writeNil();
    }
    
    @Override
    public Integer read(final Unpacker u, final Integer to, final boolean required) throws IOException {
        if (!required && u.trySkipNil()) {
            return null;
        }
        return u.readInt();
    }
    
    public static IntegerTemplate getInstance() {
        return IntegerTemplate.instance;
    }
    
    static {
        instance = new IntegerTemplate();
    }
}
