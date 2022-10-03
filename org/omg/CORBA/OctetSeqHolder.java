package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class OctetSeqHolder implements Streamable
{
    public byte[] value;
    
    public OctetSeqHolder() {
        this.value = null;
    }
    
    public OctetSeqHolder(final byte[] value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = OctetSeqHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        OctetSeqHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return OctetSeqHelper.type();
    }
}
