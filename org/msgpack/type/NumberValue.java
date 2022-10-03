package org.msgpack.type;

import java.math.BigInteger;
import org.msgpack.MessageTypeException;

public abstract class NumberValue extends Number implements Value
{
    @Override
    public boolean isNilValue() {
        return false;
    }
    
    @Override
    public boolean isBooleanValue() {
        return false;
    }
    
    @Override
    public boolean isIntegerValue() {
        return false;
    }
    
    @Override
    public boolean isFloatValue() {
        return false;
    }
    
    @Override
    public boolean isArrayValue() {
        return false;
    }
    
    @Override
    public boolean isMapValue() {
        return false;
    }
    
    @Override
    public boolean isRawValue() {
        return false;
    }
    
    @Override
    public NilValue asNilValue() {
        throw new MessageTypeException();
    }
    
    @Override
    public BooleanValue asBooleanValue() {
        throw new MessageTypeException();
    }
    
    @Override
    public IntegerValue asIntegerValue() {
        throw new MessageTypeException();
    }
    
    @Override
    public FloatValue asFloatValue() {
        throw new MessageTypeException();
    }
    
    @Override
    public ArrayValue asArrayValue() {
        throw new MessageTypeException();
    }
    
    @Override
    public MapValue asMapValue() {
        throw new MessageTypeException();
    }
    
    @Override
    public RawValue asRawValue() {
        throw new MessageTypeException();
    }
    
    public abstract BigInteger bigIntegerValue();
}
