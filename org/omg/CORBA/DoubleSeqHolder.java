package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class DoubleSeqHolder implements Streamable
{
    public double[] value;
    
    public DoubleSeqHolder() {
        this.value = null;
    }
    
    public DoubleSeqHolder(final double[] value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = DoubleSeqHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        DoubleSeqHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return DoubleSeqHelper.type();
    }
}
