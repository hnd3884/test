package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class LongLongSeqHolder implements Streamable
{
    public long[] value;
    
    public LongLongSeqHolder() {
        this.value = null;
    }
    
    public LongLongSeqHolder(final long[] value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = LongLongSeqHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        LongLongSeqHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return LongLongSeqHelper.type();
    }
}
