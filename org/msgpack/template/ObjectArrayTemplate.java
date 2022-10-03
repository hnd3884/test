package org.msgpack.template;

import java.lang.reflect.Array;
import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;

public class ObjectArrayTemplate extends AbstractTemplate
{
    protected Class componentClass;
    protected Template componentTemplate;
    
    public ObjectArrayTemplate(final Class componentClass, final Template componentTemplate) {
        this.componentClass = componentClass;
        this.componentTemplate = componentTemplate;
    }
    
    @Override
    public void write(final Packer packer, final Object v, final boolean required) throws IOException {
        if (v == null) {
            if (required) {
                throw new MessageTypeException("Attempted to write null");
            }
            packer.writeNil();
        }
        else {
            if (!(v instanceof Object[]) || !this.componentClass.isAssignableFrom(v.getClass().getComponentType())) {
                throw new MessageTypeException();
            }
            final Object[] array = (Object[])v;
            final int length = array.length;
            packer.writeArrayBegin(length);
            for (int i = 0; i < length; ++i) {
                this.componentTemplate.write(packer, array[i], required);
            }
            packer.writeArrayEnd();
        }
    }
    
    @Override
    public Object read(final Unpacker unpacker, final Object to, final boolean required) throws IOException {
        if (!required && unpacker.trySkipNil()) {
            return null;
        }
        final int length = unpacker.readArrayBegin();
        final Object[] array = (Object[])Array.newInstance(this.componentClass, length);
        for (int i = 0; i < length; ++i) {
            array[i] = this.componentTemplate.read(unpacker, null, required);
        }
        unpacker.readArrayEnd();
        return array;
    }
}
