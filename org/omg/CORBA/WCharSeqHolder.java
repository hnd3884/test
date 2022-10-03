package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class WCharSeqHolder implements Streamable
{
    public char[] value;
    
    public WCharSeqHolder() {
        this.value = null;
    }
    
    public WCharSeqHolder(final char[] value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = WCharSeqHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        WCharSeqHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return WCharSeqHelper.type();
    }
}
