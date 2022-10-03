package org.msgpack.type;

import java.io.IOException;
import org.msgpack.packer.Packer;

class TrueValueImpl extends AbstractBooleanValue
{
    private static TrueValueImpl instance;
    
    private TrueValueImpl() {
    }
    
    static TrueValueImpl getInstance() {
        return TrueValueImpl.instance;
    }
    
    @Override
    public boolean getBoolean() {
        return true;
    }
    
    @Override
    public void writeTo(final Packer pk) throws IOException {
        pk.write(true);
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
        return v.isBooleanValue() && v.asBooleanValue().getBoolean();
    }
    
    @Override
    public int hashCode() {
        return 1231;
    }
    
    @Override
    public String toString() {
        return "true";
    }
    
    @Override
    public StringBuilder toString(final StringBuilder sb) {
        return sb.append("true");
    }
    
    static {
        TrueValueImpl.instance = new TrueValueImpl();
    }
}
