package io.netty.util;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentMap;

public abstract class ConstantPool<T extends Constant<T>>
{
    private final ConcurrentMap<String, T> constants;
    private final AtomicInteger nextId;
    
    public ConstantPool() {
        this.constants = PlatformDependent.newConcurrentHashMap();
        this.nextId = new AtomicInteger(1);
    }
    
    public T valueOf(final Class<?> firstNameComponent, final String secondNameComponent) {
        return this.valueOf(ObjectUtil.checkNotNull(firstNameComponent, "firstNameComponent").getName() + '#' + ObjectUtil.checkNotNull(secondNameComponent, "secondNameComponent"));
    }
    
    public T valueOf(final String name) {
        return this.getOrCreate(ObjectUtil.checkNonEmpty(name, "name"));
    }
    
    private T getOrCreate(final String name) {
        T constant = this.constants.get(name);
        if (constant == null) {
            final T tempConstant = this.newConstant(this.nextId(), name);
            constant = this.constants.putIfAbsent(name, tempConstant);
            if (constant == null) {
                return tempConstant;
            }
        }
        return constant;
    }
    
    public boolean exists(final String name) {
        return this.constants.containsKey(ObjectUtil.checkNonEmpty(name, "name"));
    }
    
    public T newInstance(final String name) {
        return this.createOrThrow(ObjectUtil.checkNonEmpty(name, "name"));
    }
    
    private T createOrThrow(final String name) {
        T constant = this.constants.get(name);
        if (constant == null) {
            final T tempConstant = this.newConstant(this.nextId(), name);
            constant = this.constants.putIfAbsent(name, tempConstant);
            if (constant == null) {
                return tempConstant;
            }
        }
        throw new IllegalArgumentException(String.format("'%s' is already in use", name));
    }
    
    protected abstract T newConstant(final int p0, final String p1);
    
    @Deprecated
    public final int nextId() {
        return this.nextId.getAndIncrement();
    }
}
