package org.msgpack.template;

import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;

public class FloatTemplate extends AbstractTemplate<Float>
{
    static final FloatTemplate instance;
    
    private FloatTemplate() {
    }
    
    @Override
    public void write(final Packer pk, final Float target, final boolean required) throws IOException {
        if (target != null) {
            pk.write((float)target);
            return;
        }
        if (required) {
            throw new MessageTypeException("Attempted to write null");
        }
        pk.writeNil();
    }
    
    @Override
    public Float read(final Unpacker u, final Float to, final boolean required) throws IOException {
        if (!required && u.trySkipNil()) {
            return null;
        }
        return u.readFloat();
    }
    
    public static FloatTemplate getInstance() {
        return FloatTemplate.instance;
    }
    
    static {
        instance = new FloatTemplate();
    }
}
