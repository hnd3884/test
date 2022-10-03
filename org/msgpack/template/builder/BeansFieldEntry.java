package org.msgpack.template.builder;

import java.lang.reflect.InvocationTargetException;
import org.msgpack.MessageTypeException;
import java.lang.reflect.Type;
import org.msgpack.template.FieldOption;
import org.msgpack.template.builder.beans.PropertyDescriptor;

public class BeansFieldEntry extends FieldEntry
{
    protected PropertyDescriptor desc;
    
    public BeansFieldEntry() {
    }
    
    public BeansFieldEntry(final BeansFieldEntry e) {
        super(e.option);
        this.desc = e.getPropertyDescriptor();
    }
    
    public BeansFieldEntry(final PropertyDescriptor desc) {
        this(desc, FieldOption.DEFAULT);
    }
    
    public BeansFieldEntry(final PropertyDescriptor desc, final FieldOption option) {
        super(option);
        this.desc = desc;
    }
    
    public String getGetterName() {
        return this.getPropertyDescriptor().getReadMethod().getName();
    }
    
    public String getSetterName() {
        return this.getPropertyDescriptor().getWriteMethod().getName();
    }
    
    public PropertyDescriptor getPropertyDescriptor() {
        return this.desc;
    }
    
    @Override
    public String getName() {
        return this.getPropertyDescriptor().getDisplayName();
    }
    
    @Override
    public Class<?> getType() {
        return this.getPropertyDescriptor().getPropertyType();
    }
    
    @Override
    public Type getGenericType() {
        return this.getPropertyDescriptor().getReadMethod().getGenericReturnType();
    }
    
    @Override
    public Object get(final Object target) {
        try {
            return this.getPropertyDescriptor().getReadMethod().invoke(target, new Object[0]);
        }
        catch (final IllegalArgumentException e) {
            throw new MessageTypeException(e);
        }
        catch (final IllegalAccessException e2) {
            throw new MessageTypeException(e2);
        }
        catch (final InvocationTargetException e3) {
            throw new MessageTypeException(e3);
        }
    }
    
    @Override
    public void set(final Object target, final Object value) {
        try {
            this.getPropertyDescriptor().getWriteMethod().invoke(target, value);
        }
        catch (final IllegalArgumentException e) {
            throw new MessageTypeException(e);
        }
        catch (final IllegalAccessException e2) {
            throw new MessageTypeException(e2);
        }
        catch (final InvocationTargetException e3) {
            throw new MessageTypeException(e3);
        }
    }
}
