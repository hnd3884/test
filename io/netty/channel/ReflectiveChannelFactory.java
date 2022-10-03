package io.netty.channel;

import io.netty.util.internal.StringUtil;
import io.netty.util.internal.ObjectUtil;
import java.lang.reflect.Constructor;

public class ReflectiveChannelFactory<T extends Channel> implements ChannelFactory<T>
{
    private final Constructor<? extends T> constructor;
    
    public ReflectiveChannelFactory(final Class<? extends T> clazz) {
        ObjectUtil.checkNotNull(clazz, "clazz");
        try {
            this.constructor = clazz.getConstructor((Class<?>[])new Class[0]);
        }
        catch (final NoSuchMethodException e) {
            throw new IllegalArgumentException("Class " + StringUtil.simpleClassName(clazz) + " does not have a public non-arg constructor", e);
        }
    }
    
    @Override
    public T newChannel() {
        try {
            return (T)this.constructor.newInstance(new Object[0]);
        }
        catch (final Throwable t) {
            throw new ChannelException("Unable to create Channel from class " + this.constructor.getDeclaringClass(), t);
        }
    }
    
    @Override
    public String toString() {
        return StringUtil.simpleClassName(ReflectiveChannelFactory.class) + '(' + StringUtil.simpleClassName(this.constructor.getDeclaringClass()) + ".class)";
    }
}
