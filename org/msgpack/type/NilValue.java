package org.msgpack.type;

import java.io.IOException;
import org.msgpack.packer.Packer;

public class NilValue extends AbstractValue
{
    private static NilValue instance;
    
    private NilValue() {
    }
    
    static NilValue getInstance() {
        return NilValue.instance;
    }
    
    @Override
    public ValueType getType() {
        return ValueType.NIL;
    }
    
    @Override
    public boolean isNilValue() {
        return true;
    }
    
    @Override
    public NilValue asNilValue() {
        return this;
    }
    
    @Override
    public String toString() {
        return "null";
    }
    
    @Override
    public StringBuilder toString(final StringBuilder sb) {
        return sb.append("null");
    }
    
    @Override
    public void writeTo(final Packer pk) throws IOException {
        pk.writeNil();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof Value && ((Value)o).isNilValue());
    }
    
    @Override
    public int hashCode() {
        return 0;
    }
    
    static {
        NilValue.instance = new NilValue();
    }
}
