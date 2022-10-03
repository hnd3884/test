package org.msgpack.template;

import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;

public class ByteArrayTemplate extends AbstractTemplate<byte[]>
{
    static final ByteArrayTemplate instance;
    
    private ByteArrayTemplate() {
    }
    
    @Override
    public void write(final Packer pk, final byte[] target, final boolean required) throws IOException {
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
    public byte[] read(final Unpacker u, final byte[] to, final boolean required) throws IOException {
        if (!required && u.trySkipNil()) {
            return null;
        }
        return u.readByteArray();
    }
    
    public static ByteArrayTemplate getInstance() {
        return ByteArrayTemplate.instance;
    }
    
    static {
        instance = new ByteArrayTemplate();
    }
}
