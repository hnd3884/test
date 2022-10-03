package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class BooleanSeqHolder implements Streamable
{
    public boolean[] value;
    
    public BooleanSeqHolder() {
        this.value = null;
    }
    
    public BooleanSeqHolder(final boolean[] value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = BooleanSeqHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        BooleanSeqHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return BooleanSeqHelper.type();
    }
}
