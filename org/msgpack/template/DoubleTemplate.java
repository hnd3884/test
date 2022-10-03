package org.msgpack.template;

import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;

public class DoubleTemplate extends AbstractTemplate<Double>
{
    static final DoubleTemplate instance;
    
    private DoubleTemplate() {
    }
    
    @Override
    public void write(final Packer pk, final Double target, final boolean required) throws IOException {
        if (target != null) {
            pk.write((double)target);
            return;
        }
        if (required) {
            throw new MessageTypeException("Attempted to write null");
        }
        pk.writeNil();
    }
    
    @Override
    public Double read(final Unpacker u, final Double to, final boolean required) throws IOException {
        if (!required && u.trySkipNil()) {
            return null;
        }
        return u.readDouble();
    }
    
    public static DoubleTemplate getInstance() {
        return DoubleTemplate.instance;
    }
    
    static {
        instance = new DoubleTemplate();
    }
}
