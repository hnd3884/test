package org.msgpack.template;

import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;
import org.msgpack.MessagePackable;

public class MessagePackableTemplate extends AbstractTemplate<MessagePackable>
{
    private Class<?> targetClass;
    
    MessagePackableTemplate(final Class<?> targetClass) {
        this.targetClass = targetClass;
    }
    
    @Override
    public void write(final Packer pk, final MessagePackable target, final boolean required) throws IOException {
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
    public MessagePackable read(final Unpacker u, MessagePackable to, final boolean required) throws IOException {
        if (!required && u.trySkipNil()) {
            return null;
        }
        if (to == null) {
            try {
                to = (MessagePackable)this.targetClass.newInstance();
            }
            catch (final InstantiationException e) {
                throw new MessageTypeException(e);
            }
            catch (final IllegalAccessException e2) {
                throw new MessageTypeException(e2);
            }
        }
        to.readFrom(u);
        return to;
    }
}
