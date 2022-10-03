package org.msgpack.type;

import java.math.BigInteger;

public abstract class IntegerValue extends NumberValue
{
    @Override
    public ValueType getType() {
        return ValueType.INTEGER;
    }
    
    @Override
    public boolean isIntegerValue() {
        return true;
    }
    
    @Override
    public IntegerValue asIntegerValue() {
        return this;
    }
    
    public abstract byte getByte();
    
    public abstract short getShort();
    
    public abstract int getInt();
    
    public abstract long getLong();
    
    public BigInteger getBigInteger() {
        return this.bigIntegerValue();
    }
}
