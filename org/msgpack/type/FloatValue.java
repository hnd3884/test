package org.msgpack.type;

public abstract class FloatValue extends NumberValue
{
    @Override
    public ValueType getType() {
        return ValueType.FLOAT;
    }
    
    @Override
    public boolean isFloatValue() {
        return true;
    }
    
    @Override
    public FloatValue asFloatValue() {
        return this;
    }
    
    public abstract float getFloat();
    
    public abstract double getDouble();
}
