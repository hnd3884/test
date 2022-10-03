package org.msgpack.type;

import java.io.IOException;
import org.msgpack.packer.Packer;

class FalseValueImpl extends AbstractBooleanValue
{
    private static FalseValueImpl instance;
    
    private FalseValueImpl() {
    }
    
    static FalseValueImpl getInstance() {
        return FalseValueImpl.instance;
    }
    
    @Override
    public boolean getBoolean() {
        return false;
    }
    
    @Override
    public void writeTo(final Packer pk) throws IOException {
        pk.write(false);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Value)) {
            return false;
        }
        final Value v = (Value)o;
        return v.isBooleanValue() && !v.asBooleanValue().getBoolean();
    }
    
    @Override
    public int hashCode() {
        return 1237;
    }
    
    @Override
    public String toString() {
        return "false";
    }
    
    @Override
    public StringBuilder toString(final StringBuilder sb) {
        return sb.append("false");
    }
    
    static {
        FalseValueImpl.instance = new FalseValueImpl();
    }
}
