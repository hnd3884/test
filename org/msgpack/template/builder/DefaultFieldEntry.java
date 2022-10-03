package org.msgpack.template.builder;

import org.msgpack.MessageTypeException;
import java.lang.reflect.Type;
import org.msgpack.template.FieldOption;
import java.lang.reflect.Field;

public class DefaultFieldEntry extends FieldEntry
{
    protected Field field;
    
    public DefaultFieldEntry() {
        this(null, FieldOption.IGNORE);
    }
    
    public DefaultFieldEntry(final DefaultFieldEntry e) {
        this(e.field, e.option);
    }
    
    public DefaultFieldEntry(final Field field, final FieldOption option) {
        super(option);
        this.field = field;
    }
    
    public Field getField() {
        return this.field;
    }
    
    public void setField(final Field field) {
        this.field = field;
    }
    
    @Override
    public String getName() {
        return this.field.getName();
    }
    
    @Override
    public Class<?> getType() {
        return this.field.getType();
    }
    
    @Override
    public Type getGenericType() {
        return this.field.getGenericType();
    }
    
    @Override
    public Object get(final Object target) {
        try {
            return this.getField().get(target);
        }
        catch (final IllegalArgumentException e) {
            throw new MessageTypeException(e);
        }
        catch (final IllegalAccessException e2) {
            throw new MessageTypeException(e2);
        }
    }
    
    @Override
    public void set(final Object target, final Object value) {
        try {
            this.field.set(target, value);
        }
        catch (final IllegalArgumentException e) {
            throw new MessageTypeException(e);
        }
        catch (final IllegalAccessException e2) {
            throw new MessageTypeException(e2);
        }
    }
}
