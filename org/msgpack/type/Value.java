package org.msgpack.type;

import java.io.IOException;
import org.msgpack.packer.Packer;

public interface Value
{
    ValueType getType();
    
    boolean isNilValue();
    
    boolean isBooleanValue();
    
    boolean isIntegerValue();
    
    boolean isFloatValue();
    
    boolean isArrayValue();
    
    boolean isMapValue();
    
    boolean isRawValue();
    
    NilValue asNilValue();
    
    BooleanValue asBooleanValue();
    
    IntegerValue asIntegerValue();
    
    FloatValue asFloatValue();
    
    ArrayValue asArrayValue();
    
    MapValue asMapValue();
    
    RawValue asRawValue();
    
    void writeTo(final Packer p0) throws IOException;
    
    StringBuilder toString(final StringBuilder p0);
}
