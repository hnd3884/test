package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class ULongLongSeqHolder implements Streamable
{
    public long[] value;
    
    public ULongLongSeqHolder() {
        this.value = null;
    }
    
    public ULongLongSeqHolder(final long[] value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = ULongLongSeqHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        ULongLongSeqHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ULongLongSeqHelper.type();
    }
}
