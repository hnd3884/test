package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class WStringSeqHolder implements Streamable
{
    public String[] value;
    
    public WStringSeqHolder() {
        this.value = null;
    }
    
    public WStringSeqHolder(final String[] value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = WStringSeqHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        WStringSeqHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return WStringSeqHelper.type();
    }
}
