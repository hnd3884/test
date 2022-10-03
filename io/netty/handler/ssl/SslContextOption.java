package io.netty.handler.ssl;

import io.netty.util.Constant;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.ConstantPool;
import io.netty.util.AbstractConstant;

public class SslContextOption<T> extends AbstractConstant<SslContextOption<T>>
{
    private static final ConstantPool<SslContextOption<Object>> pool;
    
    public static <T> SslContextOption<T> valueOf(final String name) {
        return (SslContextOption)SslContextOption.pool.valueOf(name);
    }
    
    public static <T> SslContextOption<T> valueOf(final Class<?> firstNameComponent, final String secondNameComponent) {
        return (SslContextOption)SslContextOption.pool.valueOf(firstNameComponent, secondNameComponent);
    }
    
    public static boolean exists(final String name) {
        return SslContextOption.pool.exists(name);
    }
    
    private SslContextOption(final int id, final String name) {
        super(id, name);
    }
    
    protected SslContextOption(final String name) {
        this(SslContextOption.pool.nextId(), name);
    }
    
    public void validate(final T value) {
        ObjectUtil.checkNotNull(value, "value");
    }
    
    static {
        pool = new ConstantPool<SslContextOption<Object>>() {
            @Override
            protected SslContextOption<Object> newConstant(final int id, final String name) {
                return new SslContextOption<Object>(id, name, null);
            }
        };
    }
}
