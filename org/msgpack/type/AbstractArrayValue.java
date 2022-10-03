package org.msgpack.type;

import org.msgpack.MessageTypeException;
import java.util.AbstractList;

abstract class AbstractArrayValue extends AbstractList<Value> implements ArrayValue
{
    @Override
    public ValueType getType() {
        return ValueType.ARRAY;
    }
    
    @Override
    public boolean isArrayValue() {
        return true;
    }
    
    @Override
    public ArrayValue asArrayValue() {
        return this;
    }
    
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
    public MapValue asMapValue() {
        throw new MessageTypeException();
    }
    
    @Override
    public RawValue asRawValue() {
        throw new MessageTypeException();
    }
}
