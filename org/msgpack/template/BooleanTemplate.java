package org.msgpack.template;

import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;

public class BooleanTemplate extends AbstractTemplate<Boolean>
{
    static final BooleanTemplate instance;
    
    private BooleanTemplate() {
    }
    
    @Override
    public void write(final Packer pk, final Boolean target, final boolean required) throws IOException {
        if (target != null) {
            pk.write((boolean)target);
            return;
        }
        if (required) {
            throw new MessageTypeException("Attempted to write null");
        }
        pk.writeNil();
    }
    
    @Override
    public Boolean read(final Unpacker u, final Boolean to, final boolean required) throws IOException {
        if (!required && u.trySkipNil()) {
            return null;
        }
        return u.readBoolean();
    }
    
    public static BooleanTemplate getInstance() {
        return BooleanTemplate.instance;
    }
    
    static {
        instance = new BooleanTemplate();
    }
}
