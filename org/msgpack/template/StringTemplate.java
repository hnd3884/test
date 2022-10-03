package org.msgpack.template;

import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;

public class StringTemplate extends AbstractTemplate<String>
{
    static final StringTemplate instance;
    
    private StringTemplate() {
    }
    
    @Override
    public void write(final Packer pk, final String target, final boolean required) throws IOException {
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
    public String read(final Unpacker u, final String to, final boolean required) throws IOException {
        if (!required && u.trySkipNil()) {
            return null;
        }
        return u.readString();
    }
    
    public static StringTemplate getInstance() {
        return StringTemplate.instance;
    }
    
    static {
        instance = new StringTemplate();
    }
}
