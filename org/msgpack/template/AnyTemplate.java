package org.msgpack.template;

import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import java.lang.reflect.Type;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;

public class AnyTemplate<T> extends AbstractTemplate<T>
{
    private TemplateRegistry registry;
    
    public AnyTemplate(final TemplateRegistry registry) {
        this.registry = registry;
    }
    
    @Override
    public void write(final Packer pk, final T target, final boolean required) throws IOException {
        if (target == null) {
            if (required) {
                throw new MessageTypeException("Attempted to write null");
            }
            pk.writeNil();
        }
        else {
            this.registry.lookup(target.getClass()).write(pk, target);
        }
    }
    
    @Override
    public T read(final Unpacker u, final T to, final boolean required) throws IOException, MessageTypeException {
        if (!required && u.trySkipNil()) {
            return null;
        }
        if (to == null) {
            throw new MessageTypeException("convert into unknown type is invalid");
        }
        final T o = u.read(to);
        if (required && o == null) {
            throw new MessageTypeException("Unexpected nil value");
        }
        return o;
    }
}
