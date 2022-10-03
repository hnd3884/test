package org.msgpack.template;

import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;

public class LongTemplate extends AbstractTemplate<Long>
{
    static final LongTemplate instance;
    
    private LongTemplate() {
    }
    
    @Override
    public void write(final Packer pk, final Long target, final boolean required) throws IOException {
        if (target != null) {
            pk.write((long)target);
            return;
        }
        if (required) {
            throw new MessageTypeException("Attempted to write null");
        }
        pk.writeNil();
    }
    
    @Override
    public Long read(final Unpacker u, final Long to, final boolean required) throws IOException {
        if (!required && u.trySkipNil()) {
            return null;
        }
        return u.readLong();
    }
    
    public static LongTemplate getInstance() {
        return LongTemplate.instance;
    }
    
    static {
        instance = new LongTemplate();
    }
}
