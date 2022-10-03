package org.msgpack.template;

import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;

public class ShortTemplate extends AbstractTemplate<Short>
{
    static final ShortTemplate instance;
    
    private ShortTemplate() {
    }
    
    @Override
    public void write(final Packer pk, final Short target, final boolean required) throws IOException {
        if (target != null) {
            pk.write(target);
            return;
        }
        if (required) {
            throw new MessageTypeException("Attempted to write null");
        }
        pk.writeNil();
    }
    
    @Override
    public Short read(final Unpacker u, final Short to, final boolean required) throws IOException {
        if (!required && u.trySkipNil()) {
            return null;
        }
        return u.readShort();
    }
    
    public static ShortTemplate getInstance() {
        return ShortTemplate.instance;
    }
    
    static {
        instance = new ShortTemplate();
    }
}
