package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class LongSeqHolder implements Streamable
{
    public int[] value;
    
    public LongSeqHolder() {
        this.value = null;
    }
    
    public LongSeqHolder(final int[] value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = LongSeqHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        LongSeqHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return LongSeqHelper.type();
    }
}
