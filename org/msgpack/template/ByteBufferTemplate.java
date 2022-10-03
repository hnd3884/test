package org.msgpack.template;

import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;
import java.nio.ByteBuffer;

public class ByteBufferTemplate extends AbstractTemplate<ByteBuffer>
{
    static final ByteBufferTemplate instance;
    
    private ByteBufferTemplate() {
    }
    
    @Override
    public void write(final Packer pk, final ByteBuffer target, final boolean required) throws IOException {
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
    public ByteBuffer read(final Unpacker u, final ByteBuffer to, final boolean required) throws IOException {
        if (!required && u.trySkipNil()) {
            return null;
        }
        return u.readByteBuffer();
    }
    
    public static ByteBufferTemplate getInstance() {
        return ByteBufferTemplate.instance;
    }
    
    static {
        instance = new ByteBufferTemplate();
    }
}
