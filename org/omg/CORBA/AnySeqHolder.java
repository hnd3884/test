package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class AnySeqHolder implements Streamable
{
    public Any[] value;
    
    public AnySeqHolder() {
        this.value = null;
    }
    
    public AnySeqHolder(final Any[] value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = AnySeqHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        AnySeqHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return AnySeqHelper.type();
    }
}
