package org.msgpack.template;

import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;

public class ByteTemplate extends AbstractTemplate<Byte>
{
    static final ByteTemplate instance;
    
    private ByteTemplate() {
    }
    
    @Override
    public void write(final Packer pk, final Byte target, final boolean required) throws IOException {
        if (target != null) {
            pk.write((byte)target);
            return;
        }
        if (required) {
            throw new MessageTypeException("Attempted to write null");
        }
        pk.writeNil();
    }
    
    @Override
    public Byte read(final Unpacker u, final Byte to, final boolean required) throws IOException {
        if (!required && u.trySkipNil()) {
            return null;
        }
        return u.readByte();
    }
    
    public static ByteTemplate getInstance() {
        return ByteTemplate.instance;
    }
    
    static {
        instance = new ByteTemplate();
    }
}
