package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;

public class NullSafeAccessor<B, V, P> extends Accessor<B, V>
{
    private final Accessor<B, V> core;
    private final Lister<B, V, ?, P> lister;
    
    public NullSafeAccessor(final Accessor<B, V> core, final Lister<B, V, ?, P> lister) {
        super(core.getValueType());
        this.core = core;
        this.lister = lister;
    }
    
    @Override
    public V get(final B bean) throws AccessorException {
        V v = this.core.get(bean);
        if (v == null) {
            final P pack = this.lister.startPacking(bean, this.core);
            this.lister.endPacking(pack, bean, this.core);
            v = this.core.get(bean);
        }
        return v;
    }
    
    @Override
    public void set(final B bean, final V value) throws AccessorException {
        this.core.set(bean, value);
    }
}
