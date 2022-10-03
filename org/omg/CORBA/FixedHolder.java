package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import java.math.BigDecimal;
import org.omg.CORBA.portable.Streamable;

public final class FixedHolder implements Streamable
{
    public BigDecimal value;
    
    public FixedHolder() {
    }
    
    public FixedHolder(final BigDecimal value) {
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = inputStream.read_fixed();
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        outputStream.write_fixed(this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ORB.init().get_primitive_tc(TCKind.tk_fixed);
    }
}
