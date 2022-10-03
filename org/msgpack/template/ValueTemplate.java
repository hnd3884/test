package org.msgpack.template;

import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;
import org.msgpack.type.Value;

public class ValueTemplate extends AbstractTemplate<Value>
{
    static final ValueTemplate instance;
    
    private ValueTemplate() {
    }
    
    @Override
    public void write(final Packer pk, final Value target, final boolean required) throws IOException {
        if (target != null) {
            target.writeTo(pk);
            return;
        }
        if (required) {
            throw new MessageTypeException("Attempted to write null");
        }
        pk.writeNil();
    }
    
    @Override
    public Value read(final Unpacker u, final Value to, final boolean required) throws IOException {
        if (!required && u.trySkipNil()) {
            return null;
        }
        return u.readValue();
    }
    
    public static ValueTemplate getInstance() {
        return ValueTemplate.instance;
    }
    
    static {
        instance = new ValueTemplate();
    }
}
