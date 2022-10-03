package org.msgpack.type;

import org.msgpack.MessageTypeException;
import java.util.AbstractMap;

abstract class AbstractMapValue extends AbstractMap<Value, Value> implements MapValue
{
    @Override
    public ValueType getType() {
        return ValueType.MAP;
    }
    
    @Override
    public boolean isMapValue() {
        return true;
    }
    
    @Override
    public MapValue asMapValue() {
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
    public boolean isArrayValue() {
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
    public RawValue asRawValue() {
        throw new MessageTypeException();
    }
}
