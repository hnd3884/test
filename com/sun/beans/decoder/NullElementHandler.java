package com.sun.beans.decoder;

class NullElementHandler extends ElementHandler implements ValueObject
{
    @Override
    protected final ValueObject getValueObject() {
        return this;
    }
    
    @Override
    public Object getValue() {
        return null;
    }
    
    @Override
    public final boolean isVoid() {
        return false;
    }
}
