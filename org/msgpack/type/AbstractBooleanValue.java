package org.msgpack.type;

abstract class AbstractBooleanValue extends AbstractValue implements BooleanValue
{
    @Override
    public ValueType getType() {
        return ValueType.BOOLEAN;
    }
    
    @Override
    public boolean isBooleanValue() {
        return true;
    }
    
    public boolean isTrue() {
        return this.getBoolean();
    }
    
    public boolean isFalse() {
        return !this.getBoolean();
    }
    
    @Override
    public BooleanValue asBooleanValue() {
        return this;
    }
}
