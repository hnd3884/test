package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class FloatSeqHolder implements Streamable
{
    public float[] value;
    
    public FloatSeqHolder() {
        this.value = null;
    }
    
    public FloatSeqHolder(final float[] value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = FloatSeqHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        FloatSeqHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return FloatSeqHelper.type();
    }
}
