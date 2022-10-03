package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class UShortSeqHolder implements Streamable
{
    public short[] value;
    
    public UShortSeqHolder() {
        this.value = null;
    }
    
    public UShortSeqHolder(final short[] value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = UShortSeqHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        UShortSeqHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return UShortSeqHelper.type();
    }
}
