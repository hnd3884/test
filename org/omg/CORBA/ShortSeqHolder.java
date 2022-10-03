package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class ShortSeqHolder implements Streamable
{
    public short[] value;
    
    public ShortSeqHolder() {
        this.value = null;
    }
    
    public ShortSeqHolder(final short[] value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = ShortSeqHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        ShortSeqHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ShortSeqHelper.type();
    }
}
