package com.sun.beans.decoder;

final class ValueObjectImpl implements ValueObject
{
    static final ValueObject NULL;
    static final ValueObject VOID;
    private Object value;
    private boolean isVoid;
    
    static ValueObject create(final Object o) {
        return (o != null) ? new ValueObjectImpl(o) : ValueObjectImpl.NULL;
    }
    
    private ValueObjectImpl() {
        this.isVoid = true;
    }
    
    private ValueObjectImpl(final Object value) {
        this.value = value;
    }
    
    @Override
    public Object getValue() {
        return this.value;
    }
    
    @Override
    public boolean isVoid() {
        return this.isVoid;
    }
    
    static {
        NULL = new ValueObjectImpl(null);
        VOID = new ValueObjectImpl();
    }
}
